package fr.omi.sevenwondersduel.event

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.ai.SevenWondersDuelMove
import fr.omi.sevenwondersduel.ai.TakeWonder

class Action(val game: SevenWondersDuel, val move: SevenWondersDuelMove) {
    fun inferEventsLeadingTo(newState: SevenWondersDuel): List<GameEvent> {
        return when (move) {
            is TakeWonder -> when {
                newState.wondersAvailable.size == 4 -> listOf(PlaceFourAvailableWondersEvent(newState.wondersAvailable))
                newState.wondersAvailable.isEmpty() -> listOf(PrepareStructureEvent(checkNotNull(newState.structure)))
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}