package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.*
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPointsEffect
import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Age.*
import kotlin.math.absoluteValue

data class SevenWondersDuel(val players: Pair<Player, Player> = Pair(Player(), Player()),
                            val conflictPawnPosition: Int = 0,
                            val progressTokensAvailable: Set<ProgressToken> = ProgressToken.values().toList().shuffled().asSequence().take(5).toSet(),
                            val currentPlayer: Int? = 1,
                            val wondersAvailable: Set<Wonder> = wonders.shuffled().take(4).toSet(),
                            val structure: Structure? = null,
                            val discardedCards: List<Building> = emptyList(),
                            val pendingActions: List<PendingAction> = emptyList()) {

    fun choose(wonder: Wonder): SevenWondersDuel {
        var game = give(currentPlayer(), wonder)
        if (game.wondersAvailable.size == 3)
            game = game.swapCurrentPlayer()
        else if (game.wondersAvailable.size == 1) {
            game = game.give(opponent(), game.wondersAvailable.last())
            game = if (game.players.toList().all { it.wonders.size == 4 })
                game.copy(structure = Structure(AGE_I))
            else
                game.copy(wondersAvailable = remainingWonders().shuffled().asSequence().take(4).toSet())
        }
        return game
    }

    fun build(building: Building): SevenWondersDuel {
        val game = if (pendingActions.firstOrNull() == DiscardedBuildingToBuild) {
            require(discardedCards.contains(building))
            copy(discardedCards = discardedCards.minus(building), pendingActions = pendingActions.drop(1))
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

    private fun remainingWonders() = wonders.filter { wonder -> !wondersAvailable.contains(wonder) && players.toList().none { player -> player.wonders.any { it.wonder == wonder } } }

    private fun prepareNextAge(age: Age): SevenWondersDuel {
        val playerChoosingWhoBeginsNextAge = when {
            conflictPawnPosition < 0 -> 1
            conflictPawnPosition > 0 -> 2
            else -> currentPlayer
        }
        return copy(structure = Structure(age), currentPlayer = playerChoosingWhoBeginsNextAge, pendingActions = listOf(PlayerBeginningAgeToChoose))
    }

    private fun take(building: Building): SevenWondersDuel {
        check(pendingActions.isEmpty()) { "At least one pending action must be done before a new building can be taken" }
        return copy(structure = checkNotNull(structure).take(building))
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
            currentAgeIsOver() -> when (structure?.age) {
                AGE_I -> prepareNextAge(AGE_II)
                AGE_II -> prepareNextAge(AGE_III)
                else -> copy(currentPlayer = null)
            }
            else -> copy(currentPlayer = if (currentPlayer == 1) 2 else 1)
        }
    }

    fun currentAgeIsOver() = structure?.isEmpty() ?: false

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
            in 3..5 -> if (players.second.militaryTokensLooted < 1) copy(players = players.copy(players.first, players.second.lootFirstToken())) else this
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
            structure?.age == AGE_III && currentAgeIsOver() -> getHighest(players, ::countVictoryPoint)
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
}
