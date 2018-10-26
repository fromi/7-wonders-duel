package fr.omi.sevenwondersduel.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fr.omi.sevenwondersduel.BuildableWonder
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    val model = GameViewModel()
    lateinit var state: GameActivityState
    private val wondersViews: MutableMap<Wonder, WonderView> = hashMapOf()
    private val buildingsViews: MutableMap<Building, BuildingView> = hashMapOf()
    private var structureBuildingsViews: List<Map<Int, BuildingView>> = listOf()
    private lateinit var conflictPawnView: ConflictPawnView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        model.playRandomMoves(60)
        model.game.progressTokensAvailable.forEachIndexed { position, progressToken -> createView(progressToken, position) }
        model.game.wondersAvailable.forEachIndexed { index, wonder -> createView(wonder, 0, index) }
        state = if (model.game.wondersAvailable.isNotEmpty()) {
            WonderSelectionPhase(this)
        } else {
            displayStructure()
            GamePhase(this)
        }
        model.game.players.first.wonders.forEachIndexed { index, wonder -> createView(wonder, 1, index) }
        model.game.players.second.wonders.forEachIndexed { index, wonder -> createView(wonder, 2, index) }
        model.game.players.first.buildings.forEachIndexed { index, it -> BuildingView(this, it).apply { positionForPlayer(layout, 1, index) } }
        model.game.players.second.buildings.forEachIndexed { index, it -> BuildingView(this, it).apply { positionForPlayer(layout, 2, index) } }
        firstPlayerCoins.text = model.game.players.first.coins.toString()
        secondPlayerCoins.text = model.game.players.second.coins.toString()
        conflictPawnView = ConflictPawnView(this, model.game.conflictPawnPosition)
    }

    private fun createView(progressToken: ProgressToken, position: Int) {
        ProgressTokenView(this, progressToken).apply {
            positionOnBoard(layout, position)
        }
    }

    private fun createView(buildableWonder: BuildableWonder, owner: Int, position: Int): WonderView {
        val wonderView = createView(buildableWonder.wonder, owner, position)
        if (buildableWonder.isBuild()) {
            BuildingView(this, buildableWonder.builtWith!!).positionUnder(layout, wonderView, owner)
        }
        return wonderView
    }

    fun createView(wonder: Wonder, owner: Int, position: Int): WonderView {
        check(!wondersViews.containsKey(wonder))
        val wonderView = WonderView(this, wonder)
        wondersViews[wonder] = wonderView.apply {
            positionInto(layout, owner, position)
        }
        return wonderView
    }

    fun getView(wonder: Wonder): WonderView {
        return checkNotNull(wondersViews[wonder])
    }

    fun displayStructure() {
        val structure = checkNotNull(model.game.structure)
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
        val buildingView = BuildingView(this, building).apply {
            positionInStructure(layout, row, column)
        }
        buildingsViews[building] = buildingView
        return buildingView
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
