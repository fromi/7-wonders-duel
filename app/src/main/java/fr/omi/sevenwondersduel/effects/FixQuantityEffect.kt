package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.SevenWondersDuel

abstract class FixQuantityEffect(val quantity: Int) : QuantifiableEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return quantity
    }
}