package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game
import fr.omi.sevenwondersduel.material.ProgressToken

object ChooseGreatLibraryProgress : Effect {
    override fun applyTo(game: Game): Game {
        val tokens = ProgressToken.values().asSequence()
                .minus(game.progressTokensAvailable).minus(game.players.first.progressTokens).minus(game.players.second.progressTokens)
                .toList().shuffled().asSequence().take(3).toSet()
        return game.copy(pendingActions = game.pendingActions.plus(ChooseProgressToken(tokens = tokens)))
    }
}