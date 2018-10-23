package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.SevenWondersDuel
import kotlin.math.max

open class MajorityEffect(val count: (Player) -> Int) : QuantifiableEffect {
    override fun count(game: SevenWondersDuel, player: Player): Int {
        return max(count(game.players.first), count(game.players.second))
    }
}