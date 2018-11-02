package fr.omi.sevenwondersduel.effects.takecoins

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.effects.MajorityEffect

class TakeCoinsForMajority(quantity: Int, majorityCount: (Player) -> Int) : TakeCoinsEffect, MajorityEffect(quantity, majorityCount)