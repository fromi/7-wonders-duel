package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game

class OpponentLosesCoins(private val quantity: Int) : Effect {
    override fun applyTo(game: Game): Game {
        return game.pairInOrder(game.currentPlayer(), game.opponent().loseCoins(quantity))
    }
}