package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.ChooseProgressToken
import fr.omi.sevenwondersduel.effects.DestroyOpponentBuilding
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building.*
import fr.omi.sevenwondersduel.material.Building.Type.MANUFACTURE
import fr.omi.sevenwondersduel.material.ProgressToken.*
import fr.omi.sevenwondersduel.material.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WondersTest {

    private val sampleAge2Structure = Game.createStructure(AGE_II, listOf(
            BRICKYARD, SHELF_QUARRY, FORUM, LABORATORY, BARRACKS, LIBRARY,
            AQUEDUCT, ROSTRUM, SCHOOL, DRYING_ROOM, HORSE_BREEDERS,
            WALLS, PARADE_GROUND, STATUE, TRIBUNAL,
            CUSTOMS_HOUSE, SAWMILL, GLASSBLOWER,
            BREWERY, DISPENSARY))

    @Test
    fun play_a_second_turn() {
        var game = Game(structure = sampleAge2Structure, players = Pair(
                Player(coins = 4, buildings = setOf(PRESS, GLASSWORKS, BRICKYARD), wonders = listOf(
                        BuildableWonder(THE_TEMPLE_OF_ARTEMIS),
                        BuildableWonder(THE_COLOSSUS, builtWith = GARRISON),
                        BuildableWonder(THE_PYRAMIDS, builtWith = STABLE),
                        BuildableWonder(THE_GREAT_LIBRARY))),
                Player()))
        game = game.build(THE_TEMPLE_OF_ARTEMIS, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.build(GLASSBLOWER)
        assertThat(game.players.first.buildings).contains(GLASSBLOWER)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test
    fun play_again_effect_is_lost_at_the_end_of_an_age() {
        var game = Game(currentAge = AGE_II, conflictPawnPosition = 1, structure = Game.createStructure(AGE_II, listOf(FORUM)), players = Pair(
                Player(coins = 42, buildings = setOf(PRESS, GLASSWORKS, BRICKYARD), wonders = listOf(
                        BuildableWonder(PIRAEUS),
                        BuildableWonder(THE_COLOSSUS, builtWith = GARRISON),
                        BuildableWonder(THE_PYRAMIDS, builtWith = STABLE),
                        BuildableWonder(THE_GREAT_LIBRARY))),
                Player()))
        game = game.build(PIRAEUS, FORUM)
        assertThat(game.currentAge).isEqualTo(AGE_III)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.pendingActions).isEmpty()
    }

    @Test
    fun the_appian_way_makes_opponent_lose_3_coins() {
        var game = Game(structure = sampleAge2Structure, players = Pair(
                Player(buildings = setOf(PRESS, BRICKYARD, SHELF_QUARRY), wonders = listOf(
                        BuildableWonder(THE_APPIAN_WAY),
                        BuildableWonder(THE_COLOSSUS, builtWith = GARRISON),
                        BuildableWonder(THE_PYRAMIDS, builtWith = STABLE),
                        BuildableWonder(THE_GREAT_LIBRARY))),
                Player(coins = 2)))
        game = game.build(THE_APPIAN_WAY, DISPENSARY)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun circus_maximus_destroys_an_opponent_manufacture() {
        var game = Game(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(BuildableWonder(CIRCUS_MAXIMUS))),
                Player(buildings = setOf(GLASSWORKS, PRESS))))
        game = game.build(CIRCUS_MAXIMUS, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.destroy(GLASSWORKS)
        assertThat(game.players.second.buildings).doesNotContain(GLASSWORKS)
        assertThat(game.discardedCards).contains(GLASSWORKS)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_destroy_an_opponent_building_at_any_time() {
        val game = Game(players = Pair(Player(), Player(buildings = setOf(GLASSWORKS))))
        game.destroy(GLASSWORKS)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_destroy_any_opponent_building() {
        val game = Game(players = Pair(Player(), Player(buildings = setOf(GLASSWORKS, SAWMILL))),
                pendingActions = listOf(DestroyOpponentBuilding(MANUFACTURE)))
        game.destroy(SAWMILL)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_destroy_a_building_opponent_does_not_have() {
        val game = Game(players = Pair(Player(), Player(buildings = setOf(GLASSWORKS, SAWMILL))),
                pendingActions = listOf(DestroyOpponentBuilding(MANUFACTURE)))
        game.destroy(PRESS)
    }

    @Test
    fun destroy_building_effect_is_ignored_if_there_is_no_valid_target() {
        var game = Game(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(BuildableWonder(CIRCUS_MAXIMUS))),
                Player(buildings = setOf(TEMPLE, STABLE, CLAY_PIT))))
        game = game.build(CIRCUS_MAXIMUS, DISPENSARY)
        assertThat(game.pendingActions).isEmpty()
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test
    fun the_statue_of_zeus_destroys_an_opponent_raw_material_building() {
        var game = Game(structure = Game.createStructure(AGE_II, listOf(DISPENSARY)), players = Pair(
                Player(coins = 42, wonders = listOf(BuildableWonder(THE_STATUE_OF_ZEUS))),
                Player(buildings = setOf(SAWMILL))))
        game = game.build(THE_STATUE_OF_ZEUS, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.destroy(SAWMILL)
        assertThat(game.players.second.buildings).doesNotContain(SAWMILL)
        assertThat(game.discardedCards).contains(SAWMILL)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test
    fun the_great_library_allows_to_choose_a_random_progress_token_between_3_remaining() {
        var game = Game(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(BuildableWonder(THE_GREAT_LIBRARY))),
                Player(progressTokens = setOf(AGRICULTURE, URBANISM))),
                progressTokensAvailable = setOf(STRATEGY, LAW, THEOLOGY))
        game = game.build(THE_GREAT_LIBRARY, DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.pendingActions[0]).isInstanceOfSatisfying(ChooseProgressToken::class.java) {
            assertThat(it.tokens.size).isEqualTo(3)
            assertThat(it.tokens).doesNotContain(AGRICULTURE, URBANISM, STRATEGY, LAW, THEOLOGY)
        }
        game = Game(structure = Game.createStructure(AGE_I, listOf(CLAY_POOL)),
                pendingActions = listOf(ChooseProgressToken(setOf(MASONRY, ECONOMY, ARCHITECTURE))),
                progressTokensAvailable = setOf(STRATEGY, LAW, THEOLOGY))
        game = game.choose(ECONOMY)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.pendingActions).isEmpty()
        assertThat(game.players.first.progressTokens).contains(ECONOMY)
        assertThat(game.progressTokensAvailable).containsExactly(STRATEGY, LAW, THEOLOGY)
    }

    @Test
    fun the_great_lighthouse_produces_any_raw_good_once_built() {
        var game = Game(structure = Game.createStructure(AGE_II, listOf(FORUM)), players = Pair(
                Player(coins = 5, wonders = listOf(BuildableWonder(THE_GREAT_LIGHTHOUSE))),
                Player()
        ))
        game = game.build(FORUM)
        assertThat(game.players.first.coins).isEqualTo(0)
        game = Game(structure = Game.createStructure(AGE_II, listOf(FORUM)), players = Pair(
                Player(coins = 5, wonders = listOf(BuildableWonder(THE_GREAT_LIGHTHOUSE, builtWith = STABLE))),
                Player()
        ))
        game = game.build(FORUM)
        assertThat(game.players.first.coins).isEqualTo(2)
        game = Game(structure = Game.createStructure(AGE_II, listOf(FORUM)), players = Pair(
                Player(wonders = listOf(BuildableWonder(THE_GREAT_LIGHTHOUSE, builtWith = STABLE))),
                Player(coins = 5)
        ), currentPlayer = 2)
        game = game.build(FORUM)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun piraeus_produces_any_manufactured_good_once_built() {
        var game = Game(structure = Game.createStructure(AGE_III, listOf(CHAMBER_OF_COMMERCE)), players = Pair(
                Player(coins = 4, wonders = listOf(BuildableWonder(PIRAEUS))),
                Player()
        ))
        game = game.build(CHAMBER_OF_COMMERCE)
        assertThat(game.players.first.coins).isEqualTo(0)
        game = Game(structure = Game.createStructure(AGE_III, listOf(CHAMBER_OF_COMMERCE)), players = Pair(
                Player(coins = 4, wonders = listOf(BuildableWonder(PIRAEUS, builtWith = STABLE))),
                Player()
        ))
        game = game.build(CHAMBER_OF_COMMERCE)
        assertThat(game.players.first.coins).isEqualTo(2)
        game = Game(structure = Game.createStructure(AGE_III, listOf(CHAMBER_OF_COMMERCE)), players = Pair(
                Player(wonders = listOf(BuildableWonder(PIRAEUS, builtWith = STABLE))),
                Player(coins = 4)
        ), currentPlayer = 2)
        game = game.build(CHAMBER_OF_COMMERCE)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun the_mausoleum_let_me_build_any_discarded_card_for_free() {
        var game = Game(structure = Game.createStructure(AGE_II, listOf(DISPENSARY, DRYING_ROOM)), players = Pair(
                Player(coins = 10, wonders = listOf(BuildableWonder(THE_MAUSOLEUM))),
                Player()), discardedCards = listOf(WALLS, TRIBUNAL))
        game = game.build(THE_MAUSOLEUM, DISPENSARY)
        assertThat(game.players.first.coins).isEqualTo(0)
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.build(TRIBUNAL)
        assertThat(game.players.first.buildings).contains(TRIBUNAL)
        assertThat(game.discardedCards).doesNotContain(TRIBUNAL)
    }

    @Test(expected = IllegalArgumentException::class)
    fun the_mausoleum_does_not_let_me_build_anything() {
        val game = Game(structure = Game.createStructure(AGE_II, listOf(DISPENSARY, DRYING_ROOM)), players = Pair(
                Player(coins = 10, wonders = listOf(BuildableWonder(THE_MAUSOLEUM))),
                Player()), discardedCards = listOf(WALLS, TRIBUNAL))
        game.build(THE_MAUSOLEUM, DISPENSARY).build(PALACE)
    }
}