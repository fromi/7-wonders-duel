package fr.omi.sevenwondersduel.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import fr.omi.sevenwondersduel.ProgressToken
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.Wonder
import fr.omi.sevenwondersduel.app.GameViewModel.game
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private val wondersViews: MutableMap<Wonder, WonderView> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        game.wondersAvailable.forEachIndexed(::createAvailableWonder)
        WonderView(this).apply {
            alpha = 0F
            positionToNextWonderPlace(layout, game)
            setOnDragListener { view, event -> wonderDropListener(view as WonderView, event) }
        }
        game.progressTokensAvailable.forEachIndexed(::createProgressToken)
    }

    private fun createAvailableWonder(position: Int, wonder: Wonder) {
        wondersViews[wonder] = WonderView(this, wonder).apply {
            positionInto(layout, owner = 0, position = position)
            enableDragAndDrop()
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
                wonderView.positionToNextWonderPlace(layout, game)
                wonderView.disableDragAndDrop()
                GameViewModel.choose(wonder)
            }
            ACTION_DRAG_ENDED -> {
                wonderView.visibility = View.VISIBLE
                if (game.wondersAvailable.isEmpty()) {
                    layout.removeView(dropZone)
                } else {
                    dropZone.alpha = 0F
                    if (event.result) {
                        dropZone.positionToNextWonderPlace(layout, game)
                    }
                }
                if (event.result) {
                    if (game.wondersAvailable.isEmpty()) {
                        checkNotNull(wondersViews[game.players.second.wonders[3].wonder]).moveInto(layout, 2, 3)
                    } else if (game.wondersAvailable.size == 4) {
                        checkNotNull(wondersViews[game.players.first.wonders[1].wonder]).moveInto(layout, 1, 1)
                        game.wondersAvailable.forEachIndexed(::createAvailableWonder)
                    }
                }
            }
        }
        return true
    }
}

object GameViewModel {
    var game = SevenWondersDuel()
        private set

    fun choose(wonder: Wonder) {
        game = game.choose(wonder)
    }
}