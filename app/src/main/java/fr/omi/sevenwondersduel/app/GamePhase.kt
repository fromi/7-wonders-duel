package fr.omi.sevenwondersduel.app

import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.ai.ConstructBuilding
import fr.omi.sevenwondersduel.ai.Discard
import fr.omi.sevenwondersduel.event.Action
import fr.omi.sevenwondersduel.event.BuildingMadeAccessibleEvent
import fr.omi.sevenwondersduel.event.GameEvent
import fr.omi.sevenwondersduel.material.CommercialBuilding
import fr.omi.sevenwondersduel.material.Deck
import fr.omi.sevenwondersduel.material.LumberYard
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

    override fun handle(action: Action) {
        gameActivity.firstPlayerCoins.text = game.players.first.coins.toString()
        gameActivity.secondPlayerCoins.text = game.players.second.coins.toString()
        if (game.conflictPawnPosition != gameActivity.conflictPawnView.position) {
            gameActivity.conflictPawnView.position = game.conflictPawnPosition
        }
        super.handle(action)
    }

    override fun handleEvent(event: GameEvent) {
        when (event) {
            is BuildingMadeAccessibleEvent -> gameActivity.getView(event.building).apply {
                reveal()
                enableDragAndDrop()
            }
        }
    }

    private fun createBuildingDropZone(): BuildingView {
        // TODO improve building and wonder drop zone
        return BuildingView(gameActivity, Deck.AGE_I, LumberYard, faceUp = false).apply {
            alpha = 0F
            positionToNextBuildingPlace(game)
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
                buildingView.positionToNextBuildingPlace(game)
                buildingView.disableDragAndDrop()
                model.execute(ConstructBuilding(checkNotNull(buildingView.building)))
            }
            ACTION_DRAG_ENDED -> {
                buildDropZone.alpha = 0F
                if (event.result) {
                    buildDropZone.positionToNextBuildingPlace(game)
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
                discardCoins.text = discardCoins.resources.getString(R.string.plus_coins, 2 + game.currentPlayer.buildings.count { it is CommercialBuilding })
                discardCoins.alpha = 0.5F
                buildingView.visibility = View.INVISIBLE
            }
            ACTION_DRAG_ENTERED -> discardCoins.alpha = 1F
            ACTION_DRAG_EXITED -> discardCoins.alpha = 0.5F
            ACTION_DROP -> {
                gameActivity.remove(buildingView)
                model.execute(Discard(checkNotNull(buildingView.building)))
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
