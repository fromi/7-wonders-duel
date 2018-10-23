package fr.omi.sevenwondersduel.effects.takecoins

import fr.omi.sevenwondersduel.effects.MajorityBuildingsEffect
import fr.omi.sevenwondersduel.material.Building

class TakeCoinsForMajorityBuildings(vararg buildingTypes: Building.Type) : TakeCoinsEffect, MajorityBuildingsEffect(*buildingTypes)