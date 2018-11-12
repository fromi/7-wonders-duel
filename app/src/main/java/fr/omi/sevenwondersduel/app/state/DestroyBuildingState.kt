package fr.omi.sevenwondersduel.app.state

import android.transition.TransitionManager
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import fr.omi.sevenwondersduel.ai.DestroyBuilding
import fr.omi.sevenwondersduel.app.*
import fr.omi.sevenwondersduel.effects.OpponentBuildingToDestroy
import kotlinx.android.synthetic.main.activity_game.*

class DestroyBuildingState(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    private val discard: TextView get() = gameActivity.discard
    private val action get() = game.pendingActions.first() as OpponentBuildingToDestroy
    private val eligibleBuildings = game.opponent.buildings.filter { action.isEligible(it) }

    init {
        eligibleBuildings.forEach { gameActivity.getView(it).enableDragAndDrop() }
        discard.setOnDragListener { _, event -> discardDragListener(event) }
    }

    private fun discardDragListener(event: DragEvent): Boolean {
        if (event.localState !is BuildingView) return false
        val buildingView = event.localState as BuildingView
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                discard.scaleX = 1.1F
                discard.scaleY = 1.1F
                buildingView.visibility = View.INVISIBLE
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                discard.scaleX = 1.2F
                discard.scaleY = 1.2F
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                discard.scaleX = 1.1F
                discard.scaleY = 1.1F
            }
            DragEvent.ACTION_DROP -> {
                gameActivity.remove(buildingView)
                model.execute(DestroyBuilding(checkNotNull(buildingView.building)))
                TransitionManager.beginDelayedTransition(layout)
                game.currentPlayer.buildings.forEachIndexed { index, building -> gameActivity.getView(building).positionForPlayer(checkNotNull(game.currentPlayerNumber), index) }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                discard.scaleX = 1F
                discard.scaleY = 1F
                buildingView.visibility = View.VISIBLE
            }
        }
        return true
    }

    override fun leave() {
        gameActivity.discard.removeDragListener()
        eligibleBuildings.forEach { gameActivity.getView(it).disableDragAndDrop() }
    }
}
