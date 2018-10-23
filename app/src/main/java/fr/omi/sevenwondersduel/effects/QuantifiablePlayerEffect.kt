package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game
import fr.omi.sevenwondersduel.Player

open class QuantifiablePlayerEffect(val count: (Player) -> Int) : QuantifiableEffect {
    override fun count(game: Game, player: Player): Int {
        return count(player)
    }
}