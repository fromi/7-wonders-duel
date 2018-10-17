package fr.omi.sevenwondersduel

import android.os.Bundle
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.constraint.Guideline
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import fr.omi.sevenwondersduel.Wonder.*
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : AppCompatActivity() {

    val game = SevenWondersDuel()

    private fun createHorizontalGuidelinesLayout(topGuideline: Guideline, bottomGuideline: Guideline, dpMargin: Int = 0): LayoutParams {
        val params = LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
        params.topToBottom = topGuideline.id
        params.bottomToTop = bottomGuideline.id
        params.leftToLeft = layout.id
        params.rightToRight = layout.id
        val pxMargin = (dpMargin * resources.displayMetrics.density).toInt()
        params.topMargin = pxMargin
        params.bottomMargin = pxMargin
        return params
    }

    private fun createImage(wonder: Wonder): ImageView {
        val imageView = ImageView(this)
        when (wonder) {
            CIRCUS_MAXIMUS -> {
                imageView.setImageResource(R.drawable.circus_maximus)
                imageView.contentDescription = resources.getString(R.string.circus_maximus)
            }
            PIRAEUS -> {
                imageView.setImageResource(R.drawable.piraeus)
                imageView.contentDescription = resources.getString(R.string.piraeus)
            }
            THE_APPIAN_WAY -> {
                imageView.setImageResource(R.drawable.the_appian_way)
                imageView.contentDescription = resources.getString(R.string.the_appian_way)
            }
            THE_COLOSSUS -> {
                imageView.setImageResource(R.drawable.the_colossus)
                imageView.contentDescription = resources.getString(R.string.the_colossus)
            }
            THE_GREAT_LIBRARY -> {
                imageView.setImageResource(R.drawable.the_great_library)
                imageView.contentDescription = resources.getString(R.string.the_great_library)
            }
            THE_GREAT_LIGHTHOUSE -> {
                imageView.setImageResource(R.drawable.the_great_lighthouse)
                imageView.contentDescription = resources.getString(R.string.the_great_lighthouse)
            }
            THE_HANGING_GARDENS -> {
                imageView.setImageResource(R.drawable.the_hanging_gardens)
                imageView.contentDescription = resources.getString(R.string.the_hanging_gardens)
            }
            THE_MAUSOLEUM -> {
                imageView.setImageResource(R.drawable.the_mausoleum)
                imageView.contentDescription = resources.getString(R.string.the_mausoleum)
            }
            THE_PYRAMIDS -> {
                imageView.setImageResource(R.drawable.the_pyramids)
                imageView.contentDescription = resources.getString(R.string.the_pyramids)
            }
            THE_SPHINX -> {
                imageView.setImageResource(R.drawable.the_sphinx)
                imageView.contentDescription = resources.getString(R.string.the_sphinx)
            }
            THE_STATUE_OF_ZEUS -> {
                imageView.setImageResource(R.drawable.the_statue_of_zeus)
                imageView.contentDescription = resources.getString(R.string.the_statue_of_zeus)
            }
            THE_TEMPLE_OF_ARTEMIS -> {
                imageView.setImageResource(R.drawable.the_temple_of_artemis)
                imageView.contentDescription = resources.getString(R.string.the_temple_of_artemis)
            }
        }
        return imageView
    }

    private fun displayAvailableWonder(wonder: Wonder, layoutParams: LayoutParams) {
        val imageView = createImage(wonder)
        imageView.layoutParams = layoutParams
        layout.addView(imageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val wondersLayoutParams = listOf(createHorizontalGuidelinesLayout(boardBottom, line3, 4),
                createHorizontalGuidelinesLayout(line3, line5, 4),
                createHorizontalGuidelinesLayout(line5, line7, 4),
                createHorizontalGuidelinesLayout(line7, bottomLine, 4))
        game.wondersAvailable.zip(wondersLayoutParams).forEach { pair -> displayAvailableWonder(pair.first, pair.second) }
    }
}
