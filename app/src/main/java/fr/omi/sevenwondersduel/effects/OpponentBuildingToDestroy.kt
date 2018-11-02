package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.Building

interface OpponentBuildingToDestroy : PendingEffect {
    fun isEligible(building: Building): Boolean

    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return if (game.opponent().buildings.any(::isEligible)) super.applyTo(game) else game
    }
}