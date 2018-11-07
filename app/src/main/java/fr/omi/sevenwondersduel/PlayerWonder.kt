package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

data class PlayerWonder(val wonder: Wonder, val buildingUnder: Building? = null) {
    fun constructWith(building: Building): PlayerWonder {
        check(!isConstructed()) { "This wonder is already built" }
        return copy(buildingUnder = building)
    }

    fun isConstructed(): Boolean = buildingUnder != null
}