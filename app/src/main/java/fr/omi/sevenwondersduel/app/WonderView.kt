package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.material.*

@SuppressLint("ViewConstructor")
class WonderView(override val gameActivity: GameActivity, val wonder: Wonder? = null) : ImageView(gameActivity), GameView {

    init {
        id = generateViewId()
        setImageResource(getResource(wonder))
        contentDescription = resources.getString(getContentDescription(wonder))
        layoutParams = LayoutParams(dpsToPx(70), MATCH_CONSTRAINT)
        adjustViewBounds = true
        layout.addView(this)
    }

    fun positionInto(owner: Int, position: Int): WonderView {
        layout.transform {
            connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.BOTTOM, dpsToPx(position * 50))
            when (owner) {
                1 -> {
                    connect(id, ConstraintSet.START, R.id.layout, ConstraintSet.START, dpsToPx(4))
                    clear(id, ConstraintSet.END)
                }
                2 -> {
                    clear(id, ConstraintSet.START)
                    connect(id, ConstraintSet.END, R.id.layout, ConstraintSet.END, dpsToPx(4))
                }
                else -> {
                    connect(id, ConstraintSet.LEFT, R.id.layout, ConstraintSet.LEFT)
                    connect(id, ConstraintSet.RIGHT, R.id.layout, ConstraintSet.RIGHT)
                }
            }
        }
        return this
    }

    private lateinit var constructionAvailability: TextView

    fun showConstructionAvailable(cost: Int) {
        createConstructionAvailabilityView(cost, android.R.color.black)
    }

    fun showConstructionUnavailable(cost: Int) {
        createConstructionAvailabilityView(cost, android.R.color.holo_red_dark)
    }

    private fun createConstructionAvailabilityView(cost: Int, color: Int) {
        constructionAvailability = TextView(gameActivity).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(dpsToPx(20), dpsToPx(20))
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.coin)
            setTextSize(COMPLEX_UNIT_SP, 10F)
            text = if (cost == 0) "0" else resources.getString(R.string.minus_coins, cost)
            setTextColor(ContextCompat.getColor(gameActivity, color))
        }
        layout.addView(constructionAvailability)
        layout.transform {
            connect(constructionAvailability.id, ConstraintSet.TOP, id, ConstraintSet.TOP)
            connect(constructionAvailability.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM)
            connect(constructionAvailability.id, ConstraintSet.LEFT, id, ConstraintSet.LEFT, dpsToPx(20))
        }
    }

    fun showConstructionDrop() {
        constructionAvailability.scaleX = 1.2F
        constructionAvailability.scaleY = 1.2F
    }

    fun hideConstructionDrop() {
        constructionAvailability.scaleX = 1F
        constructionAvailability.scaleY = 1F
    }

    fun hideConstructionAvailability() {
        layout.removeView(constructionAvailability)
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
