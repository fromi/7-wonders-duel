package fr.omi.sevenwondersduel.effects.takecoins

import fr.omi.sevenwondersduel.effects.QuantifiablePlayerEffect
import fr.omi.sevenwondersduel.material.Building

class TakeCoinsForPlayerBuildings(buildingType: Building.Type, quantity: Int = 1) : TakeCoinsEffect, QuantifiablePlayerEffect({ player -> quantity * player.buildings.count { it.type == buildingType } })