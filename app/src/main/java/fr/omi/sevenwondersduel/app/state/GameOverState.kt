package fr.omi.sevenwondersduel.app.state

import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.app.GameActivity
import kotlinx.android.synthetic.main.activity_game.*

class GameOverState(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    init {
        when {
            game.conflictPawnPosition == 9 -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_win_by_military_supremacy, "Joueur 1")
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_lost_by_military_supremacy, "Joueur 2", "Joueur 1")
            }
            game.conflictPawnPosition == -9 -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_lost_by_military_supremacy, "Joueur 1", "Joueur 2")
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_win_by_military_supremacy, "Joueur 2")
            }
            game.players.first.hasScientificSupremacy -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_win_by_scientific_supremacy, "Joueur 1")
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_lost_by_scientific_supremacy, "Joueur 2", "Joueur 1")
            }
            game.players.second.hasScientificSupremacy -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_lost_by_scientific_supremacy, "Joueur 1", "Joueur 2")
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_win_by_scientific_supremacy, "Joueur 2")
            }
            else -> displayCivilianVictoryStatuses(gameActivity)
        }
    }

    private fun displayCivilianVictoryStatuses(gameActivity: GameActivity) {
        val firstPlayerVictoryPoints = game.countVictoryPoint(game.players.first)
        val secondPlayerVictoryPoints = game.countVictoryPoint(game.players.second)
        when {
            firstPlayerVictoryPoints > secondPlayerVictoryPoints -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_civilian_victory, "Joueur 1", firstPlayerVictoryPoints)
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_civilian_defeat, "Joueur 2", secondPlayerVictoryPoints)
            }
            secondPlayerVictoryPoints > firstPlayerVictoryPoints -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_civilian_defeat, "Joueur 1", firstPlayerVictoryPoints)
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_civilian_victory, "Joueur 2", secondPlayerVictoryPoints)
            }
            game.players.first.countCivilianBuildingsVictoryPoints() > game.players.second.countCivilianBuildingsVictoryPoints() -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_short_civilian_victory, "Joueur 1", firstPlayerVictoryPoints, "Joueur 2")
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_short_civilian_defeat, "Joueur 2", secondPlayerVictoryPoints, "Joueur 1")
            }
            game.players.second.countCivilianBuildingsVictoryPoints() > game.players.first.countCivilianBuildingsVictoryPoints() -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_short_civilian_defeat, "Joueur 1", firstPlayerVictoryPoints, "Joueur 2")
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_short_civilian_victory, "Joueur 2", secondPlayerVictoryPoints, "Joueur 1")
            }
            else -> {
                gameActivity.firstPlayerStatus.text = gameActivity.resources.getString(R.string.player_equality, firstPlayerVictoryPoints, game.players.first.countCivilianBuildingsVictoryPoints())
                gameActivity.secondPlayerStatus.text = gameActivity.resources.getString(R.string.player_equality, firstPlayerVictoryPoints, game.players.first.countCivilianBuildingsVictoryPoints())
            }
        }
    }

    override fun leave() {}
}
