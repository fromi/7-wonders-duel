package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fr.omi.sevenwondersduel.PlayerWonder
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.Structure
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    lateinit var model: GameViewModel
    lateinit var state: GameActivityState
    private val wondersViews: MutableMap<Wonder, WonderView> = hashMapOf()
    private val buildingsViews: MutableMap<Building, BuildingView> = hashMapOf()
    private var structureBuildingsViews: List<Map<Int, BuildingView>> = listOf()
    private lateinit var conflictPawnView: ConflictPawnView
    private val progressTokensViews: MutableMap<ProgressToken, ProgressTokenView> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model = ViewModelProviders.of(this).get(GameViewModel::class.java)

        model.game.observe(this) { game ->
            game.progressTokensAvailable.forEachIndexed { position, progressToken -> progressTokensViews.getOrPut(progressToken) { ProgressTokenView(this, progressToken) }.availableAt(position) }
            game.wondersAvailable.forEachIndexed { index, wonder -> createView(wonder, 0, index) }
            game.players.first.wonders.forEachIndexed { index, wonder -> createView(wonder, 1, index) }
            game.players.second.wonders.forEachIndexed { index, wonder -> createView(wonder, 2, index) }
            game.players.first.buildings.forEachIndexed { index, it -> BuildingView(this, it).apply { positionForPlayer(layout, 1, index) } }
            game.players.second.buildings.forEachIndexed { index, it -> BuildingView(this, it).apply { positionForPlayer(layout, 2, index) } }
            firstPlayerCoins.text = game.players.first.coins.toString()
            secondPlayerCoins.text = game.players.second.coins.toString()
            conflictPawnView = ConflictPawnView(this, game.conflictPawnPosition)
            state = when {
                game.wondersAvailable.isNotEmpty() -> WonderSelectionPhase(this)
                game.structure != null -> {
                    display(game.structure)
                    GamePhase(this)
                }
                else -> GameOverPhase(this)
            }
        }
    }

    private fun createView(playerWonder: PlayerWonder, owner: Int, position: Int): WonderView {
        val wonderView = createView(playerWonder, owner, position)
        if (playerWonder.isBuild()) {
            BuildingView(this, playerWonder.buildingUnder!!).positionUnder(layout, wonderView, owner)
        }
        return wonderView
    }

    fun createView(wonder: Wonder, owner: Int, position: Int): WonderView {
        return wondersViews.getOrPut(wonder) { WonderView(this, wonder) }.apply {
            positionInto(layout, owner, position)
        }
    }

    fun getView(wonder: Wonder): WonderView {
        return checkNotNull(wondersViews[wonder])
    }

    fun display(structure: Structure) {
        structureBuildingsViews = structure.mapIndexed { rowIndex, row ->
            row.mapValues { entry ->
                if (structure.isFaceUp(rowIndex, entry.key))
                    createView(entry.value, rowIndex, entry.key)
                else
                    createView(entry.value.deck, rowIndex, entry.key)
            }
        }
    }

    private fun createView(building: Building, row: Int, column: Int): BuildingView {
        return buildingsViews.getOrPut(building) { BuildingView(this, building) }.apply {
            positionInStructure(layout, row, column)
        }
    }

    private fun createView(deck: Building.Deck, row: Int, column: Int): BuildingView {
        return BuildingView(this, deck).apply {
            positionInStructure(layout, row, column)
        }
    }

    fun getView(building: Building): BuildingView {
        return checkNotNull(buildingsViews[building])
    }

    fun remove(buildingView: BuildingView) {
        layout.removeView(buildingView)
        buildingsViews.remove(buildingView.building)
    }

}
