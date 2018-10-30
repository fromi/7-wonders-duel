package fr.omi.sevenwondersduel.app

import android.support.constraint.ConstraintLayout
import kotlinx.android.synthetic.main.activity_game.*

interface GameView {
    val gameActivity: GameActivity

    val layout: ConstraintLayout get() = gameActivity.layout
}
