package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Age.*
import fr.omi.sevenwondersduel.BuildingType.CIVILIAN
import fr.omi.sevenwondersduel.BuildingType.COMMERCIAL
import fr.omi.sevenwondersduel.Resource.Type
import fr.omi.sevenwondersduel.Resource.Type.MANUFACTURED_GOOD
import fr.omi.sevenwondersduel.Resource.Type.RAW_GOOD
import kotlin.math.absoluteValue

data class SevenWondersDuel(val players: Pair<Player, Player> = Pair(Player(), Player()),
                            val conflictPawnPosition: Int = 0,
                            val progressTokensAvailable: Set<ProgressToken> = ProgressToken.values().toList().shuffled().asSequence().take(5).toSet(),
                            val currentPlayer: Int? = 1,
                            val wondersAvailable: Set<Wonder> = Wonder.values().toList().shuffled().asSequence().take(4).toSet(),
                            val structure: List<Map<Int, Building>> = emptyList(),
                            val discardedCards: List<Building> = emptyList(),
                            val pendingActions: List<Action> = emptyList(),
                            val currentAge: Age = AGE_I) {

    fun choose(wonder: Wonder): SevenWondersDuel {
        var game = give(currentPlayer(), wonder)
        if (game.wondersAvailable.size == 3)
            game = game.swapCurrentPlayer()
        else if (game.wondersAvailable.size == 1) {
            game = game.give(opponent(), game.wondersAvailable.last())
            game = if (game.players.toList().all { it.wonders.size == 4 })
                game.prepare(AGE_I)
            else
                game.copy(wondersAvailable = remainingWonders().shuffled().asSequence().take(4).toSet())
        }
        return game
    }

    fun build(building: Building): SevenWondersDuel {
        var game = take(building)
        if (!currentPlayer().buildings.contains(building.freeLink)) {
            game = game.pay(building.cost)
        }
        return game.currentPlayerDo { it.build(building) }
                .applyEffects(building.effects)
                .continueGame()
    }

    fun discard(building: Building): SevenWondersDuel =
            take(building)
                    .currentPlayerDo { it.discard() }
                    .copy(discardedCards = discardedCards.plus(building))
                    .continueGame()

    fun build(wonder: Wonder, buildingUsed: Building): SevenWondersDuel =
            take(buildingUsed)
                    .pay(wonder.cost)
                    .currentPlayerDo { it.build(wonder, buildingUsed) }
                    .applyEffects(wonder.effects)
                    .discardIf7WondersBuilt()
                    .continueGame()

    fun letOpponentBegin(): SevenWondersDuel {
        check(structure.sumBy { it.size } == 20) { "You can only let the opponent begin when starting Age II or Age III" }
        check(if (currentPlayer == 1) conflictPawnPosition < 0 else conflictPawnPosition > 0) { "You can only let the opponent begin next Age when strictly weaker than him" }
        return copy(currentPlayer = if (currentPlayer == 1) 2 else 1)
    }

    fun choose(progressToken: ProgressToken): SevenWondersDuel {
        check(pendingActions.firstOrNull() is ChooseProgressToken) { "You are not currently allowed to choose a progress token" }
        require((pendingActions.first() as ChooseProgressToken).tokens.contains(progressToken)) { "You cannot choose this progress token" }
        return currentPlayerDo { it.take(progressToken) }
                .copy(progressTokensAvailable = progressTokensAvailable.minus(progressToken),
                        pendingActions = pendingActions.drop(1))
                .continueGame()
    }

    fun currentPlayer(): Player = when (currentPlayer) {
        1 -> players.first
        2 -> players.second
        else -> throw IllegalStateException("Game is over")
    }

    private fun opponent(): Player = when (currentPlayer) {
        1 -> players.second
        2 -> players.first
        else -> throw IllegalStateException("Game is over")
    }

    private fun swapCurrentPlayer(): SevenWondersDuel = copy(currentPlayer = when (currentPlayer) {
        1 -> 2
        2 -> 1
        else -> throw IllegalStateException("Game is over")
    })

    private fun currentPlayerDo(action: (Player) -> Player): SevenWondersDuel =
            pairInOrder(action(currentPlayer()), opponent())

    private fun pairInOrder(player: Player, opponent: Player): SevenWondersDuel =
            copy(players = when (currentPlayer) {
                1 -> Pair(first = player, second = opponent)
                2 -> Pair(first = opponent, second = player)
                else -> throw IllegalStateException("Game is over")
            })

    private fun give(player: Player, wonder: Wonder): SevenWondersDuel {
        require(wondersAvailable.contains(wonder)) { "This wonder is not available" }
        val players = when (player) {
            players.first -> players.copy(first = player.take(wonder))
            players.second -> players.copy(second = player.take(wonder))
            else -> throw IllegalArgumentException("This player is not in this game")
        }
        return copy(wondersAvailable = wondersAvailable.minus(wonder), players = players)
    }

    private fun remainingWonders() = Wonder.values().toList().filter { wonder -> !wondersAvailable.contains(wonder) && players.toList().none { player -> player.wonders.any { it.wonder == wonder } } }

    private fun prepare(age: Age): SevenWondersDuel {
        return copy(structure = createStructure(age), currentAge = age, currentPlayer = when {
            conflictPawnPosition < 0 -> 1
            conflictPawnPosition > 0 -> 2
            else -> currentPlayer
        })
    }

    private fun take(building: Building): SevenWondersDuel {
        require(accessibleBuildings().contains(building)) { "This building cannot be taken" }
        return copy(structure = structure.map { row -> row.minus(row.keys.filter { row[it] == building }) })
    }

    fun accessibleBuildings(): Collection<Building> {
        return structure.mapIndexed { index, row -> row.filterKeys { index == structure.size - 1 || !(structure[index + 1].contains(it - 1) || structure[index + 1].contains(it + 1)) }.values }.flatten()
    }

    private fun pay(cost: Cost): SevenWondersDuel {
        val player = currentPlayer().pay(cost, ::getTradingCost)
        val opponent = opponent() // if ECONOMY blablabla
        return pairInOrder(player, opponent)
    }

    private fun getTradingCost(resource: Resource): Int =
            if (currentPlayer().effects().any { it is FixTradingCostTo1 && it.resource == resource }) 1 else 2 + opponent().productionOf(resource)

    private fun continueGame(): SevenWondersDuel {
        return when {
            isOver() -> this
            currentAgeIsOver() -> when (currentAge) {
                AGE_I -> prepare(AGE_II)
                AGE_II -> prepare(AGE_III)
                else -> copy(currentPlayer = null)
            }
            pendingActions.isNotEmpty() -> this
            else -> copy(currentPlayer = if (currentPlayer == 1) 2 else 1)
        }
    }

    private fun currentAgeIsOver() = structure.flatMap { it.values }.isEmpty()

    fun isOver(): Boolean = currentPlayer == null

    private fun applyEffects(effects: List<Effect>): SevenWondersDuel {
        return if (effects.isEmpty()) this else effects.first().applyTo(this).applyEffects(effects.drop(1))
    }

    private fun discardIf7WondersBuilt(): SevenWondersDuel =
            if (players.toList().sumBy { player -> player.wonders.count { it.isBuild() } } == 7)
                SevenWondersDuel(players = players.copy(players.first.discardUnfinishedWonder(), players.second.discardUnfinishedWonder()))
            else this

    fun moveConflictPawn(quantity: Int): SevenWondersDuel {
        val newConflictPosition = if (currentPlayer == 1) minOf(conflictPawnPosition + quantity, 9) else maxOf(conflictPawnPosition - quantity, -9)
        return if (newConflictPosition.absoluteValue >= 9) copy(conflictPawnPosition = newConflictPosition, currentPlayer = null)
        else copy(conflictPawnPosition = newConflictPosition).lootMilitaryTokens()
    }

    private fun lootMilitaryTokens(): SevenWondersDuel {
        return when (conflictPawnPosition) {
            in -8..-6 -> if (players.first.militaryTokensLooted < 2) copy(players = players.copy(players.first.lootSecondToken(), players.second)) else this
            in -5..-3 -> if (players.first.militaryTokensLooted < 1) copy(players = players.copy(players.first.lootFirstToken(), players.second)) else this
            in 3..5 -> if (players.second.militaryTokensLooted < 2) copy(players = players.copy(players.first, players.second.lootFirstToken())) else this
            in 6..8 -> if (players.second.militaryTokensLooted < 2) copy(players = players.copy(players.first, players.second.lootSecondToken())) else this
            else -> this
        }
    }

    fun getWinner(): Player? {
        return when {
            conflictPawnPosition >= 9 -> players.first
            conflictPawnPosition <= -9 -> players.second
            players.first.countDifferentScientificSymbols() == 6 -> players.first
            players.second.countDifferentScientificSymbols() == 6 -> players.second
            currentAge == AGE_III && currentAgeIsOver() -> players.strictMaxBy { countVictoryPoint(it) }
                    ?: players.strictMaxBy { it.countCivilianBuildingsVictoryPoints() }
            else -> null
        }
    }

    fun countVictoryPoint(player: Player): Int {
        return militaryPoints(player) + player.effects().asSequence().filterIsInstance<VictoryPointsEffect>().sumBy { it.count(this, player) } + player.coins / 3
    }

    private fun militaryPoints(player: Player): Int {
        val relativeConflictPawnPosition = if (player == players.first) conflictPawnPosition else -conflictPawnPosition
        return when (relativeConflictPawnPosition) {
            in 1..2 -> 2
            in 3..5 -> 5
            in 6..8 -> 10
            else -> 0
        }
    }
}

