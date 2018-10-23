package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel

interface ChoiceEffect : Effect, Action {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.copy(pendingActions = game.pendingActions.plus(this))
    }
}