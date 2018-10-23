package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game

interface ChoiceEffect : Effect, Action {
    override fun applyTo(game: Game): Game {
        return game.copy(pendingActions = game.pendingActions.plus(this))
    }
}