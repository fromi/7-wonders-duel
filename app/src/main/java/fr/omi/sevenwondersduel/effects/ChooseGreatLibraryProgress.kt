package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.ProgressToken

object ChooseGreatLibraryProgress : Effect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        val tokens = ProgressToken.values().asSequence()
                .minus(game.progressTokensAvailable).minus(game.players.first.progressTokens).minus(game.players.second.progressTokens)
                .toList().shuffled().asSequence().take(3).toSet()
        return game.copy(pendingActions = game.pendingActions.plus(ProgressTokenToChoose(tokens = tokens)))
    }
}