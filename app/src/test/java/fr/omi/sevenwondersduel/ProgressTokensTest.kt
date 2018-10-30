package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.Age.AGE_II
import fr.omi.sevenwondersduel.material.Age.AGE_III
import fr.omi.sevenwondersduel.material.Building.*
import fr.omi.sevenwondersduel.material.ProgressToken.*
import fr.omi.sevenwondersduel.material.TheColossus
import fr.omi.sevenwondersduel.material.ThePyramids
import fr.omi.sevenwondersduel.material.TheSphinx
import fr.omi.sevenwondersduel.material.TheTempleOfArtemis
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ProgressTokensTest {

    @Test
    fun agriculture() {
        var game = SevenWondersDuel(pendingActions = listOf(ProgressTokenToChoose(setOf(AGRICULTURE))),
                players = Pair(Player(coins = 0), Player()))
        game = game.choose(AGRICULTURE)
        assertThat(game.players.first.coins).isEqualTo(6)
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(6)
    }

    @Test
    fun architecture_gives_a_reduction_on_wonders_construction() {
        var game = SevenWondersDuel(structure = Structure(AGE_III, listOf(PALACE)), players = Pair(
                Player(coins = 1, progressTokens = setOf(ARCHITECTURE),
                        buildings = setOf(PRESS, STONE_RESERVE),
                        wonders = listOf(PlayerWonder(TheTempleOfArtemis))),
                Player(buildings = setOf(SAWMILL))))
        game = game.build(TheTempleOfArtemis, PALACE)
        assertThat(game.players.first.coins).isEqualTo(12)
    }

    @Test
    fun with_the_economy_I_gain_the_money_spent_by_my_opponent_on_resources() {
        val game = SevenWondersDuel(structure = Structure(AGE_III, listOf(TOWN_HALL, PRETORIUM)), players = Pair(
                Player(coins = 10, buildings = setOf(PRESS, STONE_RESERVE)),
                Player(coins = 0, progressTokens = setOf(ECONOMY))))
        assertThat(game.build(TOWN_HALL).players.second.coins).isEqualTo(7)
        assertThat(game.build(PRETORIUM).players.second.coins).isEqualTo(0)
    }

    @Test
    fun law_gives_a_scientific_symbol() {
        val game = SevenWondersDuel(players = Pair(Player(progressTokens = setOf(LAW)), Player()))
        assertThat(game.players.first.countDifferentScientificSymbols()).isEqualTo(1)
    }

    @Test
    fun masonry_gives_a_reduction_on_civilian_building_construction() {
        val game = SevenWondersDuel(structure = Structure(AGE_III, listOf(PALACE, ACADEMY)), players = Pair(
                Player(coins = 10, progressTokens = setOf(MASONRY),
                        buildings = setOf(CUSTOMS_HOUSE, CLAY_POOL)),
                Player(buildings = setOf(SAWMILL, GLASSBLOWER))))
        assertThat(game.build(PALACE).players.first.coins).isEqualTo(8)
        assertThat(game.build(ACADEMY).players.first.coins).isEqualTo(2)
    }

    @Test
    fun mathematics_give_victory_points_per_progress_token_I_have() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, progressTokens = setOf(PHILOSOPHY, MATHEMATICS)),
                Player(coins = 0, progressTokens = setOf(AGRICULTURE, STRATEGY))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(13)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(4)
    }

    @Test
    fun philosophy_give_7_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, progressTokens = setOf(PHILOSOPHY)),
                Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(7)
    }

    @Test
    fun strategy_give_extra_shield_for_military_buildings() {
        var game = SevenWondersDuel(structure = Structure(AGE_II, listOf(ARCHERY_RANGE)),
                conflictPawnPosition = 0, players = Pair(Player(progressTokens = setOf(STRATEGY)), Player()))
        game = game.build(ARCHERY_RANGE)
        assertThat(game.conflictPawnPosition).isEqualTo(3)
    }

    @Test
    fun strategy_does_not_give_extra_shield_for_wonders() {
        var game = SevenWondersDuel(structure = Structure(AGE_II, listOf(ARCHERY_RANGE)),
                conflictPawnPosition = 0, players = Pair(
                Player(coins = 42, progressTokens = setOf(STRATEGY), wonders = listOf(PlayerWonder(TheColossus))),
                Player()))
        game = game.build(TheColossus, ARCHERY_RANGE)
        assertThat(game.conflictPawnPosition).isEqualTo(2)
    }

    @Test
    fun theology_give_play_again_effect_to_all_my_wonders() {
        var game = SevenWondersDuel(structure = Structure(AGE_III, listOf(PALACE, OBSERVATORY)), players = Pair(
                Player(coins = 42, progressTokens = setOf(THEOLOGY),
                        wonders = listOf(PlayerWonder(ThePyramids))),
                Player(buildings = setOf(SAWMILL))))
        game = game.build(ThePyramids, PALACE)
        assertThat(game.currentPlayer).isEqualTo(1)
    }

    @Test
    fun a_wonder_cannot_have_play_again_effect_twice_with_theology() {
        var game = SevenWondersDuel(structure = Structure(AGE_III, listOf(PALACE, OBSERVATORY)), players = Pair(
                Player(coins = 42, progressTokens = setOf(THEOLOGY),
                        wonders = listOf(PlayerWonder(TheSphinx))),
                Player(buildings = setOf(SAWMILL))))
        game = game.build(TheSphinx, PALACE)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.pendingActions).isEmpty()
    }

    @Test
    fun urbanism_gives_4_coin_for_each_chained_building() {
        val game = SevenWondersDuel(structure = Structure(AGE_II, listOf(BARRACKS, TEMPLE, AQUEDUCT)),
                players = Pair(Player(coins = 0,
                        progressTokens = setOf(URBANISM),
                        buildings = setOf(BATHS, GARRISON, LOGGING_CAMP, PRESS)
                ), Player()))
        assertThat(game.build(BARRACKS).players.first.coins).isEqualTo(4)
        assertThat(game.build(TEMPLE).players.first.coins).isEqualTo(0)
        assertThat(game.build(AQUEDUCT).players.first.coins).isEqualTo(4)
    }
}