package fr.omi.sevenwondersduel.app.state

import android.support.constraint.ConstraintLayout
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.app.GameActivity
import fr.omi.sevenwondersduel.app.GameViewModel
import kotlinx.android.synthetic.main.activity_game.*

abstract class GameActivityState(internal val gameActivity: GameActivity) {
    internal val game: SevenWondersDuel
        get() = gameActivity.model.game

    internal val layout: ConstraintLayout
        get() = gameActivity.layout

    internal val model: GameViewModel
        get() = gameActivity.model

    abstract fun displayPlayerStatuses()
    abstract fun leave()
}
