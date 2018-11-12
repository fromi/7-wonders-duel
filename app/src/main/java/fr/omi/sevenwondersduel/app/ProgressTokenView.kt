package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.WRAP_CONTENT
import android.support.constraint.ConstraintSet
import android.widget.ImageView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.ProgressToken.*
import fr.omi.sevenwondersduel.material.TheGreatLibrary
import kotlinx.android.synthetic.main.activity_game.*

@SuppressLint("ViewConstructor")
class ProgressTokenView(override val gameActivity: GameActivity, val progressToken: ProgressToken) : ImageView(gameActivity), GameView {

    init {
        id = generateViewId()
        setImageResource(getResource(progressToken))
        contentDescription = resources.getString(getContentDescription(progressToken))
        layoutParams = LayoutParams(dpsToPx(25), WRAP_CONTENT)
        adjustViewBounds = true
        layout.addView(this)
    }

    fun availableAt(position: Int) {
        layout.transform {
            connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.TOP, dpsToPx(7))
            when (position) {
                0 -> {
                    connect(id, ConstraintSet.START, R.id.board, ConstraintSet.START)
                    connect(id, ConstraintSet.END, R.id.board, ConstraintSet.END, dpsToPx(120))
                }
                1 -> {
                    connect(id, ConstraintSet.START, R.id.board, ConstraintSet.START)
                    connect(id, ConstraintSet.END, R.id.board, ConstraintSet.END, dpsToPx(60))
                }
                2 -> {
                    connect(id, ConstraintSet.START, R.id.board, ConstraintSet.START)
                    connect(id, ConstraintSet.END, R.id.board, ConstraintSet.END)
                }
                3 -> {
                    connect(id, ConstraintSet.START, R.id.board, ConstraintSet.START, dpsToPx(60))
                    connect(id, ConstraintSet.END, R.id.board, ConstraintSet.END)
                }
                4 -> {
                    connect(id, ConstraintSet.START, R.id.board, ConstraintSet.START, dpsToPx(120))
                    connect(id, ConstraintSet.END, R.id.board, ConstraintSet.END)
                }
                else -> throw IllegalArgumentException("Illegal progress token position: $position")
            }
        }
    }

    fun positionForPlayer(playerNumber: Int, position: Int) {
        layout.transform {
            clear(id, ConstraintSet.TOP)
            clear(id, ConstraintSet.START)
            clear(id, ConstraintSet.END)
            connect(id, ConstraintSet.BOTTOM, gameActivity.board.id, ConstraintSet.BOTTOM, dpsToPx(8))
            when (playerNumber) {
                1 -> connect(id, ConstraintSet.START, gameActivity.firstPlayerCoins.id, ConstraintSet.END, dpsToPx(position * 30 + 5))
                2 -> connect(id, ConstraintSet.END, gameActivity.secondPlayerCoins.id, ConstraintSet.START, dpsToPx(position * 30 + 5))
            }
        }
    }

    fun positionOnTheGreatLibrary(position: Int) {
        layout.transform {
            connect(id, ConstraintSet.START, gameActivity.getView(TheGreatLibrary).id, ConstraintSet.START, dpsToPx(position * 21 + 1))
            connect(id, ConstraintSet.TOP, gameActivity.getView(TheGreatLibrary).id, ConstraintSet.TOP)
            connect(id, ConstraintSet.BOTTOM, gameActivity.getView(TheGreatLibrary).id, ConstraintSet.BOTTOM)
        }
    }

    companion object {
        fun getResource(progressToken: ProgressToken): Int {
            return when (progressToken) {
                AGRICULTURE -> R.drawable.progress_agriculture
                ARCHITECTURE -> R.drawable.progress_architecture
                ECONOMY -> R.drawable.progress_economy
                LAW -> R.drawable.progress_law
                MASONRY -> R.drawable.progress_masonry
                MATHEMATICS -> R.drawable.progress_mathematics
                PHILOSOPHY -> R.drawable.progress_philosophy
                STRATEGY -> R.drawable.progress_strategy
                THEOLOGY -> R.drawable.progress_theology
                URBANISM -> R.drawable.progress_urbanism
            }
        }

        fun getContentDescription(progressToken: ProgressToken): Int {
            return when (progressToken) {
                AGRICULTURE -> R.string.progress_agriculture
                ARCHITECTURE -> R.string.progress_architecture
                ECONOMY -> R.string.progress_economy
                LAW -> R.string.progress_law
                MASONRY -> R.string.progress_masonry
                MATHEMATICS -> R.string.progress_mathematics
                PHILOSOPHY -> R.string.progress_philosophy
                STRATEGY -> R.string.progress_strategy
                THEOLOGY -> R.string.progress_theology
                URBANISM -> R.string.progress_urbanism
            }
        }
    }
}
