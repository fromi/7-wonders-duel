package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel

object PlayAgain : PendingEffect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return if (!game.currentAgeIsOver() && !game.pendingActions.contains(PlayAgain)) super.applyTo(game) else game
    }
}