package fr.omi.sevenwondersduel.app.state

import android.view.DragEvent
import android.view.View
import fr.omi.sevenwondersduel.ai.ChooseProgressToken
import fr.omi.sevenwondersduel.app.GameActivity
import fr.omi.sevenwondersduel.app.ProgressTokenView
import fr.omi.sevenwondersduel.app.disableDragAndDrop
import fr.omi.sevenwondersduel.app.enableDragAndDrop
import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.ProgressToken

class ChooseProgressTokenState(gameActivity: GameActivity, val action: ProgressTokenToChoose) : GameActivityState(gameActivity) {
    private val progressTokenDropZone = ProgressTokenView(gameActivity, ProgressToken.LAW).apply {
        alpha = 0F
        positionForPlayer(checkNotNull(game.currentPlayerNumber), game.currentPlayer.progressTokens.size)
        setOnDragListener { _, event -> progressTokenDropListener(event) }
    }

    init {
        if (action.tokens == game.progressTokensAvailable) {
            action.tokens.forEach { gameActivity.getView(it).enableDragAndDrop() }
        } else {
            action.tokens.forEachIndexed { index, progressToken ->
                gameActivity.createView(progressToken).apply {
                    positionOnTheGreatLibrary(index)
                    enableDragAndDrop()
                }
            }
        }
    }

    private fun progressTokenDropListener(event: DragEvent): Boolean {
        if (event.localState !is ProgressTokenView) return false
        val progressTokenView = event.localState as ProgressTokenView
        val progressToken = progressTokenView.progressToken
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                progressTokenView.visibility = View.INVISIBLE
                progressTokenDropZone.alpha = 0.5F
                progressTokenDropZone.setImageResource(ProgressTokenView.getResource(checkNotNull(progressToken)))
            }
            DragEvent.ACTION_DRAG_ENTERED -> progressTokenDropZone.alpha = 1F
            DragEvent.ACTION_DRAG_EXITED -> progressTokenDropZone.alpha = 0.5F
            DragEvent.ACTION_DROP -> {
                progressTokenView.positionForPlayer(checkNotNull(game.currentPlayerNumber), game.currentPlayer.progressTokens.size)
                progressTokenView.visibility = View.VISIBLE
                progressTokenView.disableDragAndDrop()
                action.tokens.filter { it != progressToken }.forEach {
                    if (game.progressTokensAvailable.contains(it))
                        gameActivity.getView(it).disableDragAndDrop()
                    else
                        gameActivity.removeView(it)
                }
                model.execute(ChooseProgressToken(checkNotNull(progressToken)))
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                progressTokenView.visibility = View.VISIBLE
                progressTokenDropZone.alpha = 0F
            }
        }
        return true
    }

    override fun leave() {
        layout.removeView(progressTokenDropZone)
    }
}
