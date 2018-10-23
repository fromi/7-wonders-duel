package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.ViewModel
import fr.omi.sevenwondersduel.Game
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

object GameViewModel : ViewModel() {
    var game = Game()
        private set

    fun choose(wonder: Wonder) {
        game = game.choose(wonder)
    }

    fun build(building: Building) {
        game = game.build(building)
    }
}