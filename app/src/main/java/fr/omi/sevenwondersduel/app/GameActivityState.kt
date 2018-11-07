package fr.omi.sevenwondersduel.app

import android.support.constraint.ConstraintLayout
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.event.Action
import fr.omi.sevenwondersduel.event.GameEvent
import kotlinx.android.synthetic.main.activity_game.*

abstract class GameActivityState(internal val gameActivity: GameActivity) {
    open fun handle(action: Action) {
        action.inferEventsLeadingTo(game).forEach(::handleEvent)
    }

    abstract fun handleEvent(event: GameEvent)

    internal val game: SevenWondersDuel
        get() = gameActivity.model.game

    internal val layout: ConstraintLayout
        get() = gameActivity.layout

    internal val model: GameViewModel
        get() = gameActivity.model
}
