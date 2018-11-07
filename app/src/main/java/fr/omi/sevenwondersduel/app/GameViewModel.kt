package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.ai.RandomBot
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

class GameViewModel : ViewModel() {
    val game = MutableLiveData<SevenWondersDuel>()

    init {
        game.value = SevenWondersDuel()
        repeat(0) { game.value = RandomBot.play(game.value!!) }
    }

    fun choose(wonder: Wonder) {
        game.postValue(checkNotNull(game.value).choose(wonder))
    }

    fun build(building: Building) {
        game.postValue(checkNotNull(game.value).build(building))
    }

    fun discard(building: Building) {
        game.postValue(checkNotNull(game.value).discard(building))
    }
}