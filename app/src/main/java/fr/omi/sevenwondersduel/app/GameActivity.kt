package fr.omi.sevenwondersduel.app

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.DragEvent
import android.view.DragEvent.*
import android.view.MotionEvent
import android.view.View
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
        wonderDropZone.setOnDragListener { _, event -> wonderDropListener(event) }
    }

    private fun createAvailableWonder(position: Int, wonder: Wonder) {
        val wonderView = WonderView(this, wonder)
        wondersViews[wonder] = wonderView
        layout.addView(wonderView)
        positionWonder(wonderView, owner = 0, position = position)
        wonderView.setOnTouchListener(::availableWonderTouchListener)
    }

    private fun availableWonderTouchListener(view: View, touchEvent: MotionEvent): Boolean {
        return if (touchEvent.action == MotionEvent.ACTION_DOWN) {
            view.startDragAndDrop()
            true
        } else false
    }

    private fun wonderDropListener(event: DragEvent): Boolean {
        if (event.localState !is WonderView) return false
        val wonderView = event.localState as WonderView
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                wonderDropZone.alpha = 0.5F
                wonderDropZone.setImageResource(WonderView.getResource(wonderView.wonder))
                wonderView.visibility = View.INVISIBLE
            }
            ACTION_DRAG_ENTERED -> wonderDropZone.alpha = 1F
            ACTION_DRAG_EXITED -> wonderDropZone.alpha = 0.5F
            ACTION_DROP -> {
                positionWonder(wonderView, game.currentPlayer!!, game.currentPlayer().wonders.size)
                wonderView.removeOnTouchListener()
                GameViewModel.choose(wonderView.wonder)
            }
            ACTION_DRAG_ENDED -> {
                wonderView.visibility = View.VISIBLE
                if (game.wondersAvailable.isEmpty()) {
                    layout.removeView(wonderDropZone)
                } else {
                    wonderDropZone.alpha = 0F
                    if (event.result) {
                        positionWonder(wonderDropZone, game.currentPlayer!!, game.currentPlayer().wonders.size)
                    }
                }
                if (event.result) {
                    if (game.wondersAvailable.isEmpty()) {
                        TransitionManager.beginDelayedTransition(layout)
                        positionWonder(wondersViews[game.players.second.wonders[3].wonder]!!, 2, 3)
                    } else if (game.wondersAvailable.size == 4) {
                        TransitionManager.beginDelayedTransition(layout)
                        positionWonder(wondersViews[game.players.first.wonders[1].wonder]!!, 1, 1)
                        game.wondersAvailable.forEachIndexed(::createAvailableWonder)
                    }
                }
            }
        }
        return true
    }

    private fun positionWonder(view: View, owner: Int, position: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)
        val pxMargin = (4 * resources.displayMetrics.density).toInt()
        when (position) {
            0 -> {
                constraintSet.connect(view.id, ConstraintSet.TOP, R.id.boardBottom, ConstraintSet.BOTTOM, pxMargin)
                constraintSet.connect(view.id, ConstraintSet.BOTTOM, R.id.line3, ConstraintSet.TOP, pxMargin)
            }
            1 -> {
                constraintSet.connect(view.id, ConstraintSet.TOP, R.id.line3, ConstraintSet.BOTTOM, pxMargin)
                constraintSet.connect(view.id, ConstraintSet.BOTTOM, R.id.line5, ConstraintSet.TOP, pxMargin)
            }
            2 -> {
                constraintSet.connect(view.id, ConstraintSet.TOP, R.id.line5, ConstraintSet.BOTTOM, pxMargin)
                constraintSet.connect(view.id, ConstraintSet.BOTTOM, R.id.line7, ConstraintSet.TOP, pxMargin)
            }
            3 -> {
                constraintSet.connect(view.id, ConstraintSet.TOP, R.id.line7, ConstraintSet.BOTTOM, pxMargin)
                constraintSet.connect(view.id, ConstraintSet.BOTTOM, R.id.layout, ConstraintSet.BOTTOM, pxMargin)
            }
            else -> throw IllegalArgumentException("Illegal Wonder position: $position")
        }
        when (owner) {
            1 -> {
                constraintSet.connect(view.id, ConstraintSet.START, R.id.layout, ConstraintSet.START, pxMargin)
                constraintSet.clear(view.id, ConstraintSet.END)
            }
            2 -> {
                constraintSet.clear(view.id, ConstraintSet.START)
                constraintSet.connect(view.id, ConstraintSet.END, R.id.layout, ConstraintSet.END, pxMargin)
            }
            else -> {
                constraintSet.connect(view.id, ConstraintSet.LEFT, R.id.layout, ConstraintSet.LEFT, 0)
                constraintSet.connect(view.id, ConstraintSet.RIGHT, R.id.layout, ConstraintSet.RIGHT, 0)
            }
        }
        constraintSet.applyTo(layout)
    }

}

object GameViewModel {
    var game = SevenWondersDuel()
        private set

    fun choose(wonder: Wonder) {
        game = game.choose(wonder)
    }
}