package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Age
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.BuildingCard
import fr.omi.sevenwondersduel.material.Deck

data class Structure(val list: List<Map<Int, BuildingCard>>, val age: Age) : List<Map<Int, BuildingCard>> by list {

    constructor(age: Age) : this(createStructure(age), age)

    constructor(age: Age, buildings: List<Building>) : this(createStructure(age, buildings), age)

    fun take(building: Building): Structure {
        val (row, column) = getCoordinatesOf(building)
        require(isAccessible(row, column)) { "This building cannot be taken" }
        return copy(list = list.map { line -> line.filterValues { it.building != building } }.filter { it.isNotEmpty() })
    }

    private fun getCoordinatesOf(building: Building): Pair<Int, Int> {
        return requireNotNull(list.asSequence()
                .mapIndexed { row, line -> Pair(row, line.entries.firstOrNull { it.value.building == building }?.key) }
                .filter { coordinates -> coordinates.second != null }
                .map { coordinates -> Pair(coordinates.first, coordinates.second!!) }
                .lastOrNull()) { "This building is not in the structure" }
    }

    private fun isAccessible(row: Int, column: Int): Boolean {
        return list.size <= row + 1 || list[row + 1].keys.none { it == column - 1 || it == column + 1 }
    }

    fun revealAccessibleBuildings(): Structure = copy(list = list.mapIndexed { index, row ->
        row.mapValues { entry ->
            if (!entry.value.faceUp && isAccessible(index, entry.key)) entry.value.copy(faceUp = true) else entry.value
        }
    })

    fun accessibleBuildings(): Collection<Building> {
        return mapIndexed { row, line -> line.filterKeys { isAccessible(row, it) }.map { it.value.building } }.flatten()
    }

    companion object {
        private fun createStructure(age: Age): List<Map<Int, BuildingCard>> {
            val deck = when (age) {
                AGE_I -> Deck.AGE_I.buildings.shuffled()
                AGE_II -> Deck.AGE_II.buildings.shuffled()
                else -> Deck.AGE_III.buildings.shuffled().drop(3).plus(Deck.GUILDS.buildings.shuffled().take(3)).shuffled()
            }
            return createStructure(age, deck)
        }

        private fun createStructure(age: Age, buildings: List<Building>): List<Map<Int, BuildingCard>> {
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
                    }.mapIndexed { index, positions -> positions.associate { it to BuildingCard(deck.removeAt(0), index % 2 == 0) } }
                    .filter { it.isNotEmpty() }.toList()
        }
    }

}