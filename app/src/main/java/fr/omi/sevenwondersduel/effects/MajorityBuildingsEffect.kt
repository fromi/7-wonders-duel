package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.material.Building

abstract class MajorityBuildingsEffect(vararg buildingTypes: Building.Type) : MajorityEffect({ player -> player.buildings.count { building -> buildingTypes.any { building.type == it } } })