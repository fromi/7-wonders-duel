package fr.omi.sevenwondersduel.material

import fr.omi.sevenwondersduel.material.Resource.Type.MANUFACTURED_GOOD
import fr.omi.sevenwondersduel.material.Resource.Type.RAW_GOOD

enum class Resource(val type: Type) {
    CLAY(RAW_GOOD), WOOD(RAW_GOOD), STONE(RAW_GOOD), GLASS(MANUFACTURED_GOOD), PAPYRUS(MANUFACTURED_GOOD);

    enum class Type {
        RAW_GOOD, MANUFACTURED_GOOD
    }
}