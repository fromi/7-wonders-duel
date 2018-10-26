package fr.omi.sevenwondersduel.app

import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import fr.omi.sevenwondersduel.R
import kotlinx.android.synthetic.main.activity_game.*

class GamePhase(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    private val buildDropZone: BuildingView

    private val discard: ImageView
        get() = gameActivity.discard

    private val discardCoins: TextView
        get() = gameActivity.discardCoins


    init {
        buildDropZone = createBuildingDropZone()
        discard.setOnDragListener { _, event -> discardDropListener(event) }
        checkNotNull(game.structure).accessibleBuildings().forEach { building -> gameActivity.getView(building).enableDragAndDrop() }
    }

    private fun createBuildingDropZone(): BuildingView {
        return BuildingView(gameActivity, game.structure!!.age).apply {
            alpha = 0F
            positionToNextBuildingPlace(layout, model.game)
            setOnDragListener { _, event -> buildingDropListener(event) }
        }
    }

    private fun buildingDropListener(event: DragEvent): Boolean {
        if (event.localState !is BuildingView) return false
        val buildingView = event.localState as BuildingView
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                buildDropZone.alpha = 0.5F
                buildDropZone.setImageResource(BuildingView.getResource(checkNotNull(buildingView.building)))
                buildDropZone.bringToFront()
            }
            ACTION_DRAG_ENTERED -> buildDropZone.alpha = 1F
            ACTION_DRAG_EXITED -> buildDropZone.alpha = 0.5F
            ACTION_DROP -> {
                buildingView.positionToNextBuildingPlace(layout, model.game)
                buildingView.disableDragAndDrop()
                model.build(checkNotNull(buildingView.building))
            }
            ACTION_DRAG_ENDED -> {
                buildDropZone.alpha = 0F
                if (event.result) {
                    buildDropZone.positionToNextBuildingPlace(layout, model.game)
                }
            }
        }
        return true
    }

    private fun discardDropListener(event: DragEvent): Boolean {
        if (event.localState !is BuildingView) return false
        val buildingView = event.localState as BuildingView
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                discard.scaleX = 1.2F
                discard.scaleY = 1.2F
                discardCoins.text = discardCoins.resources.getString(R.string.plus_coins, game.currentPlayer().getDiscardCoins())
                discardCoins.alpha = 0.5F
                buildingView.visibility = View.INVISIBLE
            }
            ACTION_DRAG_ENTERED -> discardCoins.alpha = 1F
            ACTION_DRAG_EXITED -> discardCoins.alpha = 0.5F
            ACTION_DROP -> {
                gameActivity.remove(buildingView)
                model.discard(checkNotNull(buildingView.building))
            }
            ACTION_DRAG_ENDED -> {
                discardCoins.alpha = 0F
                discard.scaleX = 1F
                discard.scaleY = 1F
                buildingView.visibility = View.VISIBLE
            }
        }
        return true
    }
}
