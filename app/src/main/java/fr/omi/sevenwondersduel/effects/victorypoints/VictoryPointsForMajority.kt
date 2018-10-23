package fr.omi.sevenwondersduel.effects.victorypoints

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.effects.MajorityEffect

class VictoryPointsForMajority(majorityCount: (Player) -> Int) : VictoryPointsEffect, MajorityEffect(majorityCount)