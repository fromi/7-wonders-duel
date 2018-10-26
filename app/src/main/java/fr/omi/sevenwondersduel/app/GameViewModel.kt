package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.ViewModel
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.ai.RandomBot
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

class GameViewModel : ViewModel() {
    var game = SevenWondersDuel()
        private set

    fun choose(wonder: Wonder) {
        game = game.choose(wonder)
    }

    fun build(building: Building) {
        game = game.build(building)
    }

    fun playRandomMoves(quantity: Int) {
        if (quantity < 1) return
        game = RandomBot.play(game)
        playRandomMoves(quantity - 1)
    }

    fun discard(building: Building) {
        game = game.discard(building)
    }
}