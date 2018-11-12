package fr.omi.sevenwondersduel.app.state

import android.support.constraint.ConstraintSet
import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import android.widget.TextView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.ai.ChoosePlayerBeginningAge
import fr.omi.sevenwondersduel.ai.ConstructWonder
import fr.omi.sevenwondersduel.ai.Discard
import fr.omi.sevenwondersduel.app.*
import fr.omi.sevenwondersduel.effects.PlayerBeginningAgeToChoose
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.CommercialBuilding
import kotlinx.android.synthetic.main.activity_game.*

class PlayAccessibleCardState(gameActivity: GameActivity) : ConstructBuildingState(gameActivity) {
    private val accessibleBuildings = checkNotNull(game.structure).accessibleBuildings()

    private val buildableWonders = game.currentPlayer.wonders.filter { !it.isConstructed() }.map { it.wonder }
    private val discard: TextView get() = gameActivity.discard

    private val discardCoins: TextView get() = gameActivity.discardCoins

    init {
        discard.setOnDragListener { _, event -> discardDragListener(event) }
        accessibleBuildings.forEach { gameActivity.getView(it).enableDragAndDrop() }
        buildableWonders.forEach { gameActivity.getView(it).setOnDragListener { view, event -> wonderDragListener(view as WonderView, event) } }
        if (game.pendingActions.isNotEmpty() && game.pendingActions.first() is PlayerBeginningAgeToChoose) {
            val opponentName = if (game.currentPlayerNumber == 1) "Joueur 2" else "Joueur 1"
            gameActivity.opponentBeginNewAgeButton.apply {
                visibility = View.VISIBLE
                text = gameActivity.resources.getString(R.string.let_opponent_start_new_age, opponentName)
                setOnClickListener { model.execute(ChoosePlayerBeginningAge(if (game.currentPlayerNumber == 1) 2 else 1)) }
            }
            layout.transform {
                clear(gameActivity.opponentBeginNewAgeButton.id, ConstraintSet.START)
                clear(gameActivity.opponentBeginNewAgeButton.id, ConstraintSet.END)
                if (game.currentPlayerNumber == 1)
                    connect(gameActivity.opponentBeginNewAgeButton.id, ConstraintSet.START, layout.id, ConstraintSet.START)
                else
                    connect(gameActivity.opponentBeginNewAgeButton.id, ConstraintSet.END, layout.id, ConstraintSet.END)
            }
        }
    }

    override fun canConstruct(building: Building): Boolean = game.coinsToPay(building) <= game.currentPlayer.coins

    override fun getCurrentPlayerStatus(playerName: String, opponentName: String): String {
        return gameActivity.resources.getString(R.string.player_must_play_a_card, playerName)
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
                discardCoins.bringToFront()
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
                    buildingView.visibility = View.VISIBLE
                    model.execute(ConstructWonder(wonder, checkNotNull(buildingView.building)))
                } else return false
            }
            ACTION_DRAG_ENDED -> wonderView.hideConstructionAvailability()
        }
        return true
    }

    override fun leave() {
        super.leave()
        discard.removeDragListener()
        discardCoins.alpha = 0F
        discard.scaleX = 1F
        discard.scaleY = 1F
        accessibleBuildings.forEach { gameActivity.getView(it).disableDragAndDrop() }
        buildableWonders.forEach {
            gameActivity.getView(it).apply {
                hideConstructionAvailability()
                removeDragListener()
            }
        }
        gameActivity.opponentBeginNewAgeButton.visibility = View.INVISIBLE
    }
}
