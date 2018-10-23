package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private val model = ViewModelProviders.of(this).get(GameViewModel::class.java)
    private val wondersViews: MutableMap<Wonder, WonderView> = hashMapOf()
    private var structureBuildingsViews: List<Map<Int, BuildingView>> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        model.game.wondersAvailable.forEachIndexed(::createAvailableWonder)
        createWonderDropZone()
        model.game.progressTokensAvailable.forEachIndexed(::createProgressToken)
    }

    private fun createAvailableWonder(position: Int, wonder: Wonder) {
        wondersViews[wonder] = WonderView(this, wonder).apply {
            positionInto(layout, owner = 0, position = position)
            enableDragAndDrop()
        }
    }

    private fun createWonderDropZone() {
        WonderView(this).apply {
            alpha = 0F
            positionToNextWonderPlace(layout, model.game)
            setOnDragListener { view, event -> wonderDropListener(view as WonderView, event) }
        }
    }

    private fun createProgressToken(position: Int, progressToken: ProgressToken) {
        ProgressTokenView(this, progressToken).apply {
            positionOnBoard(layout, position)
        }
    }

    private fun wonderDropListener(dropZone: WonderView, event: DragEvent): Boolean {
        if (event.localState !is WonderView) return false
        val wonderView = event.localState as WonderView
        val wonder = checkNotNull(wonderView.wonder)
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                dropZone.alpha = 0.5F
                dropZone.setImageResource(WonderView.getResource(wonder))
                wonderView.visibility = View.INVISIBLE
            }
            ACTION_DRAG_ENTERED -> dropZone.alpha = 1F
            ACTION_DRAG_EXITED -> dropZone.alpha = 0.5F
            ACTION_DROP -> {
                wonderView.positionToNextWonderPlace(layout, model.game)
                wonderView.disableDragAndDrop()
                model.choose(wonder)
            }
            ACTION_DRAG_ENDED -> {
                wonderView.visibility = View.VISIBLE
                if (model.game.wondersAvailable.isEmpty()) {
                    layout.removeView(dropZone)
                } else {
                    dropZone.alpha = 0F
                    if (event.result) {
                        dropZone.positionToNextWonderPlace(layout, model.game)
                    }
                }
                if (event.result) {
                    if (model.game.wondersAvailable.isEmpty()) {
                        checkNotNull(wondersViews[model.game.players.second.wonders[3].wonder]).moveInto(layout, 2, 3)
                        displayStructure()
                        createBuildingDropZone()
                    } else if (model.game.wondersAvailable.size == 4) {
                        checkNotNull(wondersViews[model.game.players.first.wonders[1].wonder]).moveInto(layout, 1, 1)
                        model.game.wondersAvailable.forEachIndexed(::createAvailableWonder)
                    }
                }
            }
        }
        return true
    }

    private fun displayStructure() {
        structureBuildingsViews = model.game.structure.mapIndexed { rowIndex, row -> row.mapValues { entry -> createBuilding(entry.value, rowIndex, entry.key) } }
        structureBuildingsViews.last().values.forEach { it.enableDragAndDrop() }
    }

    private fun createBuilding(building: Building, row: Int, column: Int): BuildingView {
        return (if (row % 2 == 0) BuildingView(this, building) else BuildingView(this)).apply {
            positionInStructure(layout, row, column)
        }
    }

    private fun createBuildingDropZone() {
        BuildingView(this).apply {
            alpha = 0F
            positionToNextBuildingPlace(layout, model.game)
            setOnDragListener { view, event -> buildingDropListener(view as BuildingView, event) }
        }
    }

    private fun buildingDropListener(dropZone: BuildingView, event: DragEvent): Boolean {
        if (event.localState !is BuildingView) return false
        val buildingView = event.localState as BuildingView
        val building = checkNotNull(buildingView.building)
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                dropZone.alpha = 0.5F
                dropZone.setImageResource(BuildingView.getResource(building))
                buildingView.visibility = View.INVISIBLE
            }
            ACTION_DRAG_ENTERED -> dropZone.alpha = 1F
            ACTION_DRAG_EXITED -> dropZone.alpha = 0.5F
            ACTION_DROP -> {
                buildingView.positionToNextBuildingPlace(layout, model.game)
                buildingView.disableDragAndDrop()
                model.build(building)
            }
            ACTION_DRAG_ENDED -> {
                buildingView.visibility = View.VISIBLE
                dropZone.alpha = 0F
                if (event.result) {
                    dropZone.positionToNextBuildingPlace(layout, model.game)
                }
            }
        }
        return true
    }
}
