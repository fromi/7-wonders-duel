package fr.omi.sevenwondersduel.ai

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.effects.DiscardedBuildingToBuild
import fr.omi.sevenwondersduel.effects.OpponentBuildingToDestroy
import fr.omi.sevenwondersduel.effects.PlayerBeginningAgeToChoose
import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.Building

object RandomBot {
    fun play(game: SevenWondersDuel): SevenWondersDuel {
        return possibleMoves(game).toMutableList().shuffled().first().applyTo(game)
    }

    private fun possibleMoves(game: SevenWondersDuel): List<SevenWondersDuelMove> {
        if (game.wondersAvailable.isNotEmpty()) {
            return game.wondersAvailable.map { ChooseWonder(it) }
        }
        if (game.pendingActions.isNotEmpty()) {
            val firstPendingAction = game.pendingActions.first()
            when (firstPendingAction) {
                is ProgressTokenToChoose -> return firstPendingAction.tokens.map { ChooseProgressToken(it) }
                is DiscardedBuildingToBuild -> return game.discardedCards.map { Build(it) }
                is OpponentBuildingToDestroy -> return game.opponent().buildings.asSequence().filter { it.type == firstPendingAction.type }.map { DestroyBuilding(it) }.toList()
                is PlayerBeginningAgeToChoose -> return listOf(ChoosePlayerBeginningAge(1), ChoosePlayerBeginningAge(2))
            }
        }
        if (game.isOver()) return emptyList()
        return game.accessibleBuildings().flatMap { building -> possibleMovesWith(game, building) }
    }

    private fun possibleMovesWith(game: SevenWondersDuel, building: Building): Iterable<SevenWondersDuelMove> {
        val moves: MutableList<SevenWondersDuelMove> = mutableListOf(Discard(building))
        game.currentPlayer().wonders.filter { !it.isBuild() && game.coinsToPay(it.wonder) <= game.currentPlayer().coins }
                .forEach { moves.add(BuildWonder(it.wonder, building)) }
        if (game.coinsToPay(building) <= game.currentPlayer().coins)
            moves.add(Build(building))
        return moves
    }
}
