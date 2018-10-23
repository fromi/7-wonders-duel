package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game

interface Effect {
    fun applyTo(game: Game): Game {
        return game
    }
}

