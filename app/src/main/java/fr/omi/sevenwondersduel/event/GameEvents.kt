package fr.omi.sevenwondersduel.event

import fr.omi.sevenwondersduel.Structure
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

interface GameEvent

data class PlaceFourAvailableWondersEvent(val wonders: Set<Wonder>) : GameEvent
data class PrepareStructureEvent(val structure: Structure) : GameEvent
data class BuildingRevealedEvent(val building: Building) : GameEvent

object ConflictPawnMoved : GameEvent
data class MilitaryTokenLooted(val playerNumber: Int, val tokenNumber: Int) : GameEvent
data class LastWonderDiscarded(val wonder: Wonder) : GameEvent