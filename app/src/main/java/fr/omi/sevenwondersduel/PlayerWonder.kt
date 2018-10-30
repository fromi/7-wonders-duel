package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

data class PlayerWonder(val wonder: Wonder, val buildingUnder: Building? = null) {
    fun buildWith(building: Building): PlayerWonder {
        check(!isBuild()) { "This wonder is already built" }
        return copy(buildingUnder = building)
    }

    fun isBuild(): Boolean = buildingUnder != null
}