package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.constraint.ConstraintSet
import android.transition.TransitionManager
import android.widget.ImageView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.material.*

@SuppressLint("ViewConstructor")
class WonderView(context: Context, val wonder: Wonder? = null) : ImageView(context) {

    init {
        id = generateViewId()
        setImageResource(getResource(wonder))
        contentDescription = resources.getString(getContentDescription(wonder))
        layoutParams = LayoutParams(dpsToPx(70), MATCH_CONSTRAINT)
        adjustViewBounds = true
    }

    fun positionInto(constraintLayout: ConstraintLayout, owner: Int, position: Int) {
        if (parent == null) {
            constraintLayout.addView(this)
        }
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.BOTTOM, dpsToPx(position * 50))
        when (owner) {
            1 -> {
                constraintSet.connect(id, ConstraintSet.START, R.id.layout, ConstraintSet.START, dpsToPx(4))
                constraintSet.clear(id, ConstraintSet.END)
            }
            2 -> {
                constraintSet.clear(id, ConstraintSet.START)
                constraintSet.connect(id, ConstraintSet.END, R.id.layout, ConstraintSet.END, dpsToPx(4))
            }
            else -> {
                constraintSet.connect(id, ConstraintSet.LEFT, R.id.layout, ConstraintSet.LEFT)
                constraintSet.connect(id, ConstraintSet.RIGHT, R.id.layout, ConstraintSet.RIGHT)
            }
        }
        constraintSet.applyTo(constraintLayout)
    }

    fun moveInto(constraintLayout: ConstraintLayout, owner: Int, position: Int) {
        TransitionManager.beginDelayedTransition(constraintLayout)
        positionInto(constraintLayout, owner, position)
    }

    companion object {
        fun getResource(wonder: Wonder?): Int {
            return when (wonder) {
                is CircusMaximus -> R.drawable.circus_maximus
                is Piraeus -> R.drawable.piraeus
                is TheAppianWay -> R.drawable.the_appian_way
                is TheColossus -> R.drawable.the_colossus
                is TheGreatLibrary -> R.drawable.the_great_library
                is TheGreatLighthouse -> R.drawable.the_great_lighthouse
                is TheHangingGardens -> R.drawable.the_hanging_gardens
                is TheMausoleum -> R.drawable.the_mausoleum
                is ThePyramids -> R.drawable.the_pyramids
                is TheSphinx -> R.drawable.the_sphinx
                is TheStatueOfZeus -> R.drawable.the_statue_of_zeus
                is TheTempleOfArtemis -> R.drawable.the_temple_of_artemis
                else -> R.drawable.wonders_back
            }
        }

        fun getContentDescription(wonder: Wonder?): Int {
            return when (wonder) {
                is CircusMaximus -> R.string.circus_maximus
                is Piraeus -> R.string.piraeus
                is TheAppianWay -> R.string.the_appian_way
                is TheColossus -> R.string.the_colossus
                is TheGreatLibrary -> R.string.the_great_library
                is TheGreatLighthouse -> R.string.the_great_lighthouse
                is TheHangingGardens -> R.string.the_hanging_gardens
                is TheMausoleum -> R.string.the_mausoleum
                is ThePyramids -> R.string.the_pyramids
                is TheSphinx -> R.string.the_sphinx
                is TheStatueOfZeus -> R.string.the_statue_of_zeus
                is TheTempleOfArtemis -> R.string.the_temple_of_artemis
                else -> R.string.wonders_back
            }
        }
    }
}
