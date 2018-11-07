package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fr.omi.sevenwondersduel.PlayerWonder
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.Structure
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.BuildingCard
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    lateinit var model: GameViewModel
    var state: GameActivityState? = null
    val wondersViews: MutableMap<Wonder, WonderView> = hashMapOf()
    private val buildingsViews: MutableMap<Building, BuildingView> = hashMapOf()
    private var structureBuildingsViews: List<Map<Int, BuildingView>> = listOf()
    lateinit var conflictPawnView: ConflictPawnView
    private val progressTokensViews: MutableMap<ProgressToken, ProgressTokenView> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model = ViewModelProviders.of(this).get(GameViewModel::class.java)

        model.game.progressTokensAvailable.forEachIndexed { position, progressToken -> progressTokensViews[progressToken] = ProgressTokenView(this, progressToken).availableAt(position) }
        model.game.players.first.wonders.forEachIndexed { index, wonder -> createView(wonder, 1, index) }
        model.game.players.second.wonders.forEachIndexed { index, wonder -> createView(wonder, 2, index) }
        model.game.players.first.buildings.forEachIndexed { index, it -> buildingsViews[it] = BuildingView(this, it, faceUp = true).positionForPlayer(1, index) }
        model.game.players.second.buildings.forEachIndexed { index, it -> buildingsViews[it] = BuildingView(this, it, faceUp = true).positionForPlayer(2, index) }
        firstPlayerCoins.text = model.game.players.first.coins.toString()
        secondPlayerCoins.text = model.game.players.second.coins.toString()
        conflictPawnView = ConflictPawnView(this, model.game.conflictPawnPosition)
        model.game.structure?.let { display(it) }

        model.actions.observe(this) { action -> state?.handle(action) }

        state = when {
            model.game.wondersAvailable.isNotEmpty() -> WonderSelectionPhase(this)
            model.game.isOver -> GameOverPhase(this)
            else -> GamePhase(this)
        }

        resetButton.setOnClickListener {
            model.reset()
            recreate()
        }
    }

    private fun createView(playerWonder: PlayerWonder, owner: Int, position: Int) {
        val wonderView = WonderView(this, playerWonder.wonder).positionInto(owner, position)
        wondersViews[playerWonder.wonder] = wonderView
        if (playerWonder.buildingUnder != null) {
            BuildingView(this, playerWonder.buildingUnder, faceUp = false).positionUnder(wonderView, owner)
        }
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
