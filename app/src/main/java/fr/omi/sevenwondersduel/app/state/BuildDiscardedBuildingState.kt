package fr.omi.sevenwondersduel.app.state

import fr.omi.sevenwondersduel.app.GameActivity
import fr.omi.sevenwondersduel.app.disableDragAndDrop
import fr.omi.sevenwondersduel.app.enableDragAndDrop
import fr.omi.sevenwondersduel.material.Building

class BuildDiscardedBuildingState(gameActivity: GameActivity) : ConstructBuildingState(gameActivity) {
    init {
        gameActivity.displayDiscard()
        game.discardedCards.forEach { building -> gameActivity.getView(building).enableDragAndDrop() }
    }

    override fun canConstruct(building: Building): Boolean = true

    override fun leave() {
        gameActivity.hideDiscard()
        game.discardedCards.forEach { building -> gameActivity.getView(building).disableDragAndDrop() }
    }

}
