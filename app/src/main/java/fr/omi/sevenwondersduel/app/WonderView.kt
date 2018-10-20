package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.constraint.ConstraintLayout.LayoutParams.WRAP_CONTENT
import android.view.MotionEvent
import android.widget.ImageView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.Wonder

@SuppressLint("ViewConstructor")
class WonderView(private val gameActivity: GameActivity, val wonder: Wonder, owner: Int) : ImageView(gameActivity) {

    init {
        id = generateViewId()
        setImageResource(getResource(wonder))
        contentDescription = when (wonder) {
            Wonder.CIRCUS_MAXIMUS -> resources.getString(R.string.circus_maximus)
            Wonder.PIRAEUS -> resources.getString(R.string.piraeus)
            Wonder.THE_APPIAN_WAY -> resources.getString(R.string.the_appian_way)
            Wonder.THE_COLOSSUS -> resources.getString(R.string.the_colossus)
            Wonder.THE_GREAT_LIBRARY -> resources.getString(R.string.the_great_library)
            Wonder.THE_GREAT_LIGHTHOUSE -> resources.getString(R.string.the_great_lighthouse)
            Wonder.THE_HANGING_GARDENS -> resources.getString(R.string.the_hanging_gardens)
            Wonder.THE_MAUSOLEUM -> resources.getString(R.string.the_mausoleum)
            Wonder.THE_PYRAMIDS -> resources.getString(R.string.the_pyramids)
            Wonder.THE_SPHINX -> resources.getString(R.string.the_sphinx)
            Wonder.THE_STATUE_OF_ZEUS -> resources.getString(R.string.the_statue_of_zeus)
            Wonder.THE_TEMPLE_OF_ARTEMIS -> resources.getString(R.string.the_temple_of_artemis)
        }
        layoutParams = LayoutParams(WRAP_CONTENT, MATCH_CONSTRAINT)
        adjustViewBounds = true
        if (owner == 0) {
            setOnTouchListener { _, touchEvent -> availableWonderTouchListener(touchEvent) }
        }
    }

    private fun availableWonderTouchListener(touchEvent: MotionEvent): Boolean {
        return if (touchEvent.action == MotionEvent.ACTION_DOWN) {
            startDragAndDrop()
            true
        } else false
    }

    companion object {
        fun getResource(wonder: Wonder): Int {
            return when (wonder) {
                Wonder.CIRCUS_MAXIMUS -> R.drawable.circus_maximus
                Wonder.PIRAEUS -> R.drawable.piraeus
                Wonder.THE_APPIAN_WAY -> R.drawable.the_appian_way
                Wonder.THE_COLOSSUS -> R.drawable.the_colossus
                Wonder.THE_GREAT_LIBRARY -> R.drawable.the_great_library
                Wonder.THE_GREAT_LIGHTHOUSE -> R.drawable.the_great_lighthouse
                Wonder.THE_HANGING_GARDENS -> R.drawable.the_hanging_gardens
                Wonder.THE_MAUSOLEUM -> R.drawable.the_mausoleum
                Wonder.THE_PYRAMIDS -> R.drawable.the_pyramids
                Wonder.THE_SPHINX -> R.drawable.the_sphinx
                Wonder.THE_STATUE_OF_ZEUS -> R.drawable.the_statue_of_zeus
                Wonder.THE_TEMPLE_OF_ARTEMIS -> R.drawable.the_temple_of_artemis
            }
        }
    }
}
