package fr.omi.sevenwondersduel.app

import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import fr.omi.sevenwondersduel.ai.TakeWonder
import fr.omi.sevenwondersduel.event.Action
import fr.omi.sevenwondersduel.event.GameEvent
import fr.omi.sevenwondersduel.event.PlaceFourAvailableWondersEvent
import fr.omi.sevenwondersduel.event.PrepareStructureEvent

class WonderSelectionPhase(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    private val wonderDropZone: WonderView

    init {
        wonderDropZone = WonderView(this.gameActivity).apply {
            alpha = 0F
            setOnDragListener { _, event -> wonderDropListener(event) }
            positionToNextWonderPlace(this)
        }
        game.wondersAvailable.forEachIndexed { index, wonder ->
            val wonderView = WonderView(gameActivity, wonder).positionInto(0, index)
            gameActivity.wondersViews[wonder] = wonderView
            wonderView.enableDragAndDrop()
        }
    }

    override fun handle(action: Action) {
        if (game.wondersAvailable.isNotEmpty()) {
            positionToNextWonderPlace(wonderDropZone)
        }
        super.handle(action)
    }

    override fun handleEvent(event: GameEvent) {
        when (event) {
            is PlaceFourAvailableWondersEvent -> event.wonders.forEachIndexed { index, wonder ->
                val wonderView = WonderView(gameActivity, wonder).positionInto(0, index)
                gameActivity.wondersViews[wonder] = wonderView
                wonderView.enableDragAndDrop()
            }
            is PrepareStructureEvent -> goToGamePhase()
        }
    }

    private fun positionToNextWonderPlace(wonderView: WonderView) {
        wonderView.positionInto(checkNotNull(game.currentPlayerNumber), game.currentPlayer.wonders.size)
    }

    private fun wonderDropListener(event: DragEvent): Boolean {
        if (event.localState !is WonderView) return false
        val wonderView = event.localState as WonderView
        when (event.action) {
            ACTION_DRAG_STARTED -> displayDropPosition(wonderView)
            ACTION_DRAG_ENTERED -> wonderDropZone.alpha = 1F
            ACTION_DRAG_EXITED -> wonderDropZone.alpha = 0.5F
            ACTION_DROP -> {
                positionToNextWonderPlace(wonderView)
                wonderView.disableDragAndDrop()
            }
            ACTION_DRAG_ENDED -> {
                wonderView.visibility = View.VISIBLE
                wonderDropZone.alpha = 0F
                if (event.result) {
                    model.execute(TakeWonder(checkNotNull(wonderView.wonder)))
                }
            }
        }
        return true
    }

    private fun displayDropPosition(wonderView: WonderView) {
        wonderView.visibility = View.INVISIBLE
        wonderDropZone.alpha = 0.5F
        wonderDropZone.setImageResource(WonderView.getResource(checkNotNull(wonderView.wonder)))
    }

    private fun goToGamePhase() {
        layout.removeView(wonderDropZone)
        gameActivity.display(checkNotNull(game.structure))
        gameActivity.state = GamePhase(gameActivity)
    }
}