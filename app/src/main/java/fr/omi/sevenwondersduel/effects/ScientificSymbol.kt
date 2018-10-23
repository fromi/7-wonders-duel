package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel

enum class ScientificSymbol : Effect {
    MORTAR, PENDULUM, INKWELL, WHEEL, SUNDIAL, GYROSCOPE, BALANCE;

    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        val player = game.currentPlayer()
        return when {
            player.count(this) == 2 -> game.copy(pendingActions = game.pendingActions.plus(ChooseProgressToken(game.progressTokensAvailable)))
            player.countDifferentScientificSymbols() == 6 -> game.copy(currentPlayer = null)
            else -> game
        }
    }
}