package fr.omi.sevenwondersduel.app

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.ai.RandomBot
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.Wonder

class GameViewModel : ViewModel() {
    val game: MutableLiveData<SevenWondersDuel> by lazy {
        MutableLiveData<SevenWondersDuel>().apply {
            var game = SevenWondersDuel()
            repeat(0) {
                game = RandomBot.play(game)
            }
            postValue(game)
        }
    }

    private val gameNotNull: SevenWondersDuel get() = checkNotNull(game.value)

    fun choose(wonder: Wonder) {
        game.postValue(gameNotNull.choose(wonder))
    }

    fun build(building: Building) {
        game.postValue(gameNotNull.build(building))
    }

    fun discard(building: Building) {
        game.postValue(gameNotNull.discard(building))
    }
}