package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.OpponentBuildingToDestroy
import fr.omi.sevenwondersduel.effects.PlayAgain
import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building.*
import fr.omi.sevenwondersduel.material.Building.Type.MANUFACTURE
import fr.omi.sevenwondersduel.material.ProgressToken.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WondersTest {

    private val sampleAge2Structure = Structure(AGE_II, listOf(
            BRICKYARD, SHELF_QUARRY, FORUM, LABORATORY, BARRACKS, LIBRARY,
            AQUEDUCT, ROSTRUM, SCHOOL, DRYING_ROOM, HORSE_BREEDERS,
            WALLS, PARADE_GROUND, STATUE, COURTHOUSE,
            CUSTOMS_HOUSE, SAWMILL, GLASSBLOWER,
            BREWERY, DISPENSARY))

    @Test
    fun there_are_12_wonders() {
        assertThat(wonders.size).isEqualTo(12)
    }

    @Test
    fun play_a_second_turn() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 4, buildings = setOf(PRESS, GLASSWORKS, BRICKYARD), wonders = listOf(
                        PlayerWonder(TheTempleOfArtemis),
                        PlayerWonder(TheColossus, buildingUnder = GARRISON),
                        PlayerWonder(ThePyramids, buildingUnder = STABLE),
                        PlayerWonder(TheGreatLibrary))),
                Player()))
        game = game.build(TheTempleOfArtemis, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.build(GLASSBLOWER)
        assertThat(game.players.first.buildings).contains(GLASSBLOWER)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test
    fun play_again_effect_is_lost_at_the_end_of_an_age() {
        var game = SevenWondersDuel(conflictPawnPosition = 1, structure = Structure(AGE_II, listOf(FORUM)), players = Pair(
                Player(coins = 42, buildings = setOf(PRESS, GLASSWORKS, BRICKYARD), wonders = listOf(
                        PlayerWonder(Piraeus),
                        PlayerWonder(TheColossus, buildingUnder = GARRISON),
                        PlayerWonder(ThePyramids, buildingUnder = STABLE),
                        PlayerWonder(TheGreatLibrary))),
                Player()))
        game = game.build(Piraeus, FORUM)
        assertThat(game.structure?.age).isEqualTo(AGE_III)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.pendingActions).doesNotContain(PlayAgain)
    }

    @Test
    fun the_appian_way_makes_opponent_lose_3_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(buildings = setOf(PRESS, BRICKYARD, SHELF_QUARRY), wonders = listOf(
                        PlayerWonder(TheAppianWay),
                        PlayerWonder(TheColossus, buildingUnder = GARRISON),
                        PlayerWonder(ThePyramids, buildingUnder = STABLE),
                        PlayerWonder(TheGreatLibrary))),
                Player(coins = 2)))
        game = game.build(TheAppianWay, DISPENSARY)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun circus_maximus_destroys_an_opponent_manufacture() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(CircusMaximus))),
                Player(buildings = setOf(GLASSWORKS, PRESS))))
        game = game.build(CircusMaximus, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.destroy(GLASSWORKS)
        assertThat(game.players.second.buildings).doesNotContain(GLASSWORKS)
        assertThat(game.discardedCards).contains(GLASSWORKS)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_destroy_an_opponent_building_at_any_time() {
        val game = SevenWondersDuel(players = Pair(Player(), Player(buildings = setOf(GLASSWORKS))))
        game.destroy(GLASSWORKS)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_destroy_any_opponent_building() {
        val game = SevenWondersDuel(players = Pair(Player(), Player(buildings = setOf(GLASSWORKS, SAWMILL))),
                pendingActions = listOf(OpponentBuildingToDestroy(MANUFACTURE)))
        game.destroy(SAWMILL)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_destroy_a_building_opponent_does_not_have() {
        val game = SevenWondersDuel(players = Pair(Player(), Player(buildings = setOf(GLASSWORKS, SAWMILL))),
                pendingActions = listOf(OpponentBuildingToDestroy(MANUFACTURE)))
        game.destroy(PRESS)
    }

    @Test
    fun destroy_building_effect_is_ignored_if_there_is_no_valid_target() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(CircusMaximus))),
                Player(buildings = setOf(TEMPLE, STABLE, CLAY_PIT))))
        game = game.build(CircusMaximus, DISPENSARY)
        assertThat(game.pendingActions).isEmpty()
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test
    fun the_statue_of_zeus_destroys_an_opponent_raw_material_building() {
        var game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY)), players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus))),
                Player(buildings = setOf(SAWMILL))))
        game = game.build(TheStatueOfZeus, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.destroy(SAWMILL)
        assertThat(game.players.second.buildings).doesNotContain(SAWMILL)
        assertThat(game.discardedCards).contains(SAWMILL)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalStateException::class)
    fun the_statue_of_zeus_does_not_allow_me_to_build_another_building() {
        val game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, BREWERY)), players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus))),
                Player(buildings = setOf(SAWMILL))))
        game.build(TheStatueOfZeus, DISPENSARY).build(BREWERY)
    }

    @Test(expected = IllegalStateException::class)
    fun the_statue_of_zeus_does_not_allow_me_to_discard_another_from_the_structure() {
        val game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, BREWERY)), players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus), PlayerWonder(Piraeus))),
                Player(buildings = setOf(SAWMILL))))
        game.build(TheStatueOfZeus, DISPENSARY).discard(BREWERY)
    }

    @Test(expected = IllegalStateException::class)
    fun the_statue_of_zeus_does_not_allow_me_to_build_another_wonder() {
        val game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, BREWERY)), players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus), PlayerWonder(Piraeus))),
                Player(buildings = setOf(SAWMILL))))
        game.build(TheStatueOfZeus, DISPENSARY).build(Piraeus, BREWERY)
    }

    @Test
    fun the_great_library_allows_to_choose_a_random_progress_token_between_3_remaining() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheGreatLibrary))),
                Player(progressTokens = setOf(AGRICULTURE, URBANISM))),
                progressTokensAvailable = setOf(STRATEGY, LAW, THEOLOGY))
        game = game.build(TheGreatLibrary, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.pendingActions[0]).isInstanceOfSatisfying(ProgressTokenToChoose::class.java) {
            assertThat(it.tokens.size).isEqualTo(3)
            assertThat(it.tokens).doesNotContain(AGRICULTURE, URBANISM, STRATEGY, LAW, THEOLOGY)
        }
        game = SevenWondersDuel(structure = Structure(AGE_I, listOf(CLAY_POOL)),
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
        var game = SevenWondersDuel(structure = Structure(AGE_II, listOf(FORUM)), players = Pair(
                Player(coins = 5, wonders = listOf(PlayerWonder(TheGreatLighthouse))),
                Player()
        ))
        game = game.build(FORUM)
        assertThat(game.players.first.coins).isEqualTo(0)
        game = SevenWondersDuel(structure = Structure(AGE_II, listOf(FORUM)), players = Pair(
                Player(coins = 5, wonders = listOf(PlayerWonder(TheGreatLighthouse, buildingUnder = STABLE))),
                Player()
        ))
        game = game.build(FORUM)
        assertThat(game.players.first.coins).isEqualTo(2)
        game = SevenWondersDuel(structure = Structure(AGE_II, listOf(FORUM)), players = Pair(
                Player(wonders = listOf(PlayerWonder(TheGreatLighthouse, buildingUnder = STABLE))),
                Player(coins = 5)
        ), currentPlayer = 2)
        game = game.build(FORUM)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun piraeus_produces_any_manufactured_good_once_built() {
        var game = SevenWondersDuel(structure = Structure(AGE_III, listOf(CHAMBER_OF_COMMERCE)), players = Pair(
                Player(coins = 4, wonders = listOf(PlayerWonder(Piraeus))),
                Player()
        ))
        game = game.build(CHAMBER_OF_COMMERCE)
        assertThat(game.players.first.coins).isEqualTo(0)
        game = SevenWondersDuel(structure = Structure(AGE_III, listOf(CHAMBER_OF_COMMERCE)), players = Pair(
                Player(coins = 4, wonders = listOf(PlayerWonder(Piraeus, buildingUnder = STABLE))),
                Player()
        ))
        game = game.build(CHAMBER_OF_COMMERCE)
        assertThat(game.players.first.coins).isEqualTo(2)
        game = SevenWondersDuel(structure = Structure(AGE_III, listOf(CHAMBER_OF_COMMERCE)), players = Pair(
                Player(wonders = listOf(PlayerWonder(Piraeus, buildingUnder = STABLE))),
                Player(coins = 4)
        ), currentPlayer = 2)
        game = game.build(CHAMBER_OF_COMMERCE)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun the_mausoleum_let_me_build_any_discarded_card_for_free() {
        var game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, DRYING_ROOM)), players = Pair(
                Player(coins = 10, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = listOf(WALLS, COURTHOUSE))
        game = game.build(TheMausoleum, DISPENSARY)
        assertThat(game.players.first.coins).isEqualTo(0)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.build(COURTHOUSE)
        assertThat(game.players.first.buildings).contains(COURTHOUSE)
        assertThat(game.discardedCards).doesNotContain(COURTHOUSE)
    }

    @Test
    fun if_discard_empty_mausoleum_does_nothing() {
        var game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, DRYING_ROOM)), players = Pair(
                Player(coins = 10, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = emptyList())
        game = game.build(TheMausoleum, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun the_mausoleum_does_not_let_me_build_anything() {
        val game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, DRYING_ROOM)), players = Pair(
                Player(coins = 10, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = listOf(WALLS, COURTHOUSE))
        game.build(TheMausoleum, DISPENSARY).build(PALACE)
    }

    @Test(expected = IllegalArgumentException::class)
    fun the_mausoleum_does_not_let_me_build_from_the_structure() {
        val game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, DRYING_ROOM)), players = Pair(
                Player(coins = 50, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = listOf(WALLS, COURTHOUSE))
        game.build(TheMausoleum, DISPENSARY).build(DRYING_ROOM)
    }

    @Test(expected = IllegalStateException::class)
    fun the_mausoleum_does_not_let_me_build_a_wonder() {
        val game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, DRYING_ROOM)), players = Pair(
                Player(coins = 50, wonders = listOf(PlayerWonder(TheMausoleum), PlayerWonder(Piraeus))),
                Player()), discardedCards = listOf(WALLS, COURTHOUSE))
        game.build(TheMausoleum, DISPENSARY).build(Piraeus, DRYING_ROOM)
    }

    @Test(expected = IllegalStateException::class)
    fun the_mausoleum_does_not_let_me_discard_a_building() {
        val game = SevenWondersDuel(structure = Structure(AGE_II, listOf(DISPENSARY, DRYING_ROOM)), players = Pair(
                Player(coins = 50, wonders = listOf(PlayerWonder(TheMausoleum), PlayerWonder(Piraeus))),
                Player()), discardedCards = listOf(WALLS, COURTHOUSE))
        game.build(TheMausoleum, DISPENSARY).discard(DRYING_ROOM)
    }
}