package fr.omi.sevenwondersduel.material

import fr.omi.sevenwondersduel.effects.*
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoins
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPoints
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPointsForPlayer

enum class ProgressToken(val effects: List<Effect>) {
    AGRICULTURE(listOf(TakeCoins(6), VictoryPoints(4))),
    ARCHITECTURE(listOf(ResourcesRebate(2) { it is Wonder })),
    ECONOMY(listOf(GainTradingCost)),
    LAW(listOf(ScientificSymbol.BALANCE)),
    MASONRY(listOf(ResourcesRebate(2) { it is CivilianBuilding })),
    MATHEMATICS(listOf(VictoryPointsForPlayer { it.progressTokens.size * 3 })),
    PHILOSOPHY(listOf(VictoryPoints(7))),
    STRATEGY(listOf(ConstructionTriggeredEffect(Shield()) { it is MilitaryBuilding })),
    THEOLOGY(listOf(ConstructionTriggeredEffect(PlayAgain) { it is Wonder })),
    URBANISM(listOf(TakeCoins(6), ChainBuildingTriggeredEffect(TakeCoins(4))))
}