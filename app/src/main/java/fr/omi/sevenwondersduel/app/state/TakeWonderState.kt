package fr.omi.sevenwondersduel.app.state

import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.ai.TakeWonder
import fr.omi.sevenwondersduel.app.GameActivity
import fr.omi.sevenwondersduel.app.WonderView
import fr.omi.sevenwondersduel.app.disableDragAndDrop
import fr.omi.sevenwondersduel.app.enableDragAndDrop

class TakeWonderState(gameActivity: GameActivity) : OngoingGameState(gameActivity) {
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
                wonderView.visibility = View.VISIBLE
                wonderView.disableDragAndDrop()
                model.execute(TakeWonder(checkNotNull(wonderView.wonder)))
            }
            ACTION_DRAG_ENDED -> {
                wonderView.visibility = View.VISIBLE
                wonderDropZone.alpha = 0F
            }
        }
        return true
    }

    private fun displayDropPosition(wonderView: WonderView) {
        wonderView.visibility = View.INVISIBLE
        wonderDropZone.alpha = 0.5F
        wonderDropZone.setImageResource(WonderView.getResource(checkNotNull(wonderView.wonder)))
    }

    override fun getCurrentPlayerStatus(playerName: String, opponentName: String): String {
        return gameActivity.resources.getString(R.string.player_must_take_a_wonder, playerName)
    }

    override fun leave() {
        layout.removeView(wonderDropZone)
        game.wondersAvailable.forEach { gameActivity.getView(it).disableDragAndDrop() }
    }
}