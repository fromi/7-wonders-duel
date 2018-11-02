package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel

enum class ScientificSymbol : Effect {
    MORTAR, PENDULUM, INKWELL, WHEEL, SUNDIAL, GYROSCOPE, BALANCE;

    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        val player = game.currentPlayer
        return when {
            player.effects.count { it == this } == 2 && game.progressTokensAvailable.isNotEmpty() -> game.copy(pendingActions = game.pendingActions.plus(ProgressTokenToChoose(game.progressTokensAvailable)))
            else -> game
        }
    }
}