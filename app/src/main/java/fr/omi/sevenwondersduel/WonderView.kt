package fr.omi.sevenwondersduel

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.constraint.ConstraintLayout.LayoutParams.WRAP_CONTENT
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_game.*

@SuppressLint("ViewConstructor")
class WonderView(private val gameActivity: GameActivity, val wonder: Wonder, owner: Int, position: Int) : ImageView(gameActivity) {
    var onSelection: (() -> Unit)? = null

    init {
        when (wonder) {
            Wonder.CIRCUS_MAXIMUS -> {
                setImageResource(R.drawable.circus_maximus)
                contentDescription = resources.getString(R.string.circus_maximus)
            }
            Wonder.PIRAEUS -> {
                setImageResource(R.drawable.piraeus)
                contentDescription = resources.getString(R.string.piraeus)
            }
            Wonder.THE_APPIAN_WAY -> {
                setImageResource(R.drawable.the_appian_way)
                contentDescription = resources.getString(R.string.the_appian_way)
            }
            Wonder.THE_COLOSSUS -> {
                setImageResource(R.drawable.the_colossus)
                contentDescription = resources.getString(R.string.the_colossus)
            }
            Wonder.THE_GREAT_LIBRARY -> {
                setImageResource(R.drawable.the_great_library)
                contentDescription = resources.getString(R.string.the_great_library)
            }
            Wonder.THE_GREAT_LIGHTHOUSE -> {
                setImageResource(R.drawable.the_great_lighthouse)
                contentDescription = resources.getString(R.string.the_great_lighthouse)
            }
            Wonder.THE_HANGING_GARDENS -> {
                setImageResource(R.drawable.the_hanging_gardens)
                contentDescription = resources.getString(R.string.the_hanging_gardens)
            }
            Wonder.THE_MAUSOLEUM -> {
                setImageResource(R.drawable.the_mausoleum)
                contentDescription = resources.getString(R.string.the_mausoleum)
            }
            Wonder.THE_PYRAMIDS -> {
                setImageResource(R.drawable.the_pyramids)
                contentDescription = resources.getString(R.string.the_pyramids)
            }
            Wonder.THE_SPHINX -> {
                setImageResource(R.drawable.the_sphinx)
                contentDescription = resources.getString(R.string.the_sphinx)
            }
            Wonder.THE_STATUE_OF_ZEUS -> {
                setImageResource(R.drawable.the_statue_of_zeus)
                contentDescription = resources.getString(R.string.the_statue_of_zeus)
            }
            Wonder.THE_TEMPLE_OF_ARTEMIS -> {
                setImageResource(R.drawable.the_temple_of_artemis)
                contentDescription = resources.getString(R.string.the_temple_of_artemis)
            }
        }
    }

    init {
        layoutParams = LayoutParams(WRAP_CONTENT, MATCH_CONSTRAINT).apply {
            val pxMargin = (4 * resources.displayMetrics.density).toInt()
            topMargin = pxMargin
            bottomMargin = pxMargin
            adjustViewBounds = true
            when (position) {
                0 -> {
                    topToBottom = gameActivity.boardBottom.id
                    bottomToTop = gameActivity.line3.id
                }
                1 -> {
                    topToBottom = gameActivity.line3.id
                    bottomToTop = gameActivity.line5.id
                }
                2 -> {
                    topToBottom = gameActivity.line5.id
                    bottomToTop = gameActivity.line7.id
                }
                3 -> {
                    topToBottom = gameActivity.line7.id
                    bottomToBottom = gameActivity.layout.id
                }
                else -> throw IllegalArgumentException("Illegal Wonder position: $position")
            }
            when (owner) {
                1 -> {
                    leftToLeft = gameActivity.layout.id
                    leftMargin = pxMargin
                }
                2 -> {
                    rightToRight = gameActivity.layout.id
                    rightMargin = pxMargin
                }
                else -> {
                    leftToLeft = gameActivity.layout.id
                    rightToRight = gameActivity.layout.id
                    setOnTouchListener { view, touchEvent -> availableWonderTouchListener(view as WonderView, touchEvent) }
                }
            }
        }
    }

    private fun availableWonderTouchListener(wonderView: WonderView, touchEvent: MotionEvent): Boolean {
        return if (touchEvent.action == MotionEvent.ACTION_DOWN) {
            val wonder = wonderView.wonder
            wonderView.visibility = View.INVISIBLE
            gameActivity.layout.addView(createWonderDestinationView(wonderView))
            wonderView.startDragAndDrop(wonder)
            true
        } else false
    }

    private fun createWonderDestinationView(wonderView: WonderView): WonderView {
        return WonderView(gameActivity, wonderView.wonder, gameActivity.game.currentPlayer
                ?: 0, gameActivity.game.currentPlayer().wonders.size).apply {
            alpha = 0.5F
            setOnDragListener { destinationView, event -> wonderDestinationDragListener(wonderView, destinationView, event) }
        }
    }

    private fun wonderDestinationDragListener(wonderView: WonderView, destinationView: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> destinationView.alpha = 1F
            DragEvent.ACTION_DRAG_EXITED -> destinationView.alpha = 0.5F
            DragEvent.ACTION_DROP -> wonderView.onSelection?.invoke()
            DragEvent.ACTION_DRAG_ENDED -> if (!event.result) {
                wonderView.visibility = View.VISIBLE
                gameActivity.layout.removeView(destinationView)
            } else {
                destinationView.setOnDragListener(null)
                gameActivity.layout.removeView(wonderView)
            }
        }
        return true
    }
}
