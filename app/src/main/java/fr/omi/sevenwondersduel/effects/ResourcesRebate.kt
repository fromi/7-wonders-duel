package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.material.Construction

class ResourcesRebate(val quantity: Int, val appliesTo: (Construction) -> Boolean) : Effect