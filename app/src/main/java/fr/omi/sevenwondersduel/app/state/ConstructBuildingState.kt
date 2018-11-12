package fr.omi.sevenwondersduel.app.state

import android.view.DragEvent
import android.view.View
import fr.omi.sevenwondersduel.ai.ConstructBuilding
import fr.omi.sevenwondersduel.app.BuildingView
import fr.omi.sevenwondersduel.app.GameActivity
import fr.omi.sevenwondersduel.app.disableDragAndDrop
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Deck
import fr.omi.sevenwondersduel.material.LumberYard

abstract class ConstructBuildingState(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    private val buildDropZone = BuildingView(gameActivity, Deck.AGE_I, LumberYard, faceUp = false).apply {
        alpha = 0F
        positionToNextBuildingPlace(game)
        setOnDragListener { _, event -> buildingDropListener(event) }
    }

    private fun buildingDropListener(event: DragEvent): Boolean {
        if (event.localState !is BuildingView) return false
        val buildingView = event.localState as BuildingView
        val building = buildingView.building
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                buildDropZone.alpha = 0.5F
                buildDropZone.setImageResource(BuildingView.getResource(checkNotNull(building)))
                buildingView.visibility = View.INVISIBLE
            }
            DragEvent.ACTION_DRAG_ENTERED -> buildDropZone.alpha = 1F
            DragEvent.ACTION_DRAG_EXITED -> buildDropZone.alpha = 0.5F
            DragEvent.ACTION_DROP -> if (canConstruct(building)) {
                buildingView.positionToNextBuildingPlace(game)
                buildingView.visibility = View.VISIBLE
                buildingView.disableDragAndDrop()
                model.execute(ConstructBuilding(checkNotNull(building)))
            } else return false
            DragEvent.ACTION_DRAG_ENDED -> {
                buildDropZone.alpha = 0F
                buildingView.visibility = View.VISIBLE
            }
        }
        return true
    }

    abstract fun canConstruct(building: Building): Boolean

    override fun leave() {
        layout.removeView(buildDropZone)
    }
}