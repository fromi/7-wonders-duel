package fr.omi.sevenwondersduel.app.state

import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.app.GameActivity
import kotlinx.android.synthetic.main.activity_game.*

abstract class OngoingGameState(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    override fun displayPlayerStatuses() {
        if (game.currentPlayerNumber == 1) {
            gameActivity.firstPlayerStatus.text = getCurrentPlayerStatus("Joueur 1", "Joueur 2")
            gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_must_wait, "Joueur 2")
        } else {
            gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_must_wait, "Joueur 1")
            gameActivity.secondPlayerStatus.text = getCurrentPlayerStatus("Joueur 2", "Joueur 1")
        }
    }

    abstract fun getCurrentPlayerStatus(playerName: String, opponentName: String): String
}