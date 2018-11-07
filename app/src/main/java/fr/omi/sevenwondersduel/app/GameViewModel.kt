package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.ai.ConstructWonder
import fr.omi.sevenwondersduel.ai.RandomBot
import fr.omi.sevenwondersduel.ai.SevenWondersDuelMove
import fr.omi.sevenwondersduel.event.Action

class GameViewModel : ViewModel() {
    var game = SevenWondersDuel()
    val actions = MutableLiveData<Action>()

    init {
        game = RandomBot.playUntilMoveAvailable { it is ConstructWonder }
    }

    fun execute(move: SevenWondersDuelMove) {
        actions.postValue(Action(game, move))
        game = move.applyTo(game)
    }

    fun reset() {
        game = SevenWondersDuel()
    }
}