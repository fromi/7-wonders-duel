package fr.omi.sevenwondersduel.app

import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View

class WonderSelectionPhase(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    private val wonderDropZone: WonderView

    init {
        wonderDropZone = createWonderDropZone()
        game.wondersAvailable.forEachIndexed { index, wonder ->
            val wonderView = WonderView(gameActivity, wonder).positionInto(0, index)
            gameActivity.wondersViews[wonder] = wonderView
            wonderView.enableDragAndDrop()
        }
    }

    private fun createWonderDropZone(): WonderView {
        return WonderView(gameActivity).apply {
            alpha = 0F
            setOnDragListener { _, event -> wonderDropListener(event) }
            positionToNextWonderPlace(this)
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
            ACTION_DROP -> selectWonder(wonderView)
            ACTION_DRAG_ENDED -> {
                wonderView.visibility = View.VISIBLE
                wonderDropZone.alpha = 0F
                if (event.result) {
                    handleWonderSelected()
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

    private fun selectWonder(wonderView: WonderView) {
        positionToNextWonderPlace(wonderView)
        wonderView.disableDragAndDrop()
        model.choose(checkNotNull(wonderView.wonder))
    }

    private fun handleWonderSelected() {
        if (game.wondersAvailable.isNotEmpty()) {
            prepareNextWonderSelection()
        } else {
            gameActivity.getView(game.players.second.wonders[3].wonder).moveInto(layout, 2, 3)
            goToGamePhase()
        }
    }

    private fun prepareNextWonderSelection() {
        positionToNextWonderPlace(wonderDropZone)
        if (game.wondersAvailable.size == 4) {
            gameActivity.getView(game.players.first.wonders[1].wonder).moveInto(layout, 1, 1)
            game.wondersAvailable.forEachIndexed { index, wonder ->
                val wonderView = WonderView(gameActivity, wonder).positionInto(0, index)
                gameActivity.wondersViews[wonder] = wonderView
                wonderView.enableDragAndDrop()
            }
        }
    }

    private fun goToGamePhase() {
        layout.removeView(wonderDropZone)
        gameActivity.display(checkNotNull(game.structure))
        gameActivity.state = GamePhase(gameActivity)
    }
}