package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.constraint.ConstraintLayout.LayoutParams.WRAP_CONTENT
import android.support.constraint.ConstraintSet
import android.widget.ImageView
import fr.omi.sevenwondersduel.ProgressToken
import fr.omi.sevenwondersduel.ProgressToken.*
import fr.omi.sevenwondersduel.R

@SuppressLint("ViewConstructor")
class ProgressTokenView(context: Context, progressToken: ProgressToken) : ImageView(context) {

    init {
        id = generateViewId()
        setImageResource(getResource(progressToken))
        contentDescription = resources.getString(getContentDescription(progressToken))
        layoutParams = LayoutParams(MATCH_CONSTRAINT, WRAP_CONTENT)
        adjustViewBounds = true
    }

    fun positionOnBoard(constraintLayout: ConstraintLayout, position: Int) {
        if (parent == null) {
            constraintLayout.addView(this)
        }
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        val sideMargin = (2 * resources.displayMetrics.density).toInt()
        val topMargin = (6 * resources.displayMetrics.density).toInt()
        constraintSet.connect(id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP, topMargin)
        when (position) {
            0 -> {
                constraintSet.connect(id, ConstraintSet.LEFT, R.id.progressGuide1, ConstraintSet.RIGHT, sideMargin)
                constraintSet.connect(id, ConstraintSet.RIGHT, R.id.progressGuide2, ConstraintSet.LEFT, sideMargin)
            }
            1 -> {
                constraintSet.connect(id, ConstraintSet.LEFT, R.id.progressGuide2, ConstraintSet.RIGHT, sideMargin)
                constraintSet.connect(id, ConstraintSet.RIGHT, R.id.progressGuide3, ConstraintSet.LEFT, sideMargin)
            }
            2 -> {
                constraintSet.connect(id, ConstraintSet.LEFT, R.id.progressGuide3, ConstraintSet.RIGHT, sideMargin)
                constraintSet.connect(id, ConstraintSet.RIGHT, R.id.progressGuide4, ConstraintSet.LEFT, sideMargin)
            }
            3 -> {
                constraintSet.connect(id, ConstraintSet.LEFT, R.id.progressGuide4, ConstraintSet.RIGHT, sideMargin)
                constraintSet.connect(id, ConstraintSet.RIGHT, R.id.progressGuide5, ConstraintSet.LEFT, sideMargin)
            }
            4 -> {
                constraintSet.connect(id, ConstraintSet.LEFT, R.id.progressGuide5, ConstraintSet.RIGHT, sideMargin)
                constraintSet.connect(id, ConstraintSet.RIGHT, R.id.progressGuide6, ConstraintSet.LEFT, sideMargin)
            }
            else -> throw IllegalArgumentException("Illegal Wonder position: $position")
        }
        constraintSet.applyTo(constraintLayout)
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
