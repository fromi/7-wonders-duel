package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.constraint.ConstraintLayout.LayoutParams.WRAP_CONTENT
import android.support.constraint.ConstraintSet
import android.transition.TransitionManager
import android.widget.ImageView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.Wonder
import fr.omi.sevenwondersduel.Wonder.*

@SuppressLint("ViewConstructor")
class WonderView(context: Context, val wonder: Wonder? = null) : ImageView(context) {

    init {
        id = generateViewId()
        setImageResource(getResource(wonder))
        contentDescription = resources.getString(getContentDescription(wonder))
        layoutParams = LayoutParams(WRAP_CONTENT, MATCH_CONSTRAINT)
        adjustViewBounds = true
    }

    fun positionInto(constraintLayout: ConstraintLayout, owner: Int, position: Int) {
        if (parent == null) {
            constraintLayout.addView(this)
        }
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        val pxMargin = (4 * resources.displayMetrics.density).toInt()
        when (position) {
            0 -> {
                constraintSet.connect(id, ConstraintSet.TOP, R.id.boardBottom, ConstraintSet.BOTTOM, pxMargin)
                constraintSet.connect(id, ConstraintSet.BOTTOM, R.id.line3, ConstraintSet.TOP, pxMargin)
            }
            1 -> {
                constraintSet.connect(id, ConstraintSet.TOP, R.id.line3, ConstraintSet.BOTTOM, pxMargin)
                constraintSet.connect(id, ConstraintSet.BOTTOM, R.id.line5, ConstraintSet.TOP, pxMargin)
            }
            2 -> {
                constraintSet.connect(id, ConstraintSet.TOP, R.id.line5, ConstraintSet.BOTTOM, pxMargin)
                constraintSet.connect(id, ConstraintSet.BOTTOM, R.id.line7, ConstraintSet.TOP, pxMargin)
            }
            3 -> {
                constraintSet.connect(id, ConstraintSet.TOP, R.id.line7, ConstraintSet.BOTTOM, pxMargin)
                constraintSet.connect(id, ConstraintSet.BOTTOM, R.id.layout, ConstraintSet.BOTTOM, pxMargin)
            }
            else -> throw IllegalArgumentException("Illegal Wonder position: $position")
        }
        when (owner) {
            1 -> {
                constraintSet.connect(id, ConstraintSet.START, R.id.layout, ConstraintSet.START, pxMargin)
                constraintSet.clear(id, ConstraintSet.END)
            }
            2 -> {
                constraintSet.clear(id, ConstraintSet.START)
                constraintSet.connect(id, ConstraintSet.END, R.id.layout, ConstraintSet.END, pxMargin)
            }
            else -> {
                constraintSet.connect(id, ConstraintSet.LEFT, R.id.layout, ConstraintSet.LEFT, 0)
                constraintSet.connect(id, ConstraintSet.RIGHT, R.id.layout, ConstraintSet.RIGHT, 0)
            }
        }
        constraintSet.applyTo(constraintLayout)
    }

    fun positionToNextWonderPlace(constraintLayout: ConstraintLayout, game: SevenWondersDuel) {
        positionInto(constraintLayout, checkNotNull(game.currentPlayer), game.currentPlayer().wonders.size)
    }

    fun moveInto(constraintLayout: ConstraintLayout, owner: Int, position: Int) {
        TransitionManager.beginDelayedTransition(constraintLayout)
        positionInto(constraintLayout, owner, position)
    }

    companion object {
        fun getResource(wonder: Wonder?): Int {
            return when (wonder) {
                CIRCUS_MAXIMUS -> R.drawable.circus_maximus
                PIRAEUS -> R.drawable.piraeus
                THE_APPIAN_WAY -> R.drawable.the_appian_way
                THE_COLOSSUS -> R.drawable.the_colossus
                THE_GREAT_LIBRARY -> R.drawable.the_great_library
                THE_GREAT_LIGHTHOUSE -> R.drawable.the_great_lighthouse
                THE_HANGING_GARDENS -> R.drawable.the_hanging_gardens
                THE_MAUSOLEUM -> R.drawable.the_mausoleum
                THE_PYRAMIDS -> R.drawable.the_pyramids
                THE_SPHINX -> R.drawable.the_sphinx
                THE_STATUE_OF_ZEUS -> R.drawable.the_statue_of_zeus
                THE_TEMPLE_OF_ARTEMIS -> R.drawable.the_temple_of_artemis
                else -> R.drawable.wonders_back
            }
        }

        fun getContentDescription(wonder: Wonder?): Int {
            return when (wonder) {
                CIRCUS_MAXIMUS -> R.string.circus_maximus
                PIRAEUS -> R.string.piraeus
                THE_APPIAN_WAY -> R.string.the_appian_way
                THE_COLOSSUS -> R.string.the_colossus
                THE_GREAT_LIBRARY -> R.string.the_great_library
                THE_GREAT_LIGHTHOUSE -> R.string.the_great_lighthouse
                THE_HANGING_GARDENS -> R.string.the_hanging_gardens
                THE_MAUSOLEUM -> R.string.the_mausoleum
                THE_PYRAMIDS -> R.string.the_pyramids
                THE_SPHINX -> R.string.the_sphinx
                THE_STATUE_OF_ZEUS -> R.string.the_statue_of_zeus
                THE_TEMPLE_OF_ARTEMIS -> R.string.the_temple_of_artemis
                else -> R.string.wonders_back
            }
        }
    }
}
