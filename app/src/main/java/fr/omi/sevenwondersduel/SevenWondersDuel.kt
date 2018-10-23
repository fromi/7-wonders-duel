package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.Action
import fr.omi.sevenwondersduel.material.Age
import fr.omi.sevenwondersduel.material.Building
import fr.omi.sevenwondersduel.material.ProgressToken
import fr.omi.sevenwondersduel.material.Wonder

interface SevenWondersDuel {
    val players: Pair<Player, Player>
    val conflictPawnPosition: Int
    val progressTokensAvailable: Set<ProgressToken>
    val currentPlayer: Int?
    val wondersAvailable: Set<Wonder>
    val structure: List<Map<Int, Building>>
    val discardedCards: List<Building>
    val pendingActions: List<Action>
    val currentAge: Age
    fun choose(wonder: Wonder): Game
    fun build(building: Building): Game
    fun discard(building: Building): Game
    fun build(wonder: Wonder, buildingUsed: Building): Game
    fun letOpponentBegin(): Game
    fun choose(progressToken: ProgressToken): Game
    fun destroy(building: Building): Game
    fun currentPlayer(): Player
    fun opponent(): Player
    fun isOver(): Boolean
    fun getWinner(): Player?
}