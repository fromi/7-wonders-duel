package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.*
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPointsEffect
import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building.Deck.Guilds
import kotlin.math.abs
import kotlin.math.absoluteValue

data class SevenWondersDuel(val players: Pair<Player, Player> = Pair(Player(), Player()),
                            val conflictPawnPosition: Int = 0,
                            val progressTokensAvailable: Set<ProgressToken> = ProgressToken.values().toList().shuffled().asSequence().take(5).toSet(),
                            val currentPlayer: Int? = 1,
                            val wondersAvailable: Set<Wonder> = Wonder.values().toList().shuffled().asSequence().take(4).toSet(),
                            val structure: List<Map<Int, Building>> = emptyList(),
                            val discardedCards: List<Building> = emptyList(),
                            val pendingActions: List<PendingAction> = emptyList(),
                            val currentAge: Age = AGE_I) {

    fun choose(wonder: Wonder): SevenWondersDuel {
        var game = give(currentPlayer(), wonder)
        if (game.wondersAvailable.size == 3)
            game = game.swapCurrentPlayer()
        else if (game.wondersAvailable.size == 1) {
            game = game.give(opponent(), game.wondersAvailable.last())
            game = if (game.players.toList().all { it.wonders.size == 4 })
                game.copy(structure = SevenWondersDuel.createStructure(AGE_I), currentAge = AGE_I)
            else
                game.copy(wondersAvailable = remainingWonders().shuffled().asSequence().take(4).toSet())
        }
        return game
    }

    fun build(building: Building): SevenWondersDuel {
        val game = if (pendingActions.firstOrNull() == DiscardedBuildingToBuild) {
            require(discardedCards.contains(building))
            copy(discardedCards = discardedCards.minus(building))
        } else {
            takeAndPay(building)
        }
        return game.currentPlayerDo { it.build(building) }
                .applyEffects(building)
                .continueGame()
    }

    private fun takeAndPay(building: Building): SevenWondersDuel {
        val game = take(building)
        return if (currentPlayer().buildings.contains(building.freeLink)) {
            game.applyEffects(currentPlayer().effects().filterIsInstance<ChainBuildingTriggeredEffect>().map { it.triggeredEffect }.toList())
        } else {
            game.pay(building)
        }
    }

    fun discard(building: Building): SevenWondersDuel =
            take(building)
                    .currentPlayerDo { it.discard() }
                    .copy(discardedCards = discardedCards.plus(building))
                    .continueGame()

    fun build(wonder: Wonder, buildingUsed: Building): SevenWondersDuel =
            take(buildingUsed)
                    .pay(wonder)
                    .currentPlayerDo { it.build(wonder, buildingUsed) }
                    .applyEffects(wonder)
                    .discardIf7WondersBuilt()
                    .continueGame()

    fun choosePlayerBeginningNextAge(player: Int): SevenWondersDuel {
        check(pendingActions.firstOrNull() is PlayerBeginningAgeToChoose)
        return copy(currentPlayer = player, pendingActions = pendingActions.drop(1))
    }

    fun choose(progressToken: ProgressToken): SevenWondersDuel {
        val chooseProgressToken = checkNotNull(pendingActions.firstOrNull() as? ProgressTokenToChoose) { "You are not currently allowed to choose a progress token" }
        require(chooseProgressToken.tokens.contains(progressToken)) { "You cannot choose this progress token" }
        return currentPlayerDo { it.take(progressToken) }
                .copy(progressTokensAvailable = progressTokensAvailable.minus(progressToken),
                        pendingActions = pendingActions.minus(chooseProgressToken))
                .applyEffects(progressToken.effects)
                .continueGame()
    }

    fun destroy(building: Building): SevenWondersDuel {
        val destroyOpponentBuilding = checkNotNull(pendingActions.firstOrNull() as? OpponentBuildingToDestroy) { "You are not currently allowed to destroy an opponent building" }
        require(building.type == destroyOpponentBuilding.type) { "You are not allowed to destroy this kind of building" }
        return pairInOrder(currentPlayer(), opponent().destroy(building))
                .copy(pendingActions = pendingActions.minus(destroyOpponentBuilding), discardedCards = discardedCards.plus(building))
                .continueGame()
    }

    fun currentPlayer(): Player = when (currentPlayer) {
        1 -> players.first
        2 -> players.second
        else -> throw IllegalStateException("Game is over")
    }

    fun opponent(): Player = when (currentPlayer) {
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

    fun pairInOrder(player: Player, opponent: Player): SevenWondersDuel =
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

    private fun prepareNextAge(age: Age): SevenWondersDuel {
        val playerChoosingWhoBeginsNextAge = when {
            conflictPawnPosition < 0 -> 1
            conflictPawnPosition > 0 -> 2
            else -> currentPlayer
        }
        return copy(structure = SevenWondersDuel.createStructure(age), currentAge = age, currentPlayer = playerChoosingWhoBeginsNextAge, pendingActions = listOf(PlayerBeginningAgeToChoose))
    }

    private fun take(building: Building): SevenWondersDuel {
        check(pendingActions.isEmpty()) { "At least one pending action must be done before a new building can be taken" }
        require(isAccessible(building)) { "This building cannot be taken" }
        return copy(structure = structure.map { row -> row.minus(row.keys.filter { row[it] == building }) })
    }

    fun isAccessible(building: Building): Boolean {
        val remainingAccessibleColumns = (if (currentAge == AGE_III) (-3..3) else (-5..5)).toMutableSet()
        var row = structure.size - 1
        while (remainingAccessibleColumns.isNotEmpty() && row >= 0) {
            structure[row].filterKeys { remainingAccessibleColumns.contains(it) }.forEach { entry ->
                if (entry.value == building) return true
                else remainingAccessibleColumns.removeAll { abs(entry.key - it) <= 1 }
            }
            row--
        }
        return false
    }

    private fun pay(construction: Construction): SevenWondersDuel {
        val player = currentPlayer().pay(construction, ::getTradingCost)
        val opponent = if (opponent().effects().any { it is GainTradingCost })
            opponent().takeCoins(currentPlayer().sumTradingCost(construction, ::getTradingCost))
        else opponent()
        return pairInOrder(player, opponent)
    }

    fun coinsToPay(construction: Construction): Int {
        return construction.cost.coins + currentPlayer().sumTradingCost(construction, ::getTradingCost)
    }

    private fun getTradingCost(resource: Resource): Int =
            if (currentPlayer().effects().any { it is FixTradingCostTo1 && it.resource == resource }) 1 else 2 + opponent().productionOf(resource)

    private fun continueGame(): SevenWondersDuel {
        return when {
            isOver() -> this
            pendingActions.isNotEmpty() -> if (pendingActions.first() == PlayAgain) copy(pendingActions = pendingActions.drop(1)) else this
            currentAgeIsOver() -> when (currentAge) {
                AGE_I -> prepareNextAge(AGE_II)
                AGE_II -> prepareNextAge(AGE_III)
                else -> copy(currentPlayer = null)
            }
            else -> copy(currentPlayer = if (currentPlayer == 1) 2 else 1)
        }
    }

    fun currentAgeIsOver() = structure.flatMap { it.values }.isEmpty()

    fun isOver(): Boolean = currentPlayer == null

    private fun applyEffects(construction: Construction): SevenWondersDuel {
        val triggeredEffects = currentPlayer().effects().filterIsInstance<ConstructionTriggeredEffect>().filter { it.appliesTo(construction) }.map { it.triggeredEffect }
        return applyEffects(construction.effects.plus(triggeredEffects))
    }

    private fun applyEffects(effects: Collection<Effect>): SevenWondersDuel {
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

    fun takeCoins(quantity: Int): SevenWondersDuel {
        return pairInOrder(currentPlayer().takeCoins(quantity), opponent())
    }

    fun getWinner(): Player? {
        return when {
            conflictPawnPosition >= 9 -> players.first
            conflictPawnPosition <= -9 -> players.second
            players.first.countDifferentScientificSymbols() == 6 -> players.first
            players.second.countDifferentScientificSymbols() == 6 -> players.second
            currentAge == AGE_III && currentAgeIsOver() -> getHighest(players, ::countVictoryPoint)
                    ?: getHighest(players, Player::countCivilianBuildingsVictoryPoints)
            else -> null
        }
    }

    private fun getHighest(players: Pair<Player, Player>, selector: (Player) -> Int): Player? {
        val firstValue = selector(players.first)
        val secondValue = selector(players.second)
        return when {
            firstValue > secondValue -> players.first
            firstValue < secondValue -> players.second
            else -> null
        }
    }

    fun countVictoryPoint(player: Player): Int {
        return militaryPoints(player) + player.effects().filterIsInstance<VictoryPointsEffect>().sumBy { it.count(this, player) } + player.coins / 3
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

    companion object {
        fun createStructure(age: Age): List<Map<Int, Building>> {
            val deck = when (age) {
                AGE_I, AGE_II -> Building.values().filter { it.deck == age }.shuffled()
                else -> {
                    val threeRandomGuilds = Building.values().filter { it.deck == Guilds }.shuffled().take(3)
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
                AGE_III -> listOf(listOf(-1, 1), listOf(-2, 0, 2), listOf(-3, -1, 1, 3), listOf(-2, 2), listOf(-3, -1, 1, 3), listOf(-2, 0, 2), listOf(-1, 1))
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
    }
}
