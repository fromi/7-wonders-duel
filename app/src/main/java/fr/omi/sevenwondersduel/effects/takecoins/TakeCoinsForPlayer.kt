package fr.omi.sevenwondersduel.effects.takecoins

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.effects.QuantifiablePlayerEffect

class TakeCoinsForPlayer(count: (Player) -> Int) : TakeCoinsEffect, QuantifiablePlayerEffect(count)