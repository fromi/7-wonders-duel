package fr.omi.sevenwondersduel

import android.os.Bundle
import android.support.constraint.ConstraintLayout.LayoutParams
import android.support.constraint.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : AppCompatActivity() {

    val game = SevenWondersDuel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.the_great_library)
        val params = LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
        imageView.layoutParams = params
        params.topToBottom = guideline.id
        params.bottomToTop = guideline2.id
        params.leftToLeft = layout.id
        params.rightToRight = layout.id
        params.topMargin = (4 * resources.displayMetrics.density).toInt()
        params.bottomMargin = (4 * resources.displayMetrics.density).toInt()
        imageView.contentDescription = resources.getString(R.string.the_great_library)
        layout.addView(imageView)

        val imageView2 = ImageView(this)
        imageView2.setImageResource(R.drawable.the_sphinx)
        val params2 = LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
        imageView2.layoutParams = params2
        params2.topToBottom = guideline2.id
        params2.bottomToTop = guideline3.id
        params2.leftToLeft = layout.id
        params2.rightToRight = layout.id
        params2.topMargin = (4 * resources.displayMetrics.density).toInt()
        params2.bottomMargin = (4 * resources.displayMetrics.density).toInt()
        imageView2.contentDescription = resources.getString(R.string.the_sphinx)
        layout.addView(imageView2)
    }
}
