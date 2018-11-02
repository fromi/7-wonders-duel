package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.PlayAgain
import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.ProgressToken.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WondersTest {

    private val sampleAge2Structure = Structure(age = 2, buildings = listOf(
            Brickyard, ShelfQuarry, Forum, Laboratory, Barracks, Library,
            Aqueduct, Rostrum, School, DryingRoom, HorseBreeders,
            Walls, ParadeGround, Statue, Courthouse,
            CustomsHouse, Sawmill, Glassblower,
            Brewery, Dispensary))

    @Test
    fun there_are_12_wonders() {
        assertThat(wonders.size).isEqualTo(12)
    }

    @Test
    fun play_a_second_turn() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 4, buildings = setOf(Press, Glassworks, Brickyard), wonders = listOf(
                        PlayerWonder(TheTempleOfArtemis),
                        PlayerWonder(TheColossus, buildingUnder = Garrison),
                        PlayerWonder(ThePyramids, buildingUnder = Stable),
                        PlayerWonder(TheGreatLibrary))),
                Player()))
        game = game.build(TheTempleOfArtemis, Dispensary)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.build(Glassblower)
        assertThat(game.players.first.buildings).contains(Glassblower)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test
    fun play_again_effect_is_lost_at_the_end_of_an_age() {
        var game = SevenWondersDuel(conflictPawnPosition = 1, structure = Structure(age = 2, buildings = listOf(Forum)), players = Pair(
                Player(coins = 42, buildings = setOf(Press, Glassworks, Brickyard), wonders = listOf(
                        PlayerWonder(Piraeus),
                        PlayerWonder(TheColossus, buildingUnder = Garrison),
                        PlayerWonder(ThePyramids, buildingUnder = Stable),
                        PlayerWonder(TheGreatLibrary))),
                Player()))
        game = game.build(Piraeus, Forum)
        assertThat(game.structure?.age).isEqualTo(3)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.pendingActions).doesNotContain(PlayAgain)
    }

    @Test
    fun the_appian_way_makes_opponent_lose_3_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(buildings = setOf(Press, Brickyard, ShelfQuarry), wonders = listOf(
                        PlayerWonder(TheAppianWay),
                        PlayerWonder(TheColossus, buildingUnder = Garrison),
                        PlayerWonder(ThePyramids, buildingUnder = Stable),
                        PlayerWonder(TheGreatLibrary))),
                Player(coins = 2)))
        game = game.build(TheAppianWay, Dispensary)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun circus_maximus_destroys_an_opponent_manufacture() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(CircusMaximus))),
                Player(buildings = setOf(Glassworks, Press))))
        game = game.build(CircusMaximus, Dispensary)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.destroy(Glassworks)
        assertThat(game.players.second.buildings).doesNotContain(Glassworks)
        assertThat(game.discardedCards).contains(Glassworks)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_destroy_an_opponent_building_at_any_time() {
        val game = SevenWondersDuel(players = Pair(Player(), Player(buildings = setOf(Glassworks))))
        game.destroy(Glassworks)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_destroy_any_opponent_building() {
        val game = SevenWondersDuel(players = Pair(Player(), Player(buildings = setOf(Glassworks, Sawmill))),
                pendingActions = listOf(CircusMaximus))
        game.destroy(Sawmill)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_destroy_a_building_opponent_does_not_have() {
        val game = SevenWondersDuel(players = Pair(Player(), Player(buildings = setOf(Glassworks, Sawmill))),
                pendingActions = listOf(CircusMaximus))
        game.destroy(Press)
    }

    @Test
    fun destroy_building_effect_is_ignored_if_there_is_no_valid_target() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(CircusMaximus))),
                Player(buildings = setOf(Temple, Stable, ClayPit))))
        game = game.build(CircusMaximus, Dispensary)
        assertThat(game.pendingActions).isEmpty()
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test
    fun the_statue_of_zeus_destroys_an_opponent_raw_material_building() {
        var game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary)), players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus))),
                Player(buildings = setOf(Sawmill))))
        game = game.build(TheStatueOfZeus, Dispensary)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.destroy(Sawmill)
        assertThat(game.players.second.buildings).doesNotContain(Sawmill)
        assertThat(game.discardedCards).contains(Sawmill)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalStateException::class)
    fun the_statue_of_zeus_does_not_allow_me_to_build_another_building() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, Brewery)), players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus))),
                Player(buildings = setOf(Sawmill))))
        game.build(TheStatueOfZeus, Dispensary).build(Brewery)
    }

    @Test(expected = IllegalStateException::class)
    fun the_statue_of_zeus_does_not_allow_me_to_discard_another_from_the_structure() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, Brewery)), players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus), PlayerWonder(Piraeus))),
                Player(buildings = setOf(Sawmill))))
        game.build(TheStatueOfZeus, Dispensary).discard(Brewery)
    }

    @Test(expected = IllegalStateException::class)
    fun the_statue_of_zeus_does_not_allow_me_to_build_another_wonder() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, Brewery)), players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus), PlayerWonder(Piraeus))),
                Player(buildings = setOf(Sawmill))))
        game.build(TheStatueOfZeus, Dispensary).build(Piraeus, Brewery)
    }

    @Test
    fun the_great_library_allows_to_choose_a_random_progress_token_between_3_remaining() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheGreatLibrary))),
                Player(progressTokens = setOf(AGRICULTURE, URBANISM))),
                progressTokensAvailable = setOf(STRATEGY, LAW, THEOLOGY))
        game = game.build(TheGreatLibrary, Dispensary)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.pendingActions[0]).isInstanceOfSatisfying(ProgressTokenToChoose::class.java) {
            assertThat(it.tokens.size).isEqualTo(3)
            assertThat(it.tokens).doesNotContain(AGRICULTURE, URBANISM, STRATEGY, LAW, THEOLOGY)
        }
        game = SevenWondersDuel(structure = Structure(age = 1, buildings = listOf(ClayPool)),
                pendingActions = listOf(ProgressTokenToChoose(setOf(MASONRY, ECONOMY, ARCHITECTURE))),
                progressTokensAvailable = setOf(STRATEGY, LAW, THEOLOGY))
        game = game.choose(ECONOMY)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.pendingActions).isEmpty()
        assertThat(game.players.first.progressTokens).contains(ECONOMY)
        assertThat(game.progressTokensAvailable).containsExactly(STRATEGY, LAW, THEOLOGY)
    }

    @Test
    fun the_great_lighthouse_produces_any_raw_good_once_built() {
        var game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Forum)), players = Pair(
                Player(coins = 5, wonders = listOf(PlayerWonder(TheGreatLighthouse))),
                Player()
        ))
        game = game.build(Forum)
        assertThat(game.players.first.coins).isEqualTo(0)
        game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Forum)), players = Pair(
                Player(coins = 5, wonders = listOf(PlayerWonder(TheGreatLighthouse, buildingUnder = Stable))),
                Player()
        ))
        game = game.build(Forum)
        assertThat(game.players.first.coins).isEqualTo(2)
        game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Forum)), players = Pair(
                Player(wonders = listOf(PlayerWonder(TheGreatLighthouse, buildingUnder = Stable))),
                Player(coins = 5)
        ), currentPlayer = 2)
        game = game.build(Forum)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun piraeus_produces_any_manufactured_good_once_built() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(ChamberOfCommerce)), players = Pair(
                Player(coins = 4, wonders = listOf(PlayerWonder(Piraeus))),
                Player()
        ))
        game = game.build(ChamberOfCommerce)
        assertThat(game.players.first.coins).isEqualTo(0)
        game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(ChamberOfCommerce)), players = Pair(
                Player(coins = 4, wonders = listOf(PlayerWonder(Piraeus, buildingUnder = Stable))),
                Player()
        ))
        game = game.build(ChamberOfCommerce)
        assertThat(game.players.first.coins).isEqualTo(2)
        game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(ChamberOfCommerce)), players = Pair(
                Player(wonders = listOf(PlayerWonder(Piraeus, buildingUnder = Stable))),
                Player(coins = 4)
        ), currentPlayer = 2)
        game = game.build(ChamberOfCommerce)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun the_mausoleum_let_me_build_any_discarded_card_for_free() {
        var game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, DryingRoom)), players = Pair(
                Player(coins = 10, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = listOf(Walls, Courthouse))
        game = game.build(TheMausoleum, Dispensary)
        assertThat(game.players.first.coins).isEqualTo(0)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.build(Courthouse)
        assertThat(game.players.first.buildings).contains(Courthouse)
        assertThat(game.discardedCards).doesNotContain(Courthouse)
    }

    @Test
    fun if_discard_empty_mausoleum_does_nothing() {
        var game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, DryingRoom)), players = Pair(
                Player(coins = 10, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = emptyList())
        game = game.build(TheMausoleum, Dispensary)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun the_mausoleum_does_not_let_me_build_anything() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, DryingRoom)), players = Pair(
                Player(coins = 10, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = listOf(Walls, Courthouse))
        game.build(TheMausoleum, Dispensary).build(Palace)
    }

    @Test(expected = IllegalArgumentException::class)
    fun the_mausoleum_does_not_let_me_build_from_the_structure() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, DryingRoom)), players = Pair(
                Player(coins = 50, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = listOf(Walls, Courthouse))
        game.build(TheMausoleum, Dispensary).build(DryingRoom)
    }

    @Test(expected = IllegalStateException::class)
    fun the_mausoleum_does_not_let_me_build_a_wonder() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, DryingRoom)), players = Pair(
                Player(coins = 50, wonders = listOf(PlayerWonder(TheMausoleum), PlayerWonder(Piraeus))),
                Player()), discardedCards = listOf(Walls, Courthouse))
        game.build(TheMausoleum, Dispensary).build(Piraeus, DryingRoom)
    }

    @Test(expected = IllegalStateException::class)
    fun the_mausoleum_does_not_let_me_discard_a_building() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Dispensary, DryingRoom)), players = Pair(
                Player(coins = 50, wonders = listOf(PlayerWonder(TheMausoleum), PlayerWonder(Piraeus))),
                Player()), discardedCards = listOf(Walls, Courthouse))
        game.build(TheMausoleum, Dispensary).discard(DryingRoom)
    }
}