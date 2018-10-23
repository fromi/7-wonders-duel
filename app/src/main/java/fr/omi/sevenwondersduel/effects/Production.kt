package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.material.Resource

data class Production(val resource: Resource, val quantity: Int = 1) : Effect