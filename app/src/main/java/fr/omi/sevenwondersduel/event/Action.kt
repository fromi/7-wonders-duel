package fr.omi.sevenwondersduel.event

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.ai.*
import fr.omi.sevenwondersduel.material.Building

class Action(val game: SevenWondersDuel, val move: SevenWondersDuelMove) {
    fun inferEventsLeadingTo(newState: SevenWondersDuel): List<GameEvent> {
        return when (move) {
            is TakeWonder -> when {
                newState.wondersAvailable.size == 4 -> listOf(PlaceFourAvailableWondersEvent(newState.wondersAvailable))
                newState.wondersAvailable.isEmpty() -> listOf(PrepareStructureEvent(checkNotNull(newState.structure)))
                else -> emptyList()
            }
            is ConstructBuilding -> inferStructureConsequencesAfterTaking(move.building, newState)
            is Discard -> inferStructureConsequencesAfterTaking(move.building, newState)
            is ConstructWonder -> inferStructureConsequencesAfterTaking(move.buildingUsed, newState)
            else -> emptyList()
        }
    }

    private fun inferStructureConsequencesAfterTaking(building: Building, newState: SevenWondersDuel): List<GameEvent> {
        val structure = checkNotNull(game.structure)
        val newStructure = checkNotNull(newState.structure)
        if (structure.age != newStructure.age) return listOf(PrepareStructureEvent(newStructure))
        if (newState.pendingActions.isNotEmpty()) return emptyList() // because hidden cards are revealed after all actions (Mausoleum, Progress token...) are completed
        val (row, column) = structure.getCoordinatesOf(building)
        if (row == 0) return emptyList()
        val events = mutableListOf<BuildingMadeAccessibleEvent>()
        if (structure[row - 1].containsKey(column - 1) && !structure[row].containsKey(column - 2))
            events.add(BuildingMadeAccessibleEvent(structure, row - 1, column - 1))
        if (structure[row - 1].containsKey(column + 1) && !structure[row].containsKey(column + 2))
            events.add(BuildingMadeAccessibleEvent(structure, row - 1, column + 1))
        return events
    }
}