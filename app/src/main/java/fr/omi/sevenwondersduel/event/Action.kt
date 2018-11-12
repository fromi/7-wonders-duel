package fr.omi.sevenwondersduel.event

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.ai.ConstructBuilding
import fr.omi.sevenwondersduel.ai.ConstructWonder
import fr.omi.sevenwondersduel.ai.SevenWondersDuelMove
import fr.omi.sevenwondersduel.ai.TakeWonder
import fr.omi.sevenwondersduel.material.Wonder

class Action(val game: SevenWondersDuel, private val move: SevenWondersDuelMove) {
    fun inferEventsLeadingTo(newState: SevenWondersDuel): List<GameEvent> {
        return when (move) {
            is TakeWonder -> when {
                newState.wondersAvailable.size == 4 -> listOf(PlaceFourAvailableWondersEvent(newState.wondersAvailable))
                newState.wondersAvailable.isEmpty() -> listOf(PrepareStructureEvent(checkNotNull(newState.structure)))
                else -> emptyList()
            }
            is ConstructBuilding -> inferConstructionEffects(newState)
            is ConstructWonder -> inferWonderConstructionEffects(move.wonder, newState)
            else -> emptyList()
        }.plus(inferStructureConsequences(newState))
    }

    private fun inferConstructionEffects(newState: SevenWondersDuel): List<GameEvent> {
        val events = mutableListOf<GameEvent>()
        val opponentNumber = if (game.currentPlayerNumber == 1) 2 else 1
        val newStateOpponent = if (game.currentPlayerNumber == 1) newState.players.second else newState.players.first
        when {
            newStateOpponent.militaryTokensLooted == 1 -> events.add(MilitaryTokenLooted(opponentNumber, 1))
            newStateOpponent.militaryTokensLooted == 2 && game.opponent.militaryTokensLooted == 1 -> events.add(MilitaryTokenLooted(opponentNumber, 2))
            newStateOpponent.militaryTokensLooted == 2 && game.opponent.militaryTokensLooted == 0 -> {
                events.add(MilitaryTokenLooted(opponentNumber, 1))
                events.add(MilitaryTokenLooted(opponentNumber, 2))
            }
        }
        return events
    }

    private fun inferWonderConstructionEffects(wonder: Wonder, newState: SevenWondersDuel): List<GameEvent> {
        return if (newState.players.toList().sumBy { player -> player.wonders.count { it.isConstructed() } } == 7)
            inferConstructionEffects(newState) + LastWonderDiscarded(game.players.toList().flatMap { it.wonders }.filter { !it.isConstructed() }.map { it.wonder }.minus(wonder).first())
        else inferConstructionEffects(newState)
    }

    private fun inferStructureConsequences(newState: SevenWondersDuel): List<GameEvent> {
        val structure = checkNotNull(game.structure)
        val newStructure = checkNotNull(newState.structure)
        if (structure.age != newStructure.age) return listOf(PrepareStructureEvent(newStructure))
        if (newState.pendingActions.isNotEmpty()) return emptyList() // because hidden cards are revealed after all actions (Mausoleum, Progress token...) are completed
        return newStructure.mapIndexed { row, line -> line.filter { (column, buildingCard) -> buildingCard.faceUp && !checkNotNull(structure[row][column]).faceUp } }
                .flatMap { line -> line.values.map { BuildingRevealedEvent(it.building) } }
    }
}