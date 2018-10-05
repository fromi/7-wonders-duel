package fr.omi.sevenwondersduel

import kotlin.math.max

interface Effect {
    fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game
    }
}

data class Production(val resource: Resource, val quantity: Int = 1) : Effect
data class FixTradingCostTo1(val resource: Resource) : Effect
data class ProductionOfAny(val resourceType: Resource.Type) : Effect
data class Shield(val quantity: Int = 1) : Effect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.moveConflictPawn(quantity)
    }
}

enum class ScientificSymbol : Effect {
    MORTAR, PENDULUM, INKWELL, WHEEL, SUNDIAL, GYROSCOPE, BALANCE;

    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        val player = game.currentPlayer()
        return when {
            player.count(this) == 2 -> game.copy(pendingActions = game.pendingActions.plus(ChooseProgressToken(game.progressTokensAvailable)))
            player.countDifferentScientificSymbols() == 6 -> game.copy(currentPlayer = null)
            else -> game
        }
    }
}

interface QuantifiableEffect : Effect {
    fun count(game: SevenWondersDuel, player: Player): Int
}

open class FixQuantityEffect(val quantity: Int) : QuantifiableEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return quantity
    }
}

open class MajorityEffect(val count: (Player) -> Int) : QuantifiableEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return max(count(game.players.first), count(game.players.second))
    }
}

open class MajorityBuildingsEffect(vararg buildingTypes: BuildingType) : MajorityEffect({ player -> player.buildings.count { building -> buildingTypes.any { building.type == it } } })

open class QuantifiablePlayerEffect(val count: (Player) -> Int) : QuantifiableEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return count(player)
    }
}

interface VictoryPointsEffect : QuantifiableEffect
class VictoryPoints(quantity: Int) : VictoryPointsEffect, FixQuantityEffect(quantity)
class VictoryPointsForMajority(majorityCount: (Player) -> Int) : VictoryPointsEffect, MajorityEffect(majorityCount)
class VictoryPointsForMajorityBuildings(vararg buildingTypes: BuildingType) : VictoryPointsEffect, MajorityBuildingsEffect(*buildingTypes)
class VictoryPointsForPlayer(count: (Player) -> Int) : VictoryPointsEffect, QuantifiablePlayerEffect(count)

interface TakeCoinsEffect : QuantifiableEffect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.takeCoins(count(game, game.currentPlayer()))
    }
}

class TakeCoins(quantity: Int) : TakeCoinsEffect, FixQuantityEffect(quantity)
class TakeCoinsForMajorityBuildings(vararg buildingTypes: BuildingType) : TakeCoinsEffect, MajorityBuildingsEffect(*buildingTypes)
class TakeCoinsForPlayer(count: (Player) -> Int) : TakeCoinsEffect, QuantifiablePlayerEffect(count)
class TakeCoinsForPlayerBuildings(buildingType: BuildingType, quantity: Int = 1) : TakeCoinsEffect, QuantifiablePlayerEffect({ player -> quantity * player.buildings.count { it.type == buildingType } })

class OpponentLosesCoins(private val quantity: Int) : Effect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.pairInOrder(game.currentPlayer(), game.opponent().loseCoins(quantity))
    }
}

interface Action
data class ChooseProgressToken(val tokens: Set<ProgressToken>) : Action

interface ChoiceEffect : Effect, Action {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.copy(pendingActions = game.pendingActions.plus(this))
    }
}

object PlayAgain : ChoiceEffect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return if (!game.currentAgeIsOver() && !game.pendingActions.contains(PlayAgain)) super.applyTo(game) else game
    }
}

data class DestroyOpponentBuilding(val type: BuildingType) : ChoiceEffect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return if (game.opponent().buildings.any { it.type == type }) super.applyTo(game) else game
    }
}

object ChooseGreatLibraryProgress : Effect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        val tokens = ProgressToken.values().asSequence()
                .minus(game.progressTokensAvailable).minus(game.players.first.progressTokens).minus(game.players.second.progressTokens)
                .toList().shuffled().asSequence().take(3).toSet()
        return game.copy(pendingActions = game.pendingActions.plus(ChooseProgressToken(tokens = tokens)))
    }
}

object BuildDiscarded : ChoiceEffect

class ResourcesRebate(val quantity: Int, val appliesTo: (Construction) -> Boolean) : Effect

object GainTradingCost : Effect

class ConstructionTriggeredEffect(val triggeredEffect: Effect, val appliesTo: (Construction) -> Boolean) : Effect
class ChainBuildingTriggeredEffect(val triggeredEffect: Effect) : Effect