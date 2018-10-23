package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel

interface Effect {
    fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game
    }
}

