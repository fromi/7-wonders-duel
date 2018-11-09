package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fr.omi.sevenwondersduel.PlayerWonder
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.Structure
import fr.omi.sevenwondersduel.app.state.GameActivityState
import fr.omi.sevenwondersduel.app.state.GameOverState
import fr.omi.sevenwondersduel.app.state.PlayAccessibleCardState
import fr.omi.sevenwondersduel.app.state.TakeWonderState
import fr.omi.sevenwondersduel.event.*
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.BuildingCard
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    lateinit var model: GameViewModel
    private val game get() = model.game
    private var state: GameActivityState? = null
    private val wondersViews: MutableMap<Wonder, WonderView> = hashMapOf()
    private val buildingsViews: MutableMap<Building, BuildingView> = hashMapOf()
    private var structureBuildingsViews: List<Map<Int, BuildingView>> = listOf()
    private lateinit var conflictPawnView: ConflictPawnView
    private val progressTokensViews: MutableMap<ProgressToken, ProgressTokenView> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model = ViewModelProviders.of(this).get(GameViewModel::class.java)

        game.wondersAvailable.forEachIndexed(this::createAvailableWonderView)
        game.progressTokensAvailable.forEachIndexed { position, progressToken -> progressTokensViews[progressToken] = ProgressTokenView(this, progressToken).availableAt(position) }
        game.players.first.wonders.forEachIndexed { index, wonder -> createView(wonder, 1, index) }
        game.players.second.wonders.forEachIndexed { index, wonder -> createView(wonder, 2, index) }
        game.players.first.buildings.forEachIndexed { index, it -> buildingsViews[it] = BuildingView(this, it, faceUp = true).positionForPlayer(1, index) }
        game.players.second.buildings.forEachIndexed { index, it -> buildingsViews[it] = BuildingView(this, it, faceUp = true).positionForPlayer(2, index) }
        firstPlayerCoins.text = game.players.first.coins.toString()
        secondPlayerCoins.text = game.players.second.coins.toString()
        conflictPawnView = ConflictPawnView(this, game.conflictPawnPosition)
        if (game.players.first.militaryTokensLooted >= 1) layout.removeView(loot2player1)
        if (game.players.first.militaryTokensLooted >= 2) layout.removeView(loot5player1)
        if (game.players.second.militaryTokensLooted >= 1) layout.removeView(loot2player2)
        if (game.players.second.militaryTokensLooted >= 2) layout.removeView(loot5player2)
        game.structure?.let { display(it) }

        model.actions.observe(this) { action ->
            if (state != null) handle(action)
            updateState()
        }
        updateState()

        resetButton.setOnClickListener {
            model.reset()
            recreate()
        }
    }

    private fun updateState() {
        state?.leave()
        state = when {
            game.wondersAvailable.isNotEmpty() -> TakeWonderState(this)
            game.isOver -> GameOverState(this)
            else -> PlayAccessibleCardState(this)
        }
    }

    private fun handle(action: Action) {
        firstPlayerCoins.text = game.players.first.coins.toString()
        secondPlayerCoins.text = game.players.second.coins.toString()
        if (game.conflictPawnPosition != conflictPawnView.position) {
            conflictPawnView.position = game.conflictPawnPosition
        }
        action.inferEventsLeadingTo(model.game).forEach(::handleEvent)
    }

    private fun handleEvent(event: GameEvent) {
        when (event) {
            is PlaceFourAvailableWondersEvent -> event.wonders.forEachIndexed(this::createAvailableWonderView)
            is PrepareStructureEvent -> display(event.structure)
            is BuildingMadeAccessibleEvent -> getView(event.building).reveal()
            is MilitaryTokenLooted -> when (event.playerNumber) {
                1 -> when (event.tokenNumber) {
                    1 -> layout.removeView(loot2player1)
                    2 -> layout.removeView(loot5player1)
                }
                2 -> when (event.tokenNumber) {
                    1 -> layout.removeView(loot2player2)
                    2 -> layout.removeView(loot5player2)
                }
            }
        }
    }

    private fun createAvailableWonderView(position: Int, wonder: Wonder) {
        createView(wonder).positionInto(0, position)
    }

    private fun createView(playerWonder: PlayerWonder, owner: Int, position: Int) {
        val wonderView = createView(playerWonder.wonder)
        wonderView.positionInto(owner, position)
        if (playerWonder.buildingUnder != null) {
            BuildingView(this, playerWonder.buildingUnder, faceUp = false).positionUnder(wonderView, owner)
        }
    }

    private fun createView(wonder: Wonder): WonderView {
        val wonderView = WonderView(this, wonder)
        wondersViews[wonder] = wonderView
        return wonderView
    }

    private fun display(structure: Structure) {
        structureBuildingsViews = structure.mapIndexed { rowIndex, row ->
            row.mapValues { entry -> createView(entry.value, rowIndex, entry.key) }
        }
    }

    private fun createView(buildingCard: BuildingCard, row: Int, column: Int): BuildingView {
        return buildingsViews.getOrPut(buildingCard.building) { BuildingView(this, buildingCard) }.apply {
            positionInStructure(row, column)
        }
    }

    fun getView(building: Building): BuildingView {
        return checkNotNull(buildingsViews[building])
    }

    fun remove(buildingView: BuildingView) {
        layout.removeView(buildingView)
    }

    fun getView(wonder: Wonder): WonderView {
        return checkNotNull(wondersViews[wonder])
    }
}
