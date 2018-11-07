package fr.omi.sevenwondersduel.app

import fr.omi.sevenwondersduel.event.GameEvent

class GameOverPhase(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    override fun handleEvent(event: GameEvent) {
        throw IllegalStateException("Unexpected action after game is over: $event")
    }
}
