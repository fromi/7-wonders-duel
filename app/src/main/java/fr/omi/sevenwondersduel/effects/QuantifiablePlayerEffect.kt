package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.SevenWondersDuel

open class QuantifiablePlayerEffect(val count: (Player) -> Int) : QuantifiableEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return count(player)
    }
}