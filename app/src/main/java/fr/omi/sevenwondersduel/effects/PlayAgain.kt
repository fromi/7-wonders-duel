package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game

object PlayAgain : ChoiceEffect {
    override fun applyTo(game: Game): Game {
        return if (!game.currentAgeIsOver() && !game.pendingActions.contains(PlayAgain)) super.applyTo(game) else game
    }
}