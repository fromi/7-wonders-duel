package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class BuildingConstructionTest {

    private val sampleAge1Structure = Structure(age = 1, buildings = listOf(
            ClayReserve, Theater,
            StoneReserve, Quarry, Tavern,
            Workshop, WoodReserve, Glassworks, LoggingCamp,
            Palisade, Garrison, Stable, LumberYard, StonePit,
            Baths, Press, Altar, ClayPit, GuardTower, Pharmacist))

    private val sampleAge2Structure = Structure(age = 2, buildings = listOf(
            Brickyard, ShelfQuarry, Forum, Laboratory, Barracks, Library,
            Aqueduct, Brewery, School, DryingRoom, HorseBreeders,
            Walls, ParadeGround, Statue, Courthouse,
            CustomsHouse, Sawmill, Glassblower,
            Rostrum, Dispensary))

    @Test
    fun build_first_building() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(ClayPit)
        assertThat(game.structure?.sumBy { it.size }).isEqualTo(19)
        assertThat(game.players.first.buildings).containsExactly(ClayPit)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_unaccessible_building() {
        val game = SevenWondersDuel(structure = sampleAge1Structure)
        game.build(Stable)
    }

    @Test
    fun building_become_accessible_once_uncovered() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(ClayPit).build(Altar).build(Stable)
        assertThat(game.players.first.buildings).containsExactly(ClayPit, Stable)
        assertThat(game.players.second.buildings).containsExactly(Altar)
    }

    @Test
    fun pay_building_cost_in_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(ClayPit)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun build_with_resources() {
        var game = SevenWondersDuel(players = Pair(Player(buildings = setOf(Quarry)), Player()),
                structure = sampleAge1Structure)
        game = game.build(Baths)
        assertThat(game.players.first.coins).isEqualTo(7)
    }

    @Test
    fun trade_resource_for_2_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(Baths)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun trade_resource_increases_when_opponent_produces() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(Quarry))
        ))
        game = game.build(Baths)
        assertThat(game.players.first.coins).isEqualTo(4)

        game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(Quarry, StonePit))
        ))
        game = game.build(Baths)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun resource_cost_can_be_set_to_1() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 1, buildings = setOf(StoneReserve)),
                second = Player(buildings = setOf(Quarry))
        ))
        game = game.build(Baths)
        assertThat(game.players.first.coins).isEqualTo(0)
    }

    @Test
    fun produce_any_raw_good() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7, buildings = setOf(Caravansery)),
                second = Player()))
        game = game.build(Baths)
        assertThat(game.players.first.coins).isEqualTo(7)
    }

    @Test
    fun produce_any_raw_good_automatically_set_to_highest_resource_cost() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                first = Player(coins = 8, buildings = setOf(Caravansery)),
                second = Player(buildings = setOf(Quarry))
        ))
        game = game.build(Rostrum)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun produce_any_raw_good_as_no_impact_on_trading_cost() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(Caravansery))
        ))
        game = game.build(Baths)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun build_for_free_with_chain() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                first = Player(coins = 5, buildings = setOf(Pharmacist)),
                second = Player()
        ))
        game = game.build(Dispensary)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_if_not_enough_coins() {
        val game = SevenWondersDuel(players = Pair(Player(coins = 1), Player()), structure = sampleAge1Structure)
        game.build(Baths)
    }
}