data class Player(val militaryTokensLooted: Int = 0, val coins: Int = 7,
                  val wonders: List<BuildableWonder> = emptyList(),
                  val buildings: Set<Building> = emptySet(),
                  val progressTokens: Set<ProgressToken> = emptySet()) {
    fun take(wonder: Wonder): Player = copy(wonders = wonders.plus(BuildableWonder(wonder)))

    fun build(building: Building): Player = copy(buildings = buildings.plus(building))

    fun discard(): Player = copy(coins = coins + 2 + buildings.count { it.type == COMMERCIAL })

    fun build(wonder: Wonder, buildingUsed: Building): Player {
        require(wonders.any { it.wonder == wonder }) { "You do not have this wonder" }
        return copy(wonders = wonders.map { if (it.wonder == wonder) it.buildWith(buildingUsed) else it })
    }

    fun pay(cost: Cost, getTradingCost: (resource: Resource) -> Int): Player {
        val totalCost = cost.coins + sumTradingCost(cost.resources, getTradingCost)
        require(coins >= totalCost) { "Not enough coins" }
        return copy(coins = coins - totalCost)
    }

    private fun sumTradingCost(resourcesCost: Map<Resource, Int>, getTradingCost: (resource: Resource) -> Int): Int =
            sumTradingCostByType(RAW_GOOD, resourcesCost, getTradingCost) + sumTradingCostByType(MANUFACTURED_GOOD, resourcesCost, getTradingCost)

    private fun sumTradingCostByType(resourceType: Type, resourcesCost: Map<Resource, Int>, getTradingCost: (resource: Resource) -> Int): Int =
            resourcesCost.filter { it.key.type == resourceType }
                    .flatMap { entry -> List(entry.value - productionOf(entry.key)) { getTradingCost(entry.key) } }
                    .sorted().dropLast(effects().count { it is ProductionOfAny && it.resourceType == resourceType })
                    .sum()

    fun productionOf(resource: Resource): Int =
            effects().asSequence().filterIsInstance<Production>().filter { it.resource == resource }.sumBy { it.quantity }

    fun effects() = buildings.flatMap { it.effects }.asSequence()
            .plus(wonders.filter { it.isBuild() }.flatMap { it.wonder.effects })
            .plus(progressTokens.flatMap { it.effects }).toList()

    fun discardUnfinishedWonder(): Player {
        return copy(wonders = wonders.filter { it.isBuild() })
    }

    fun lootFirstToken(): Player {
        check(militaryTokensLooted < 1) { "First military token is already looted" }
        return copy(militaryTokensLooted = militaryTokensLooted + 1, coins = maxOf(coins - 2, 0))
    }

    fun lootSecondToken(): Player {
        check(militaryTokensLooted < 2) { "Second military token is already looted" }
        return if (militaryTokensLooted == 0) lootFirstToken().lootSecondToken() else copy(militaryTokensLooted = militaryTokensLooted + 1, coins = maxOf(coins - 5, 0))
    }

    fun count(scientificSymbol: ScientificSymbol): Int {
        return effects().count { it == scientificSymbol }
    }

    fun countDifferentScientificSymbols(): Int {
        return effects().asSequence().filterIsInstance<ScientificSymbol>().toSet().count()
    }

    fun take(progressToken: ProgressToken): Player {
        return copy(progressTokens = progressTokens.plus(progressToken))
    }

    fun countCivilianBuildingsVictoryPoints(): Int {
        return buildings.filter { it.type == CIVILIAN }.flatMap { it.effects }.asSequence().filterIsInstance<VictoryPoints>().sumBy { it.quantity }
    }
}

