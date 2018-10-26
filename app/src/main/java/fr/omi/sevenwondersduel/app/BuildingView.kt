package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.widget.ImageView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Building.*

@SuppressLint("ViewConstructor")
class BuildingView(context: Context, private val deck: Deck, val building: Building? = null) : ImageView(context) {

    constructor(context: GameActivity, building: Building) : this(context, building.deck, building)

    init {
        id = generateViewId()
        setImageResource(if (building != null) getResource(building) else getResource(deck))
        contentDescription = resources.getString(if (building != null) getContentDescription(building) else getContentDescription(deck))
        layoutParams = ConstraintLayout.LayoutParams(dpsToPx(35), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        adjustViewBounds = true
    }

    fun positionInStructure(constraintLayout: ConstraintLayout, row: Int, column: Int) {
        if (parent == null) {
            constraintLayout.addView(this)
        }
        ConstraintSet().apply {
            clone(constraintLayout)
            connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.BOTTOM, dpsToPx(row * 30))
            connect(id, ConstraintSet.START, R.id.layout, ConstraintSet.START, if (column > 0) dpsToPx(column * 40) else 0)
            connect(id, ConstraintSet.END, R.id.layout, ConstraintSet.END, if (column < 0) dpsToPx(-column * 40) else 0)
        }.applyTo(constraintLayout)
    }

    fun positionForPlayer(constraintLayout: ConstraintLayout, player: Int, position: Int) {
        if (parent == null) {
            constraintLayout.addView(this)
        }
        ConstraintSet().apply {
            clone(constraintLayout)
            clear(id, ConstraintSet.START)
            clear(id, ConstraintSet.END)
            connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.BOTTOM, dpsToPx(position * 12))
            val constraint = if (player == 1) ConstraintSet.START else ConstraintSet.END
            connect(id, constraint, R.id.layout, constraint, dpsToPx(100))
        }.applyTo(constraintLayout)
    }

    fun positionToNextBuildingPlace(constraintLayout: ConstraintLayout, game: SevenWondersDuel) {
        positionForPlayer(constraintLayout, checkNotNull(game.currentPlayer), game.currentPlayer().buildings.size)
        bringToFront()
    }

    fun positionUnder(constraintLayout: ConstraintLayout, wonderView: WonderView, player: Int) {
        putFaceDown()
        rotation = if (player == 1) 270F else 90F
        if (parent == null) {
            constraintLayout.addView(this)
        }
        wonderView.bringToFront()
        ConstraintSet().apply {
            clone(constraintLayout)
            connect(id, ConstraintSet.TOP, wonderView.id, ConstraintSet.TOP)
            connect(id, ConstraintSet.BOTTOM, wonderView.id, ConstraintSet.BOTTOM)
            val constraint = if (player == 1) ConstraintSet.START else ConstraintSet.END
            connect(id, constraint, wonderView.id, constraint, dpsToPx(40))
        }.applyTo(constraintLayout)
    }

    private fun putFaceDown() {
        setImageResource(getResource(deck))
        contentDescription = resources.getString(getContentDescription(deck))
    }

    companion object {
        fun getResource(building: Building): Int {
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
                else -> TODO()
            }
        }

        fun getResource(deck: Building.Deck): Int {
            return when (deck) {
                AGE_I -> R.drawable.age_1_back
                AGE_II -> R.drawable.age_2_back
                AGE_III -> R.drawable.age_3_back
                else -> R.drawable.guild_back
            }
        }

        fun getContentDescription(building: Building): Int {
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
                else -> TODO()
            }
        }

        fun getContentDescription(deck: Building.Deck): Int {
            return when (deck) {
                AGE_I -> R.string.age_1_back
                AGE_II -> R.string.age_2_back
                AGE_III -> R.string.age_3_back
                else -> R.string.guild_back
            }
        }
    }
}
