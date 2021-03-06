package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.Production
import fr.omi.sevenwondersduel.effects.ProductionOfAny
import fr.omi.sevenwondersduel.effects.ResourcesRebate
import fr.omi.sevenwondersduel.effects.ScientificSymbol
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPoints
import fr.omi.sevenwondersduel.material.*
import kotlin.math.max

data class Player(val militaryTokensLooted: Int = 0, val coins: Int = 7,
                  val wonders: List<PlayerWonder> = emptyList(),
                  val buildings: Set<Building> = emptySet(),
                  val progressTokens: Set<ProgressToken> = emptySet()) {

    fun take(wonder: Wonder): Player = copy(wonders = wonders.plus(PlayerWonder(wonder)))

    fun construct(building: Building): Player = copy(buildings = buildings.plus(building))

    fun discard(): Player = copy(coins = coins + 2 + buildings.count { it is CommercialBuilding })

    fun construct(wonder: Wonder, buildingUsed: Building): Player {
        require(wonders.any { it.wonder == wonder }) { "You do not have this wonder" }
        return copy(wonders = wonders.map { if (it.wonder == wonder) it.constructWith(buildingUsed) else it })
    }

    fun pay(construction: Construction, getTradingCost: (resource: Resource) -> Int): Player {
        val totalCost = construction.cost.coins + sumTradingCost(construction, getTradingCost)
        require(coins >= totalCost) { "Not enough coins" }
        return copy(coins = coins - totalCost)
    }

    fun sumTradingCost(construction: Construction, getTradingCost: (resource: Resource) -> Int): Int =
            Resource.Type.values().flatMap { listTradingCostByType(it, construction.cost, getTradingCost) }
                    .sorted().dropLast(effects.filterIsInstance<ResourcesRebate>().filter { it.appliesTo(construction) }.toList().sumBy { it.quantity })
                    .sum()

    private fun listTradingCostByType(resourceType: Resource.Type, cost: Construction.Cost, getTradingCost: (resource: Resource) -> Int): List<Int> =
            cost.resources.filter { it.key.type == resourceType }
                    .flatMap { entry -> List(max(entry.value - productionOf(entry.key), 0)) { getTradingCost(entry.key) } }
                    .sorted().dropLast(effects.count { it is ProductionOfAny && it.resourceType == resourceType })

    fun productionOf(resource: Resource): Int = effects.filterIsInstance<Production>().filter { it.resource == resource }.sumBy { it.quantity }

    val effects
        get() = buildings.flatMap { it.effects }.asSequence()
                .plus(wonders.filter { it.isConstructed() }.flatMap { it.wonder.effects })
                .plus(progressTokens.flatMap { it.effects })

    fun discardUnfinishedWonder(): Player = copy(wonders = wonders.filter { it.isConstructed() })

    fun lootFirstToken(): Player {
        check(militaryTokensLooted < 1) { "First military token is already looted" }
        return loseCoins(2).copy(militaryTokensLooted = militaryTokensLooted + 1)
    }

    fun lootSecondToken(): Player {
        check(militaryTokensLooted < 2) { "Second military token is already looted" }
        return if (militaryTokensLooted == 0) lootFirstToken().lootSecondToken()
        else loseCoins(5).copy(militaryTokensLooted = militaryTokensLooted + 1)
    }

    fun loseCoins(quantity: Int): Player = copy(coins = maxOf(coins - quantity, 0))

    val hasScientificSupremacy: Boolean get() = effects.filterIsInstance<ScientificSymbol>().toSet().count() == 6

    fun take(progressToken: ProgressToken): Player = copy(progressTokens = progressTokens.plus(progressToken))

    fun takeCoins(quantity: Int): Player = copy(coins = coins + quantity)

    fun destroy(building: Building): Player {
        require(buildings.contains(building))
        return copy(buildings = buildings.minus(building))
    }

    fun countCivilianBuildingsVictoryPoints(): Int =
            buildings.filterIsInstance<CivilianBuilding>().flatMap { it.effects }.asSequence().filterIsInstance<VictoryPoints>().sumBy { it.quantity }
}