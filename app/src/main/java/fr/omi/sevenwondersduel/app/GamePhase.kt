package fr.omi.sevenwondersduel.app

import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.ai.ConstructBuilding
import fr.omi.sevenwondersduel.ai.ConstructWonder
import fr.omi.sevenwondersduel.ai.Discard
import fr.omi.sevenwondersduel.event.Action
import fr.omi.sevenwondersduel.event.BuildingMadeAccessibleEvent
import fr.omi.sevenwondersduel.event.GameEvent
import fr.omi.sevenwondersduel.event.PrepareStructureEvent
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
        discard.setOnDragListener { _, event -> discardDragListener(event) }
        checkNotNull(game.structure).accessibleBuildings().forEach { building -> gameActivity.getView(building).enableDragAndDrop() }
        game.players.toList().flatMap { it.wonders }.filter { !it.isConstructed() }.map { it.wonder }
                .forEach { gameActivity.getView(it).setOnDragListener { view, event -> wonderDragListener(view as WonderView, event) } }
    }

    override fun handle(action: Action) {
        gameActivity.firstPlayerCoins.text = game.players.first.coins.toString()
        gameActivity.secondPlayerCoins.text = game.players.second.coins.toString()
        if (game.conflictPawnPosition != gameActivity.conflictPawnView.position) {
            gameActivity.conflictPawnView.position = game.conflictPawnPosition
        }
        if (action.move is ConstructBuilding || action.move is Discard || action.move is ConstructWonder) {
            buildDropZone.positionToNextBuildingPlace(game)
            buildDropZone.bringToFront()
        }
        when (action.move) {
            is ConstructBuilding -> gameActivity.getView(action.move.building).disableDragAndDrop()
            is Discard -> gameActivity.remove(gameActivity.getView(action.move.building))
            is ConstructWonder -> {
                gameActivity.getView(action.move.buildingUsed).disableDragAndDrop()
                gameActivity.getView(action.move.wonder).removeDragListener()
            }
        }
        super.handle(action)
    }

    override fun handleEvent(event: GameEvent) {
        when (event) {
            is BuildingMadeAccessibleEvent -> gameActivity.getView(event.building).apply {
                reveal()
                enableDragAndDrop()
            }
            is PrepareStructureEvent -> {
                gameActivity.display(event.structure)
                event.structure.accessibleBuildings().forEach { building -> gameActivity.getView(building).enableDragAndDrop() }
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
        val constructionCost = game.coinsToPay(buildingView.building)
        val constructionEnabled = constructionCost <= game.currentPlayer.coins
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                buildDropZone.alpha = 0.5F
                buildDropZone.setImageResource(BuildingView.getResource(checkNotNull(buildingView.building)))
            }
            ACTION_DRAG_ENTERED -> buildDropZone.alpha = 1F
            ACTION_DRAG_EXITED -> buildDropZone.alpha = 0.5F
            ACTION_DROP -> if (constructionEnabled) {
                buildingView.positionToNextBuildingPlace(game)
                model.execute(ConstructBuilding(checkNotNull(buildingView.building)))
            } else return false
            ACTION_DRAG_ENDED -> buildDropZone.alpha = 0F
        }
        return true
    }

    private fun discardDragListener(event: DragEvent): Boolean {
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
            ACTION_DROP -> model.execute(Discard(checkNotNull(buildingView.building)))
            ACTION_DRAG_ENDED -> {
                discardCoins.alpha = 0F
                discard.scaleX = 1F
                discard.scaleY = 1F
                buildingView.visibility = View.VISIBLE
            }
        }
        return true
    }

    private fun wonderDragListener(wonderView: WonderView, event: DragEvent): Boolean {
        if (event.localState !is BuildingView) return false
        val buildingView = event.localState as BuildingView
        val wonder = checkNotNull(wonderView.wonder)
        val constructionCost = game.coinsToPay(wonder)
        val constructionEnabled = constructionCost <= game.currentPlayer.coins
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                if (game.currentPlayer.wonders.none { !it.isConstructed() && it.wonder == wonder }) return false
                if (constructionEnabled) wonderView.showConstructionAvailable(constructionCost) else wonderView.showConstructionUnavailable(constructionCost)
            }
            ACTION_DRAG_ENTERED -> if (constructionEnabled) wonderView.showConstructionDrop()
            ACTION_DRAG_EXITED -> if (constructionEnabled) wonderView.hideConstructionDrop()
            ACTION_DROP -> {
                if (constructionEnabled) {
                    buildingView.positionUnder(wonderView, checkNotNull(game.currentPlayerNumber))
                    model.execute(ConstructWonder(wonder, checkNotNull(buildingView.building)))
                } else return false
            }
            ACTION_DRAG_ENDED -> wonderView.hideConstructionAvailability()
        }
        return true
    }
}
