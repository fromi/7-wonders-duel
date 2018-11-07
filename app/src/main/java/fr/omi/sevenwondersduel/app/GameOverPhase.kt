package fr.omi.sevenwondersduel.app

import fr.omi.sevenwondersduel.event.Action

class GameOverPhase(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    override fun handle(action: Action) {
        throw IllegalStateException("Unexpected action after game is over: $action")
    }
}
