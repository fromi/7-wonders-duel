package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game
import fr.omi.sevenwondersduel.Player

abstract class FixQuantityEffect(val quantity: Int) : QuantifiableEffect {
    override fun count(game: Game, player: Player): Int {
        return quantity
    }
}