package fr.omi.sevenwondersduel.app

import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View

class GamePhase(gameActivity: GameActivity) : GameActivityState(gameActivity) {
    init {
        createBuildingDropZone()
        game.structure!!.accessibleBuildings().forEach { building -> gameActivity.getView(building).enableDragAndDrop() }
    }

    private fun createBuildingDropZone() {
        BuildingView(gameActivity).apply {
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
                dropZone.bringToFront()
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
