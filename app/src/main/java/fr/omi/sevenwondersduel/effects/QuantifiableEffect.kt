package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.Game
import fr.omi.sevenwondersduel.Player

interface QuantifiableEffect : Effect {
    fun count(game: Game, player: Player): Int
}