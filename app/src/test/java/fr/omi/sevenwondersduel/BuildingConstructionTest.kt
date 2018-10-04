package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Age.AGE_I
import fr.omi.sevenwondersduel.Age.AGE_II
import fr.omi.sevenwondersduel.Building.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BuildingConstructionTest {

    private val sampleAge1Structure = createStructure(AGE_I, listOf(
            CLAY_RESERVE, THEATER,
            STONE_RESERVE, QUARRY, TAVERN,
            WORKSHOP, WOOD_RESERVE, GLASSWORKS, LOGGING_CAMP,
            PALISADE, GARRISON, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST))


    private val sampleAge2Structure = createStructure(AGE_II, listOf(
            BRICKYARD, SHELF_QUARRY, FORUM, LABORATORY, BARRACKS, LIBRARY,
            AQUEDUCT, BREWERY, SCHOOL, DRYING_ROOM, HORSE_BREEDERS,
            WALLS, PARADE_GROUND, STATUE, TRIBUNAL,
            CUSTOMS_HOUSE, SAWMILL, GLASSBLOWER,
            ROSTRUM, DISPENSARY))

    @Test
    fun build_first_building() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(19)
        assertThat(game.players.first.buildings).containsExactly(CLAY_PIT)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_unaccessible_building() {
        val game = SevenWondersDuel(structure = sampleAge1Structure)
        assertThat(game.accessibleBuildings()).containsExactly(BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST)
        game.build(STABLE)
    }

    @Test
    fun building_become_accessible_once_uncovered() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT).build(ALTAR)
        assertThat(game.accessibleBuildings()).containsExactlyInAnyOrder(BATHS, PRESS, GUARD_TOWER, STABLE, PHARMACIST)
        game = game.build(STABLE)
        assertThat(game.players.first.buildings).containsExactly(CLAY_PIT, STABLE)
        assertThat(game.players.second.buildings).containsExactly(ALTAR)
    }

    @Test
    fun pay_building_cost_in_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun build_with_resources() {
        var game = SevenWondersDuel(players = Pair(Player(buildings = setOf(QUARRY)), Player()),
                structure = sampleAge1Structure)
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(7)
    }

    @Test
    fun trade_resource_for_2_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun trade_resource_increases_when_opponent_produces() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(QUARRY))
        ))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(4)

        game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(QUARRY, STONE_PIT))
        ))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun resource_cost_can_be_set_to_1() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 1, buildings = setOf(STONE_RESERVE)),
                second = Player(buildings = setOf(QUARRY))
        ))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(0)
    }

    @Test
    fun produce_any_raw_good() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7, buildings = setOf(CARAVANSERY)),
                second = Player()))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(7)
    }

    @Test
    fun produce_any_raw_good_automatically_set_to_highest_resource_cost() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                first = Player(coins = 8, buildings = setOf(CARAVANSERY)),
                second = Player(buildings = setOf(QUARRY))
        ))
        game = game.build(ROSTRUM)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun produce_any_raw_good_as_no_impact_on_trading_cost() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(CARAVANSERY))
        ))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun build_for_free_with_chain() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                first = Player(coins = 5, buildings = setOf(PHARMACIST)),
                second = Player()
        ))
        game = game.build(DISPENSARY)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_if_not_enough_coins() {
        val game = SevenWondersDuel(players = Pair(Player(coins = 1), Player()), structure = sampleAge1Structure)
        game.build(BATHS)
    }
}