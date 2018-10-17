package fr.omi.sevenwondersduel

import android.os.Bundle
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.constraint.ConstraintLayout.LayoutParams.WRAP_CONTENT
import android.support.constraint.Guideline
import android.support.v7.app.AppCompatActivity
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import fr.omi.sevenwondersduel.Wonder.*
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : AppCompatActivity() {

    var game = SevenWondersDuel()

    private fun createHorizontalGuidelinesLayout(topGuideline: Guideline, bottomGuideline: Guideline, dpMargin: Int = 0): LayoutParams {
        val pxMargin = (dpMargin * resources.displayMetrics.density).toInt()
        return LayoutParams(WRAP_CONTENT, MATCH_CONSTRAINT).apply {
            topToBottom = topGuideline.id
            bottomToTop = bottomGuideline.id
            leftToLeft = layout.id
            rightToRight = layout.id
            topMargin = pxMargin
            bottomMargin = pxMargin
        }
    }

    private fun createImage(wonder: Wonder): ImageView {
        return ImageView(this).apply {
            when (wonder) {
                CIRCUS_MAXIMUS -> {
                    setImageResource(R.drawable.circus_maximus)
                    contentDescription = resources.getString(R.string.circus_maximus)
                }
                PIRAEUS -> {
                    setImageResource(R.drawable.piraeus)
                    contentDescription = resources.getString(R.string.piraeus)
                }
                THE_APPIAN_WAY -> {
                    setImageResource(R.drawable.the_appian_way)
                    contentDescription = resources.getString(R.string.the_appian_way)
                }
                THE_COLOSSUS -> {
                    setImageResource(R.drawable.the_colossus)
                    contentDescription = resources.getString(R.string.the_colossus)
                }
                THE_GREAT_LIBRARY -> {
                    setImageResource(R.drawable.the_great_library)
                    contentDescription = resources.getString(R.string.the_great_library)
                }
                THE_GREAT_LIGHTHOUSE -> {
                    setImageResource(R.drawable.the_great_lighthouse)
                    contentDescription = resources.getString(R.string.the_great_lighthouse)
                }
                THE_HANGING_GARDENS -> {
                    setImageResource(R.drawable.the_hanging_gardens)
                    contentDescription = resources.getString(R.string.the_hanging_gardens)
                }
                THE_MAUSOLEUM -> {
                    setImageResource(R.drawable.the_mausoleum)
                    contentDescription = resources.getString(R.string.the_mausoleum)
                }
                THE_PYRAMIDS -> {
                    setImageResource(R.drawable.the_pyramids)
                    contentDescription = resources.getString(R.string.the_pyramids)
                }
                THE_SPHINX -> {
                    setImageResource(R.drawable.the_sphinx)
                    contentDescription = resources.getString(R.string.the_sphinx)
                }
                THE_STATUE_OF_ZEUS -> {
                    setImageResource(R.drawable.the_statue_of_zeus)
                    contentDescription = resources.getString(R.string.the_statue_of_zeus)
                }
                THE_TEMPLE_OF_ARTEMIS -> {
                    setImageResource(R.drawable.the_temple_of_artemis)
                    contentDescription = resources.getString(R.string.the_temple_of_artemis)
                }
            }
        }
    }

    private fun displayAvailableWonder(wonder: Wonder, guidelines: Pair<Guideline, Guideline>) {
        layout.addView(createImage(wonder).apply {
            layoutParams = createHorizontalGuidelinesLayout(guidelines.first, guidelines.second, 4)
            setOnTouchListener { wonderImage, touchEvent ->
                return@setOnTouchListener if (touchEvent.action == MotionEvent.ACTION_DOWN) {
                    wonderImage.visibility = View.INVISIBLE
                    layout.addView(createImage(wonder).apply {
                        val pxMargin = (4 * resources.displayMetrics.density).toInt()
                        layoutParams = LayoutParams(WRAP_CONTENT, MATCH_CONSTRAINT).apply {
                            adjustViewBounds = true
                            topToBottom = guidelines.first.id
                            bottomToTop = guidelines.second.id
                            if (game.currentPlayer == 1) {
                                leftToLeft = layout.id
                                leftMargin = pxMargin
                            } else {
                                rightToRight = layout.id
                                rightMargin = pxMargin
                            }
                            topMargin = pxMargin
                            bottomMargin = pxMargin
                            alpha = 0.5F
                        }
                        setOnDragListener { destinationImage, event ->
                            when (event.action) {
                                DragEvent.ACTION_DRAG_ENTERED -> destinationImage.alpha = 1F
                                DragEvent.ACTION_DRAG_EXITED -> destinationImage.alpha = 0.5F
                                DragEvent.ACTION_DROP -> game = game.choose(event.localState as Wonder)
                                DragEvent.ACTION_DRAG_ENDED -> if (!event.result) {
                                    wonderImage.visibility = View.VISIBLE
                                    layout.removeView(destinationImage)
                                } else {
                                    destinationImage.setOnDragListener(null)
                                    layout.removeView(wonderImage)
                                }
                            }
                            return@setOnDragListener true
                        }
                    })
                    val myShadow = View.DragShadowBuilder(wonderImage)
                    if (android.os.Build.VERSION.SDK_INT >= 24) {
                        wonderImage.startDragAndDrop(null, myShadow, wonder, 0)
                    } else {
                        @Suppress("DEPRECATION")
                        wonderImage.startDrag(null, myShadow, wonder, 0)
                    }
                    true
                } else false
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val wondersLayoutParams = listOf(Pair(boardBottom, line3), Pair(line3, line5), Pair(line5, line7), Pair(line7, bottomLine))
        game.wondersAvailable.zip(wondersLayoutParams).forEach { pair -> displayAvailableWonder(pair.first, pair.second) }
    }
}
