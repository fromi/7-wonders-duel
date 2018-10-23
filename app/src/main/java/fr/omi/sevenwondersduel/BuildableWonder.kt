package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

data class BuildableWonder(val wonder: Wonder, val builtWith: Building? = null) {
    fun buildWith(building: Building): BuildableWonder {
        check(!isBuild()) { "This wonder is already built" }
        return copy(builtWith = building)
    }

    fun isBuild(): Boolean = builtWith != null
}