fun createStructure(age: Age): List<Map<Int, Building>> {
    val deck = when (age) {
        AGE_I, AGE_II -> Building.values().filter { it.deck == age }.shuffled()
        else -> {
            val threeRandomGuilds = Building.values().filter { it.deck == GUILDS }.shuffled().take(3)
            val age3BuildingsBut3 = Building.values().filter { it.deck == AGE_III }.shuffled().drop(3)
            age3BuildingsBut3.plus(threeRandomGuilds).shuffled()
        }
    }
    return createStructure(age, deck)
}

fun createStructure(age: Age, buildings: List<Building>): List<Map<Int, Building>> {
    val deck = buildings.toMutableList()
    val structureDescription = when (age) {
        AGE_I -> listOf(listOf(-1, 1), listOf(-2, 0, 2), listOf(-3, -1, 1, 3), listOf(-4, -2, 0, 2, 4), listOf(-5, -3, -1, 1, 3, 5))
        AGE_II -> listOf(listOf(-5, -3, -1, 1, 3, 5), listOf(-4, -2, 0, 2, 4), listOf(-3, -1, 1, 3), listOf(-2, 0, 2), listOf(-1, 1))
        else -> listOf(listOf(-1, 1), listOf(-2, 0, 2), listOf(-3, -1, 1, 3), listOf(-2, 2), listOf(-3, -1, 1, 3), listOf(-2, 0, 2), listOf(-1, 1))
    }
    return structureDescription.asSequence()
            .map {
                when (deck.size) {
                    0 -> emptyList()
                    in 1 until it.size -> it.take(deck.size)
                    else -> it
                }
            }.map { positions -> positions.associate { it to deck.removeAt(0) } }.toList()
}

data class BuildableWonder(val wonder: Wonder, val builtWith: Building? = null) {
    fun buildWith(building: Building): BuildableWonder {
        check(!isBuild()) { "This wonder is already built" }
        return copy(builtWith = building)
    }

    fun isBuild(): Boolean = builtWith != null
}

/**
 * Returns the element yielding the largest value of the given function or `null` if equals.
 */
fun <T, R : Comparable<R>> Pair<T, T>.strictMaxBy(selector: (T) -> R): T? {
    val firstValue = selector(first)
    val secondValue = selector(second)
    return when {
        firstValue > secondValue -> first
        firstValue < secondValue -> second
        else -> null
    }
}
