package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.SevenWondersDuel

object DiscardedBuildingToBuild : PendingEffect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = if (game.discardedCards.isNotEmpty()) super.applyTo(game) else game
}