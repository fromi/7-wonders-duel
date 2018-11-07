package fr.omi.sevenwondersduel.ai

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder

sealed class SevenWondersDuelMove {
    abstract fun applyTo(game: SevenWondersDuel): SevenWondersDuel
}

data class TakeWonder(val wonder: Wonder) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.take(wonder)
}

data class ConstructBuilding(val building: Building) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.construct(building)
}

data class Discard(val building: Building) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.discard(building)
}

data class ConstructWonder(val wonder: Wonder, val buildingUsed: Building) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.construct(wonder, buildingUsed)
}

data class ChoosePlayerBeginningAge(val player: Int) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.choosePlayerBeginningNextAge(player)
}

data class ChooseProgressToken(val progressToken: ProgressToken) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.choose(progressToken)
}

data class DestroyBuilding(val building: Building) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.destroy(building)
}