package fr.omi.sevenwondersduel.effects.takecoins

import fr.omi.sevenwondersduel.effects.QuantifiablePlayerEffect
import fr.omi.sevenwondersduel.material.Building

class TakeCoinsForPlayerBuildings(quantity: Int, condition: (Building) -> Boolean) : TakeCoinsEffect, QuantifiablePlayerEffect({ player -> quantity * player.buildings.count(condition) })