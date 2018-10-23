package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game

data class Shield(val quantity: Int = 1) : Effect {
    override fun applyTo(game: Game): Game {
        return game.moveConflictPawn(quantity)
    }
}