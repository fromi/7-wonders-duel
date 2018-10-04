package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Age.AGE_III
import fr.omi.sevenwondersduel.Building.*
import fr.omi.sevenwondersduel.BuildingType.*
import fr.omi.sevenwondersduel.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CivilianVictoryTest {

    @Test
    fun game_end_by_civilian_victory() {
        var game = SevenWondersDuel(structure = createStructure(AGE_III, listOf(PORT)), players = Pair(
                Player(coins = 3, buildings = setOf(THEATER, ROSTRUM, AQUEDUCT, PALACE, OBELISK)),
                Player(coins = 0, buildings = emptySet())),
                currentPlayer = 2, currentAge = AGE_III)
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
                        BuildableWonder(THE_PYRAMIDS, OBELISK),
                        BuildableWonder(PIRAEUS, STABLE),
                        BuildableWonder(THE_COLOSSUS)
                        )), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(11)
        val totalWondersPoints = Wonder.values().flatMap { it.effects }.asSequence()
                .filterIsInstance<VictoryPoints>().sumBy { it.count(game, game.players.first) }
        assertThat(totalWondersPoints).isEqualTo(42)
    }
}