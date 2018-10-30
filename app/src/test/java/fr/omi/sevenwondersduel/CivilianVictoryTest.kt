package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPoints
import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Age.AGE_III
import fr.omi.sevenwondersduel.material.Building.*
import fr.omi.sevenwondersduel.material.Building.Type.*
import fr.omi.sevenwondersduel.material.ProgressToken.AGRICULTURE
import fr.omi.sevenwondersduel.material.ProgressToken.PHILOSOPHY
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CivilianVictoryTest {

    @Test
    fun game_end_by_civilian_victory() {
        var game = SevenWondersDuel(structure = Structure(AGE_III, listOf(PORT)), players = Pair(
                Player(coins = 3, buildings = setOf(THEATER, ROSTRUM, AQUEDUCT, PALACE, OBELISK)),
                Player(coins = 0, buildings = emptySet())),
                currentPlayer = 2)
        game = game.discard(PORT)
        assertThat(game.isOver()).isTrue()
        assertThat(game.getWinner()).isEqualTo(game.players.first)
    }

    @Test
    fun count_military_points() {
        var game = SevenWondersDuel(conflictPawnPosition = -8, players = Pair(first = Player(coins = 0), second = Player(coins = 1)))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(0)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(10)
        game = game.copy(conflictPawnPosition = 2)
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(2)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(0)
        game = game.copy(conflictPawnPosition = 4)
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(5)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(0)
    }

    @Test
    fun count_treasure() {
        var game = SevenWondersDuel(players = Pair(Player(coins = 17), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(5)
        game = SevenWondersDuel(players = Pair(Player(coins = 9), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
        game = SevenWondersDuel(players = Pair(Player(coins = 4), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(1)
    }

    @Test
    fun count_civilian_buildings() {
        val game = SevenWondersDuel(players = Pair(Player(coins = 0, buildings = setOf(THEATER, PALACE)), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(10)
        val totalCivilianBuildingsPoints = Building.values().filter { it.type == CIVILIAN }.flatMap { it.effects }.asSequence()
                .filterIsInstance<VictoryPoints>().sumBy { it.count(game, game.players.first) }
        assertThat(totalCivilianBuildingsPoints).isEqualTo(67)
    }

    @Test
    fun count_scientific_buildings() {
        val game = SevenWondersDuel(players = Pair(Player(coins = 0, buildings = setOf(SCHOOL, ACADEMY)), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(4)
        val totalScientificBuildingsPoints = Building.values().filter { it.type == SCIENTIFIC }.flatMap { it.effects }.asSequence()
                .filterIsInstance<VictoryPoints>().sumBy { it.count(game, game.players.first) }
        assertThat(totalScientificBuildingsPoints).isEqualTo(18)
    }

    @Test
    fun count_commercial_buildings() {
        val game = SevenWondersDuel(players = Pair(Player(coins = 0, buildings = setOf(LIGHTHOUSE, PORT)), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(6)
        val totalCommercialBuildingsPoints = Building.values().filter { it.type == COMMERCIAL }.flatMap { it.effects }.asSequence()
                .filterIsInstance<VictoryPoints>().sumBy { it.count(game, game.players.first) }
        assertThat(totalCommercialBuildingsPoints).isEqualTo(15)
    }

    @Test
    fun count_wonders() {
        val game = SevenWondersDuel(players = Pair(Player(coins = 0,
                wonders = listOf(
                        PlayerWonder(ThePyramids, OBELISK),
                        PlayerWonder(Piraeus, STABLE),
                        PlayerWonder(TheColossus)
                        )), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(11)
        val totalWondersPoints = wonders.flatMap { it.effects }.asSequence()
                .filterIsInstance<VictoryPoints>().sumBy { it.count(game, game.players.first) }
        assertThat(totalWondersPoints).isEqualTo(42)
    }

    @Test
    fun in_case_of_tie_most_points_from_civilian_building_wins() {
        val game = SevenWondersDuel(conflictPawnPosition = 1, structure = Structure(AGE_III, emptyList()), players = Pair(
                Player(coins = 0, buildings = setOf(PORT), progressTokens = setOf(PHILOSOPHY), wonders = listOf(PlayerWonder(Piraeus, PALACE))),
                Player(coins = 0, buildings = setOf(THEATER, SENATE, ARCHERY_RANGE, UNIVERSITY), progressTokens = setOf(AGRICULTURE))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(14)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(14)
        assertThat(game.getWinner()).isEqualTo(game.players.second)
    }

    @Test
    fun in_case_of_tie_including_civilian_victory_is_shared() {
        val game = SevenWondersDuel(conflictPawnPosition = 1, structure = Structure(AGE_III, emptyList()), players = Pair(
                Player(coins = 0, buildings = setOf(TEMPLE, OBELISK), wonders = listOf(PlayerWonder(TheGreatLibrary, PALACE))),
                Player(coins = 0, buildings = setOf(THEATER, GARDENS, ARCHERY_RANGE, DISPENSARY), progressTokens = setOf(AGRICULTURE))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(15)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(15)
        assertThat(game.getWinner()).isNull()
    }
}