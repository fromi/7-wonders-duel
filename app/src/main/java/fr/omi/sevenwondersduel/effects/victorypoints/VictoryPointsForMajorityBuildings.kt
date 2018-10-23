package fr.omi.sevenwondersduel.effects.victorypoints

import fr.omi.sevenwondersduel.effects.MajorityBuildingsEffect
import fr.omi.sevenwondersduel.material.Building

class VictoryPointsForMajorityBuildings(vararg buildingTypes: Building.Type) : VictoryPointsEffect, MajorityBuildingsEffect(*buildingTypes)