package fr.omi.sevenwondersduel.material

import fr.omi.sevenwondersduel.effects.*
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoins
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPoints
import fr.omi.sevenwondersduel.material.Building.Type.MANUFACTURE
import fr.omi.sevenwondersduel.material.Building.Type.RAW_MATERIAL
import fr.omi.sevenwondersduel.material.Construction.Cost
import fr.omi.sevenwondersduel.material.Resource.*

sealed class Wonder : Construction

object CircusMaximus : Wonder() {
    override val cost = Cost(resources = mapOf(GLASS to 1, WOOD to 1, STONE to 2))
    override val effects = listOf(OpponentBuildingToDestroy(MANUFACTURE), Shield(1), VictoryPoints(3))
}

object Piraeus : Wonder() {
    override val cost = Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 2))
    override val effects = listOf(ProductionOfAny(Type.MANUFACTURED_GOOD), PlayAgain, VictoryPoints(2))
}

object TheAppianWay : Wonder() {
    override val cost = Cost(resources = mapOf(PAPYRUS to 1, CLAY to 2, STONE to 2))
    override val effects = listOf(TakeCoins(3), OpponentLosesCoins(3), PlayAgain, VictoryPoints(3))
}

object TheColossus : Wonder() {
    override val cost = Cost(resources = mapOf(GLASS to 1, CLAY to 3))
    override val effects = listOf(Shield(2), VictoryPoints(3))
}

object TheGreatLibrary : Wonder() {
    override val cost = Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, WOOD to 3))
    override val effects = listOf(ChooseGreatLibraryProgress, VictoryPoints(4))
}

object TheGreatLighthouse : Wonder() {
    override val cost = Cost(resources = mapOf(PAPYRUS to 2, STONE to 1, WOOD to 1))
    override val effects = listOf(ProductionOfAny(Type.RAW_GOOD), VictoryPoints(4))
}

object TheHangingGardens : Wonder() {
    override val cost = Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, WOOD to 2))
    override val effects = listOf(TakeCoins(6), PlayAgain, VictoryPoints(3))
}

object TheMausoleum : Wonder() {
    override val cost = Cost(resources = mapOf(PAPYRUS to 1, GLASS to 2, CLAY to 2))
    override val effects = listOf(DiscardedBuildingToBuild, VictoryPoints(2))
}

object ThePyramids : Wonder() {
    override val cost = Cost(resources = mapOf(PAPYRUS to 1, STONE to 3))
    override val effects = listOf(VictoryPoints(9))
}

object TheSphinx : Wonder() {
    override val cost = Cost(resources = mapOf(GLASS to 2, CLAY to 1, STONE to 1))
    override val effects = listOf(PlayAgain, VictoryPoints(6))
}

object TheStatueOfZeus : Wonder() {
    override val cost = Cost(resources = mapOf(PAPYRUS to 2, CLAY to 1, WOOD to 1, STONE to 1))
    override val effects = listOf(OpponentBuildingToDestroy(RAW_MATERIAL), Shield(1), VictoryPoints(3))
}

object TheTempleOfArtemis : Wonder() {
    override val cost = Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, STONE to 1, WOOD to 1))
    override val effects = listOf(TakeCoins(12), PlayAgain)
}

val wonders = setOf(CircusMaximus, Piraeus, TheAppianWay, TheColossus, TheGreatLibrary, TheGreatLighthouse,
        TheHangingGardens, TheMausoleum, ThePyramids, TheSphinx, TheStatueOfZeus, TheTempleOfArtemis)
