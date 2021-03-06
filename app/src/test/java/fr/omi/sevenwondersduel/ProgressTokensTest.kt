package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.OpponentBuildingToDestroy
import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.effects.ScientificSymbol
import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.ProgressToken.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ProgressTokensTest {

    @Test
    fun agriculture() {
        var game = SevenWondersDuel(structure = Structure(age = 2), pendingActions = listOf(ProgressTokenToChoose(setOf(AGRICULTURE))),
                players = Pair(Player(coins = 0), Player()))
        game = game.choose(AGRICULTURE)
        assertThat(game.players.first.coins).isEqualTo(6)
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(6)
    }

    @Test
    fun architecture_gives_a_reduction_on_wonders_construction() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Palace)), players = Pair(
                Player(coins = 1, progressTokens = setOf(ARCHITECTURE),
                        buildings = setOf(Press, StoneReserve),
                        wonders = listOf(PlayerWonder(TheTempleOfArtemis))),
                Player(buildings = setOf(Sawmill))))
        game = game.construct(TheTempleOfArtemis, Palace)
        assertThat(game.players.first.coins).isEqualTo(12)
    }

    @Test
    fun with_the_economy_I_gain_the_money_spent_by_my_opponent_on_resources() {
        val game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(TownHall, Pretorium)), players = Pair(
                Player(coins = 10, buildings = setOf(Press, StoneReserve)),
                Player(coins = 0, progressTokens = setOf(ECONOMY))))
        assertThat(game.construct(TownHall).players.second.coins).isEqualTo(7)
        assertThat(game.construct(Pretorium).players.second.coins).isEqualTo(0)
    }

    @Test
    fun law_gives_a_scientific_symbol() {
        val game = SevenWondersDuel(players = Pair(Player(progressTokens = setOf(LAW)), Player()))
        assertThat(game.players.first.effects.filterIsInstance<ScientificSymbol>().count()).isEqualTo(1)
    }

    @Test
    fun masonry_gives_a_reduction_on_civilian_building_construction() {
        val game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Palace, Academy)), players = Pair(
                Player(coins = 10, progressTokens = setOf(MASONRY),
                        buildings = setOf(CustomsHouse, ClayPool)),
                Player(buildings = setOf(Sawmill, Glassblower))))
        assertThat(game.construct(Palace).players.first.coins).isEqualTo(8)
        assertThat(game.construct(Academy).players.first.coins).isEqualTo(2)
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
        var game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(ArcheryRange)),
                conflictPawnPosition = 0, players = Pair(Player(progressTokens = setOf(STRATEGY)), Player()))
        game = game.construct(ArcheryRange)
        assertThat(game.conflictPawnPosition).isEqualTo(3)
    }

    @Test
    fun strategy_does_not_give_extra_shield_for_wonders() {
        var game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(ArcheryRange)),
                conflictPawnPosition = 0, players = Pair(
                Player(coins = 42, progressTokens = setOf(STRATEGY), wonders = listOf(PlayerWonder(TheColossus))),
                Player()))
        game = game.construct(TheColossus, ArcheryRange)
        assertThat(game.conflictPawnPosition).isEqualTo(2)
    }

    @Test
    fun theology_give_play_again_effect_to_all_my_wonders() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Palace, Observatory)), players = Pair(
                Player(coins = 42, progressTokens = setOf(THEOLOGY),
                        wonders = listOf(PlayerWonder(ThePyramids))),
                Player(buildings = setOf(Sawmill))))
        game = game.construct(ThePyramids, Palace)
        assertThat(game.currentPlayer).isEqualTo(game.players.first)
    }

    @Test
    fun play_again_with_theology_comes_after_the_great_library_progress_token_choice() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Palace, Observatory)), players = Pair(
                Player(coins = 42, progressTokens = setOf(THEOLOGY),
                        wonders = listOf(PlayerWonder(TheGreatLibrary))),
                Player(buildings = setOf(Sawmill))))
        game = game.construct(TheGreatLibrary, Palace)
        assertThat(game.currentPlayer).isEqualTo(game.players.first)
        assertThat(game.pendingActions.first()).isInstanceOf(ProgressTokenToChoose::class.java)
        game = game.choose((game.pendingActions.first() as ProgressTokenToChoose).tokens.first())
        assertThat(game.currentPlayer).isEqualTo(game.players.first)
    }

    @Test
    fun play_again_with_theology_comes_after_building_destruction() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Palace, Observatory)), players = Pair(
                Player(coins = 42, progressTokens = setOf(THEOLOGY),
                        wonders = listOf(PlayerWonder(TheStatueOfZeus))),
                Player(buildings = setOf(Sawmill))))
        game = game.construct(TheStatueOfZeus, Palace)
        assertThat(game.currentPlayer).isEqualTo(game.players.first)
        assertThat(game.pendingActions.first()).isInstanceOf(OpponentBuildingToDestroy::class.java)
        game = game.destroy(Sawmill)
        assertThat(game.currentPlayer).isEqualTo(game.players.first)
    }

    @Test
    fun a_wonder_cannot_have_play_again_effect_twice_with_theology() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Palace, Observatory)), players = Pair(
                Player(coins = 42, progressTokens = setOf(THEOLOGY),
                        wonders = listOf(PlayerWonder(TheSphinx))),
                Player(buildings = setOf(Sawmill))))
        game = game.construct(TheSphinx, Palace)
        assertThat(game.currentPlayer).isEqualTo(game.players.first)
        assertThat(game.pendingActions).isEmpty()
    }

    @Test
    fun urbanism_gives_4_coin_for_each_chained_building() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Barracks, Temple, Aqueduct)),
                players = Pair(Player(coins = 0,
                        progressTokens = setOf(URBANISM),
                        buildings = setOf(Baths, Garrison, LoggingCamp, Press)
                ), Player()))
        assertThat(game.construct(Barracks).players.first.coins).isEqualTo(4)
        assertThat(game.construct(Temple).players.first.coins).isEqualTo(0)
        assertThat(game.construct(Aqueduct).players.first.coins).isEqualTo(4)
    }
}