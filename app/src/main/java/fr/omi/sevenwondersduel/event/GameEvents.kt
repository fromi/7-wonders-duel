package fr.omi.sevenwondersduel.event

import fr.omi.sevenwondersduel.Structure
import fr.omi.sevenwondersduel.material.Wonder

interface GameEvent

data class PlaceFourAvailableWondersEvent(val wonders: Set<Wonder>) : GameEvent
data class PrepareStructureEvent(val structure: Structure) : GameEvent
