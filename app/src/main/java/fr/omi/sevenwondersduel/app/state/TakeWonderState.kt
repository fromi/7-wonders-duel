package fr.omi.sevenwondersduel.app.state

import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import fr.omi.sevenwondersduel.ai.TakeWonder
import fr.omi.sevenwondersduel.app.GameActivity
import fr.omi.sevenwondersduel.app.WonderView
import fr.omi.sevenwondersduel.app.disableDragAndDrop
import fr.omi.sevenwondersduel.app.enableDragAndDrop

class TakeWonderState(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    private val wonderDropZone = WonderView(this.gameActivity).apply {
        alpha = 0F
        setOnDragListener { _, event -> wonderDropListener(event) }
        positionToNextWonderPlace(this)
    }

    init {
        game.wondersAvailable.forEach { gameActivity.getView(it).enableDragAndDrop() }
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

    override fun leave() {
        layout.removeView(wonderDropZone)
        game.wondersAvailable.forEach { gameActivity.getView(it).disableDragAndDrop() }
    }
}