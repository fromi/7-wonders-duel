package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.Building

data class DestroyOpponentBuilding(val type: Building.Type) : ChoiceEffect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return if (game.opponent().buildings.any { it.type == type }) super.applyTo(game) else game
    }
}