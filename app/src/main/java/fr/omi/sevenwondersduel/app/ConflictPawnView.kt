package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.transition.TransitionManager
import android.widget.ImageView
import fr.omi.sevenwondersduel.R
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.math.abs

@SuppressLint("ViewConstructor")
class ConflictPawnView(override val gameActivity: GameActivity, position: Int) : ImageView(gameActivity), GameView {
    init {
        id = generateViewId()
        setImageResource(R.drawable.shield)
        contentDescription = resources.getString(R.string.conflict_pawn_position)
        layoutParams = ConstraintLayout.LayoutParams(dpsToPx(15), dpsToPx(15))
        adjustViewBounds = true
        gameActivity.layout.addView(this)
        updatePosition(position)
    }

    var position = position
        set(position) {
            TransitionManager.beginDelayedTransition(layout)
            updatePosition(position)
        }

    private fun updatePosition(position: Int) {
        layout.transform {
            val margin = dpsToPx(256 * abs(position) / 9)
            connect(id, ConstraintSet.START, gameActivity.board.id, ConstraintSet.START, if (position > 0) margin else 0)
            connect(id, ConstraintSet.END, gameActivity.board.id, ConstraintSet.END, if (position < 0) margin else 0)
            connect(id, ConstraintSet.TOP, gameActivity.board.id, ConstraintSet.TOP, dpsToPx(12))
            connect(id, ConstraintSet.BOTTOM, gameActivity.board.id, ConstraintSet.BOTTOM)
        }
    }
}