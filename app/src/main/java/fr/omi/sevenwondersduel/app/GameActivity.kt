package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fr.omi.sevenwondersduel.PlayerWonder
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.Structure
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.BuildingCard
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    lateinit var model: GameViewModel
    lateinit var state: GameActivityState
    val wondersViews: MutableMap<Wonder, WonderView> = hashMapOf()
    private val buildingsViews: MutableMap<Building, BuildingView> = hashMapOf()
    private var structureBuildingsViews: List<Map<Int, BuildingView>> = listOf()
    private lateinit var conflictPawnView: ConflictPawnView
    private val progressTokensViews: MutableMap<ProgressToken, ProgressTokenView> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model = ViewModelProviders.of(this).get(GameViewModel::class.java)

        val game = checkNotNull(model.game.value)
        game.progressTokensAvailable.forEachIndexed { position, progressToken -> progressTokensViews[progressToken] = ProgressTokenView(this, progressToken).availableAt(position) }
        game.players.first.wonders.forEachIndexed { index, wonder -> createView(wonder, 1, index) }
        game.players.second.wonders.forEachIndexed { index, wonder -> createView(wonder, 2, index) }
        game.players.first.buildings.forEachIndexed { index, it -> buildingsViews[it] = BuildingView(this, it, faceUp = true).positionForPlayer(1, index) }
        game.players.second.buildings.forEachIndexed { index, it -> buildingsViews[it] = BuildingView(this, it, faceUp = true).positionForPlayer(2, index) }
        firstPlayerCoins.text = game.players.first.coins.toString()
        secondPlayerCoins.text = game.players.second.coins.toString()
        conflictPawnView = ConflictPawnView(this, game.conflictPawnPosition)
        if (game.structure != null) display(game.structure)

        state = when {
            game.wondersAvailable.isNotEmpty() -> WonderSelectionPhase(this)
            game.isOver -> GameOverPhase(this)
            else -> GamePhase(this)
        }

        model.game.observe(this, ::updateGame)
    }

    private fun updateGame(game: SevenWondersDuel) {
        firstPlayerCoins.text = game.players.first.coins.toString()
        secondPlayerCoins.text = game.players.second.coins.toString()
        if (game.conflictPawnPosition != conflictPawnView.position) {
            conflictPawnView.position = game.conflictPawnPosition
        }
    }

    private fun createView(playerWonder: PlayerWonder, owner: Int, position: Int) {
        val wonderView = WonderView(this, playerWonder.wonder).positionInto(owner, position)
        wondersViews[playerWonder.wonder] = wonderView
        if (playerWonder.buildingUnder != null) {
            BuildingView(this, playerWonder.buildingUnder, faceUp = false).positionUnder(wonderView, owner)
        }
    }

    fun getView(wonder: Wonder): WonderView {
        return checkNotNull(wondersViews[wonder])
    }

    fun display(structure: Structure) {
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
        buildingsViews.remove(buildingView.building)
    }

}
