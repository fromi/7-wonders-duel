package fr.omi.sevenwondersduel.effects

import fr.omi.sevenwondersduel.material.Construction

class ConstructionTriggeredEffect(val triggeredEffect: Effect, val appliesTo: (Construction) -> Boolean) : Effect