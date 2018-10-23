package fr.omi.sevenwondersduel.material

import fr.omi.sevenwondersduel.effects.*
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoins
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPoints
import fr.omi.sevenwondersduel.material.Building.Type.MANUFACTURE
import fr.omi.sevenwondersduel.material.Building.Type.RAW_MATERIAL
import fr.omi.sevenwondersduel.material.Construction.Cost
import fr.omi.sevenwondersduel.material.Resource.*

enum class Wonder(override val effects: List<Effect>, override val cost: Cost) : Construction {
    CIRCUS_MAXIMUS(listOf(DestroyOpponentBuilding(MANUFACTURE), Shield(1), VictoryPoints(3)), Cost(resources = mapOf(GLASS to 1, WOOD to 1, STONE to 2))),
    PIRAEUS(listOf(ProductionOfAny(Type.MANUFACTURED_GOOD), PlayAgain, VictoryPoints(2)), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 2))),
    THE_APPIAN_WAY(listOf(TakeCoins(3), OpponentLosesCoins(3), PlayAgain, VictoryPoints(3)), Cost(resources = mapOf(PAPYRUS to 1, CLAY to 2, STONE to 2))),
    THE_COLOSSUS(listOf(Shield(2), VictoryPoints(3)), Cost(resources = mapOf(GLASS to 1, CLAY to 3))),
    THE_GREAT_LIBRARY(listOf(ChooseGreatLibraryProgress, VictoryPoints(4)), Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, WOOD to 3))),
    THE_GREAT_LIGHTHOUSE(listOf(ProductionOfAny(Type.RAW_GOOD), VictoryPoints(4)), Cost(resources = mapOf(PAPYRUS to 2, STONE to 1, WOOD to 1))),
    THE_HANGING_GARDENS(listOf(TakeCoins(6), PlayAgain, VictoryPoints(3)), Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, WOOD to 2))),
    THE_MAUSOLEUM(listOf(BuildDiscarded, VictoryPoints(2)), Cost(resources = mapOf(PAPYRUS to 1, GLASS to 2, CLAY to 2))),
    THE_PYRAMIDS(listOf(VictoryPoints(9)), Cost(resources = mapOf(PAPYRUS to 1, STONE to 3))),
    THE_SPHINX(listOf(PlayAgain, VictoryPoints(6)), Cost(resources = mapOf(GLASS to 2, CLAY to 1, STONE to 1))),
    THE_STATUE_OF_ZEUS(listOf(DestroyOpponentBuilding(RAW_MATERIAL), Shield(1), VictoryPoints(3)), Cost(resources = mapOf(PAPYRUS to 2, CLAY to 1, WOOD to 1, STONE to 1))),
    THE_TEMPLE_OF_ARTEMIS(listOf(TakeCoins(12), PlayAgain), Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, STONE to 1, WOOD to 1)))
}