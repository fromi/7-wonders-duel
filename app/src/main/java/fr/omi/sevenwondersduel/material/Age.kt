package fr.omi.sevenwondersduel.material

enum class Age : Building.Deck {
    AGE_I, AGE_II, AGE_III;

    override fun age(): Age {
        return this
    }
}