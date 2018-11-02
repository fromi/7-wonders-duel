package fr.omi.sevenwondersduel.effects.takecoins

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.effects.QuantifiableEffect

interface TakeCoinsEffect : QuantifiableEffect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.takeCoins(count(game, game.currentPlayer))
    }
}