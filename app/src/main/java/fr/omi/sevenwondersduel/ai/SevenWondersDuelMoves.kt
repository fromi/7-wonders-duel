package fr.omi.sevenwondersduel.ai

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder

sealed class SevenWondersDuelMove {
    abstract fun applyTo(game: SevenWondersDuel): SevenWondersDuel
}

data class ChooseWonder(val wonder: Wonder) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.choose(wonder)
}

data class Build(val building: Building) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.build(building)
}

data class Discard(val building: Building) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.discard(building)
}

data class BuildWonder(val wonder: Wonder, val buildingUsed: Building) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.build(wonder, buildingUsed)
}

object LetOpponentBegin : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.letOpponentBegin()
}

data class ChooseProgressToken(val progressToken: ProgressToken) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.choose(progressToken)
}

data class DestroyBuilding(val building: Building) : SevenWondersDuelMove() {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel = game.destroy(building)
}