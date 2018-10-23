package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.widget.ImageView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Building.*

@SuppressLint("ViewConstructor")
class BuildingView(context: Context, val building: Building? = null) : ImageView(context) {
    init {
        id = generateViewId()
        setImageResource(getResource(building))
        contentDescription = resources.getString(getContentDescription(building))
        layoutParams = ConstraintLayout.LayoutParams(dpsToPx(35), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        adjustViewBounds = true
    }

    fun positionInStructure(constraintLayout: ConstraintLayout, row: Int, column: Int) {
        if (parent == null) {
            constraintLayout.addView(this)
        }
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.BOTTOM, dpsToPx(row * 30))
        constraintSet.connect(id, ConstraintSet.START, R.id.layout, ConstraintSet.START, if (column > 0) dpsToPx(column * 40) else 0)
        constraintSet.connect(id, ConstraintSet.END, R.id.layout, ConstraintSet.END, if (column < 0) dpsToPx(-column * 40) else 0)
        constraintSet.applyTo(constraintLayout)
    }

    fun positionToNextBuildingPlace(constraintLayout: ConstraintLayout, game: SevenWondersDuel) {
        if (parent == null) {
            constraintLayout.addView(this)
        }
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.clear(id, ConstraintSet.START)
        constraintSet.clear(id, ConstraintSet.END)
        constraintSet.connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.BOTTOM, dpsToPx(game.currentPlayer().buildings.size * 12))
        val constraint = if (game.currentPlayer == 1) ConstraintSet.START else ConstraintSet.END
        constraintSet.connect(id, constraint, R.id.layout, constraint, dpsToPx(100))
        constraintSet.applyTo(constraintLayout)
        bringToFront()
    }

    companion object {
        fun getResource(building: Building?): Int {
            return when (building) {
                LUMBER_YARD -> R.drawable.lumber_yard
                LOGGING_CAMP -> R.drawable.logging_camp
                CLAY_POOL -> R.drawable.clay_pool
                CLAY_PIT -> R.drawable.clay_pit
                QUARRY -> R.drawable.quarry
                STONE_PIT -> R.drawable.stone_pit
                GLASSWORKS -> R.drawable.glassworks
                PRESS -> R.drawable.press
                GUARD_TOWER -> R.drawable.guard_tower
                STABLE -> R.drawable.stable
                GARRISON -> R.drawable.garrison
                PALISADE -> R.drawable.palisade
                WORKSHOP -> R.drawable.workshop
                APOTHECARY -> R.drawable.apothecary
                SCRIPTORIUM -> R.drawable.scriptorium
                PHARMACIST -> R.drawable.pharmacist
                THEATER -> R.drawable.theater
                ALTAR -> R.drawable.altar
                BATHS -> R.drawable.baths
                STONE_RESERVE -> R.drawable.stone_reserve
                CLAY_RESERVE -> R.drawable.clay_reserve
                WOOD_RESERVE -> R.drawable.wood_reserve
                TAVERN -> R.drawable.tavern
                else -> R.drawable.age_1_back
            }
        }

        fun getContentDescription(building: Building?): Int {
            return when (building) {
                LUMBER_YARD -> R.string.lumber_yard
                LOGGING_CAMP -> R.string.logging_camp
                CLAY_POOL -> R.string.clay_pool
                CLAY_PIT -> R.string.clay_pit
                QUARRY -> R.string.quarry
                STONE_PIT -> R.string.stone_pit
                GLASSWORKS -> R.string.glassworks
                PRESS -> R.string.press
                GUARD_TOWER -> R.string.guard_tower
                STABLE -> R.string.stable
                GARRISON -> R.string.garrison
                PALISADE -> R.string.palisade
                WORKSHOP -> R.string.workshop
                APOTHECARY -> R.string.apothecary
                SCRIPTORIUM -> R.string.scriptorium
                PHARMACIST -> R.string.pharmacist
                THEATER -> R.string.theater
                ALTAR -> R.string.altar
                BATHS -> R.string.baths
                STONE_RESERVE -> R.string.stone_reserve
                CLAY_RESERVE -> R.string.clay_reserve
                WOOD_RESERVE -> R.string.wood_reserve
                TAVERN -> R.string.tavern
                else -> R.string.age_1_back
            }
        }
    }
}
