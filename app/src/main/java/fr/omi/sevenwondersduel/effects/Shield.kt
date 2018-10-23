package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel

data class Shield(val quantity: Int = 1) : Effect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.moveConflictPawn(quantity)
    }
}