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
            is ConstructBuilding -> inferBuildingsMadeAccessibleByTaking(move.building)
            is Discard -> inferBuildingsMadeAccessibleByTaking(move.building)
            is ConstructWonder -> inferBuildingsMadeAccessibleByTaking(move.buildingUsed)
            else -> emptyList()
        }
    }

    private fun inferBuildingsMadeAccessibleByTaking(building: Building): List<BuildingMadeAccessibleEvent> {
        val events = mutableListOf<BuildingMadeAccessibleEvent>()
        val structure = checkNotNull(game.structure)
        val (row, column) = structure.getCoordinatesOf(building)
        if (row == 0) return emptyList()
        if (structure[row - 1].containsKey(column - 1) && !structure[row].containsKey(column - 2))
            events.add(BuildingMadeAccessibleEvent(structure, row - 1, column - 1))
        if (structure[row - 1].containsKey(column + 1) && !structure[row].containsKey(column + 2))
            events.add(BuildingMadeAccessibleEvent(structure, row - 1, column + 1))
        return events
    }
}