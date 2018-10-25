package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Age
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building
import kotlin.math.abs

data class Structure(val list: List<Map<Int, Building>>, val age: Age) : List<Map<Int, Building>> by list {

    constructor(age: Age) : this(createStructure(age), age)

    constructor(age: Age, buildings: List<Building>) : this(createStructure(age, buildings), age)

    fun take(building: Building): Structure {
        require(isAccessible(building)) { "This building cannot be taken" }
        return copy(list = list.map { row -> row.minus(row.keys.filter { row[it] == building }) })
    }

    fun isAccessible(building: Building): Boolean {
        val remainingAccessibleColumns = (if (age == AGE_III) (-3..3) else (-5..5)).toMutableSet()
        var row = size - 1
        while (remainingAccessibleColumns.isNotEmpty() && row >= 0) {
            get(row).filterKeys { remainingAccessibleColumns.contains(it) }.forEach { entry ->
                if (entry.value == building) return true
                else remainingAccessibleColumns.removeAll { abs(entry.key - it) <= 1 }
            }
            row--
        }
        return false
    }

    fun isFaceUp(row: Int, column: Int): Boolean {
        return row % 2 == 0 || size < row || !(get(row + 1).containsKey(column - 1) || get(row + 1).containsKey(column + 1))
    }

    override fun isEmpty(): Boolean {
        return get(0).isEmpty()
    }

    fun accessibleBuildings(): Collection<Building> {
        val remainingAccessibleColumns = (if (age == Age.AGE_III) (-3..3) else (-5..5)).toMutableSet()
        var row = size - 1
        val accessibleBuildings = mutableListOf<Building>()
        while (remainingAccessibleColumns.isNotEmpty() && row >= 0) {
            get(row).filterKeys { remainingAccessibleColumns.contains(it) }.forEach { entry ->
                accessibleBuildings.add(entry.value)
                remainingAccessibleColumns.removeAll { abs(entry.key - it) <= 1 }
            }
            row--
        }
        return accessibleBuildings
    }

    companion object {
        private fun createStructure(age: Age): List<Map<Int, Building>> {
            val deck = when (age) {
                AGE_I, AGE_II -> Building.values().filter { it.deck == age }.shuffled()
                else -> {
                    val threeRandomGuilds = Building.values().filter { it.deck == Building.Deck.Guilds }.shuffled().take(3)
                    val age3BuildingsBut3 = Building.values().filter { it.deck == AGE_III }.shuffled().drop(3)
                    age3BuildingsBut3.plus(threeRandomGuilds).shuffled()
                }
            }
            return createStructure(age, deck)
        }

        private fun createStructure(age: Age, buildings: List<Building>): List<Map<Int, Building>> {
            val deck = buildings.toMutableList()
            val structureDescription = when (age) {
                AGE_I -> listOf(listOf(-1, 1), listOf(-2, 0, 2), listOf(-3, -1, 1, 3), listOf(-4, -2, 0, 2, 4), listOf(-5, -3, -1, 1, 3, 5))
                AGE_II -> listOf(listOf(-5, -3, -1, 1, 3, 5), listOf(-4, -2, 0, 2, 4), listOf(-3, -1, 1, 3), listOf(-2, 0, 2), listOf(-1, 1))
                AGE_III -> listOf(listOf(-1, 1), listOf(-2, 0, 2), listOf(-3, -1, 1, 3), listOf(-2, 2), listOf(-3, -1, 1, 3), listOf(-2, 0, 2), listOf(-1, 1))
            }
            return structureDescription.asSequence()
                    .map {
                        when (deck.size) {
                            0 -> emptyList()
                            in 1 until it.size -> it.take(deck.size)
                            else -> it
                        }
                    }.map { positions -> positions.associate { it to deck.removeAt(0) } }.toList()
        }
    }

}