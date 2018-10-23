package fr.omi.sevenwondersduel.effects.takecoins

import fr.omi.sevenwondersduel.Game
import fr.omi.sevenwondersduel.effects.QuantifiableEffect

interface TakeCoinsEffect : QuantifiableEffect {
    override fun applyTo(game: Game): Game {
        return game.takeCoins(count(game, game.currentPlayer()))
    }
}