package fr.omi.sevenwondersduel.material

import fr.omi.sevenwondersduel.effects.Effect

interface Construction {
    data class Cost(val coins: Int = 0, val resources: Map<Resource, Int> = emptyMap())

    val cost: Cost
    val effects: List<Effect>
}