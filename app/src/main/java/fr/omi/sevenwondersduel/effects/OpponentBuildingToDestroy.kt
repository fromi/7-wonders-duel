package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.ManufactureBuilding
import fr.omi.sevenwondersduel.material.RawMaterialBuilding

interface OpponentBuildingToDestroy : PendingEffect {
    fun isEligible(building: Building): Boolean

    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return if (game.opponent.buildings.any(::isEligible)) super.applyTo(game) else game
    }
}

object OpponentRawMaterialBuildingToDestroy : OpponentBuildingToDestroy {
    override fun isEligible(building: Building): Boolean = building is RawMaterialBuilding
}

object OpponentManufactureBuildingToDestroy : OpponentBuildingToDestroy {
    override fun isEligible(building: Building): Boolean = building is ManufactureBuilding
}