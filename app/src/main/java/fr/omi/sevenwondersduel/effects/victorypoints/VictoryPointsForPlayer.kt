package fr.omi.sevenwondersduel.effects.victorypoints

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.effects.QuantifiablePlayerEffect

class VictoryPointsForPlayer(count: (Player) -> Int) : VictoryPointsEffect, QuantifiablePlayerEffect(count)