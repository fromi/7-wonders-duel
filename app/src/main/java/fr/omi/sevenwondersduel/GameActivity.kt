package fr.omi.sevenwondersduel

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    var game = SevenWondersDuel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        game.wondersAvailable.forEachIndexed(::displayAvailableWonder)
    }

    private fun displayAvailableWonder(position: Int, wonder: Wonder) {
        layout.addView(WonderView(this, wonder, owner = 0, position = position).apply {
            onSelection = { game = game.choose(wonder) }
        })
    }
}
