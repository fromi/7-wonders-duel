package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.SevenWondersDuel

interface QuantifiableEffect : Effect {
    fun count(game: SevenWondersDuel, player: Player): Int
}