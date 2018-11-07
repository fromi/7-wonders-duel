package fr.omi.sevenwondersduel.ai

import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.effects.DiscardedBuildingToBuild
import fr.omi.sevenwondersduel.effects.OpponentBuildingToDestroy
import fr.omi.sevenwondersduel.effects.PlayerBeginningAgeToChoose
import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.Building

object RandomBot {
    fun play(game: SevenWondersDuel): SevenWondersDuel {
        return possibleMoves(game).shuffled().firstOrNull()?.applyTo(game) ?: game
    }

    fun playUntilMoveAvailable(predicate: (SevenWondersDuelMove) -> Boolean): SevenWondersDuel {
        var game = SevenWondersDuel()
        while (true) {
            val moves = possibleMoves(game)
            game = when {
                moves.any(predicate) -> return game
                moves.isEmpty() -> SevenWondersDuel()
                else -> moves.shuffled().first().applyTo(game)
            }
        }
    }

    private fun possibleMoves(game: SevenWondersDuel): List<SevenWondersDuelMove> {
        if (game.wondersAvailable.isNotEmpty()) {
            return game.wondersAvailable.map { TakeWonder(it) }
        }
        if (game.pendingActions.isNotEmpty()) {
            val firstPendingAction = game.pendingActions.first()
            when (firstPendingAction) {
                is ProgressTokenToChoose -> return firstPendingAction.tokens.map { ChooseProgressToken(it) }
                is DiscardedBuildingToBuild -> return game.discardedCards.map { ConstructBuilding(it) }
                is OpponentBuildingToDestroy -> return game.opponent.buildings.asSequence().filter { firstPendingAction.isEligible(it) }.map { DestroyBuilding(it) }.toList()
                is PlayerBeginningAgeToChoose -> return listOf(ChoosePlayerBeginningAge(1), ChoosePlayerBeginningAge(2))
            }
        }
        if (game.structure != null)
            return game.structure.accessibleBuildings().flatMap { building -> possibleMovesWith(game, building) }
        return emptyList()
    }

    private fun possibleMovesWith(game: SevenWondersDuel, building: Building): Iterable<SevenWondersDuelMove> {
        val moves: MutableList<SevenWondersDuelMove> = mutableListOf(Discard(building))
        game.currentPlayer.wonders.filter { !it.isConstructed() && game.coinsToPay(it.wonder) <= game.currentPlayer.coins }
                .forEach { moves.add(ConstructWonder(it.wonder, building)) }
        if (game.coinsToPay(building) <= game.currentPlayer.coins)
            moves.add(ConstructBuilding(building))
        return moves
    }
}
