package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel

class OpponentLosesCoins(private val quantity: Int) : Effect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.pairInOrder(game.currentPlayer, game.opponent.loseCoins(quantity))
    }
}