package fr.omi.sevenwondersduel.event

import fr.omi.sevenwondersduel.Structure
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

interface GameEvent

data class PlaceFourAvailableWondersEvent(val wonders: Set<Wonder>) : GameEvent
data class PrepareStructureEvent(val structure: Structure) : GameEvent
data class BuildingMadeAccessibleEvent(val building: Building, val row: Int, val column: Int) : GameEvent {
    constructor(structure: Structure, row: Int, column: Int) : this(checkNotNull(structure[row][column]).building, row, column)
}

data class MilitaryTokenLooted(val playerNumber: Int, val tokenNumber: Int) : GameEvent