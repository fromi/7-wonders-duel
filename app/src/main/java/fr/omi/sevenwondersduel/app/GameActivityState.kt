package fr.omi.sevenwondersduel.app

import android.support.constraint.ConstraintLayout
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.event.Action
import kotlinx.android.synthetic.main.activity_game.*

abstract class GameActivityState(internal val gameActivity: GameActivity) {
    abstract fun handle(action: Action)

    internal val game: SevenWondersDuel
        get() = gameActivity.model.game

    internal val layout: ConstraintLayout
        get() = gameActivity.layout

    internal val model: GameViewModel
        get() = gameActivity.model
}
