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
data class Shield(val quantity: Int) : Effect {
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

interface VictoryPointsEffect : Effect {
    fun count(game: SevenWondersDuel, player: Player): Int
}

class VictoryPoints(val quantity: Int) : VictoryPointsEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return quantity
    }
}

class VictoryPointsForMajority(val majorityCount: (Player) -> Int) : VictoryPointsEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return max(majorityCount(game.players.first), majorityCount(game.players.second))
    }

    constructor(vararg buildingTypes: BuildingType) : this({ player -> player.buildings.count { building -> buildingTypes.any { building.type == it } } })
}

class VictoryPointsForPlayer(val count: (Player) -> Int): VictoryPointsEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return count(player)
    }
}

interface Action

data class ChooseProgressToken(val tokens: Set<ProgressToken>) : Action