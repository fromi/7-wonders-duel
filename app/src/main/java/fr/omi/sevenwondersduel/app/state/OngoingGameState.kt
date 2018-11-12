package fr.omi.sevenwondersduel.app.state

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.app.GameActivity
import kotlinx.android.synthetic.main.activity_game.*

abstract class OngoingGameState(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    init {
        gameActivity.firstPlayerStatus.text = getStatusText(game.players.first, "Joueur 1", "Joueur 2")
        gameActivity.secondPlayerStatus.text = getStatusText(game.players.second, "Joueur 2", "Joueur 1")
    }

    private fun getStatusText(player: Player, playerName: String, opponentName: String): String {
        return when {
            game.currentPlayer != player -> gameActivity.resources.getString(R.string.player_must_wait, playerName)
            else -> getCurrentPlayerStatus(playerName, opponentName)
        }
    }

    abstract fun getCurrentPlayerStatus(playerName: String, opponentName: String): String
}