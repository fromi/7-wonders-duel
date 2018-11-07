package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.ProgressToken.AGRICULTURE
import fr.omi.sevenwondersduel.material.ProgressToken.URBANISM
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TakeCoinsEffectsTest {

    private val sampleAge1Structure = Structure(age = 1, buildings = listOf(
            ClayReserve, Theater,
            StoneReserve, Quarry, GuardTower,
            Workshop, WoodReserve, Glassworks, LoggingCamp,
            Palisade, Garrison, Stable, LumberYard, StonePit,
            Baths, Press, Altar, ClayPit, Tavern, Pharmacist))

    private val sampleAge2Structure = Structure(age = 2, buildings = listOf(
            Brickyard, ShelfQuarry, Forum, Laboratory, Barracks, Library,
            Aqueduct, Rostrum, School, DryingRoom, HorseBreeders,
            Walls, ParadeGround, Statue, Courthouse,
            CustomsHouse, Sawmill, Glassblower,
            Brewery, Dispensary))

    @Test
    fun tavern_gives_4_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(Player(coins = 0), Player(coins = 7)))
        game = game.construct(Tavern)
        assertThat(game.players.first.coins).isEqualTo(4)
    }

    @Test
    fun brewery_gives_6_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(Player(coins = 0), Player(coins = 7)))
        game = game.construct(Brewery)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun chamber_of_commerce_gives_3_coins_per_manufacture_building_I_have() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(ChamberOfCommerce)),
                players = Pair(Player(coins = 0, buildings = setOf(Press, DryingRoom)), Player(coins = 7, buildings = setOf(Glassworks))))
        game = game.construct(ChamberOfCommerce)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun port_gives_2_coins_per_raw_material_building_I_have() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Port)), players = Pair(
                Player(coins = 0, buildings = setOf(Press, Glassworks, LoggingCamp, Brickyard)),
                Player(coins = 7, buildings = setOf(ClayPool, StonePit, Sawmill, ShelfQuarry))))
        game = game.construct(Port)
        assertThat(game.players.first.coins).isEqualTo(4)
    }

    @Test
    fun armory_gives_1_coins_per_military_building_I_have() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Armory)), players = Pair(
                Player(coins = 0, buildings = setOf(ShelfQuarry, Arsenal, Glassblower)),
                Player(coins = 7, buildings = setOf(GuardTower, Walls))))
        game = game.construct(Armory)
        assertThat(game.players.first.coins).isEqualTo(1)
    }

    @Test
    fun lighthouse_gives_1_coins_per_commercial_building_I_have_including_it() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Lighthouse)), players = Pair(
                Player(coins = 0, buildings = setOf(ClayPit, Forum, Caravansery, ClayReserve, Brewery)),
                Player(coins = 7, buildings = setOf(Arena))))
        game = game.construct(Lighthouse)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun arena_gives_2_coins_per_wonder_I_built() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(Arena)), players = Pair(
                Player(coins = 0, buildings = setOf(Brewery), wonders = listOf(
                        PlayerWonder(Piraeus),
                        PlayerWonder(TheColossus, buildingUnder = Garrison),
                        PlayerWonder(ThePyramids, buildingUnder = Stable),
                        PlayerWonder(TheGreatLibrary))),
                Player(coins = 7, buildings = setOf(Arena), wonders = listOf(
                        PlayerWonder(TheHangingGardens, buildingUnder = LoggingCamp),
                        PlayerWonder(TheStatueOfZeus, buildingUnder = Brickyard),
                        PlayerWonder(TheAppianWay, buildingUnder = Palace),
                        PlayerWonder(CircusMaximus, buildingUnder = MoneylendersGuild)))))
        game = game.construct(Arena)
        assertThat(game.players.first.coins).isEqualTo(4)
    }

    @Test
    fun agriculture_gives_6_coins() {
        var game = SevenWondersDuel(structure = Structure(age = 2), pendingActions = listOf(ProgressTokenToChoose(setOf(AGRICULTURE))),
                players = Pair(Player(coins = 0), Player()))
        game = game.choose(AGRICULTURE)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun urbanism_gives_6_coins() {
        var game = SevenWondersDuel(structure = Structure(age = 2), pendingActions = listOf(ProgressTokenToChoose(setOf(URBANISM))),
                players = Pair(Player(coins = 0), Player()))
        game = game.choose(URBANISM)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun the_appian_way_gives_3_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 8, buildings = setOf(Forum), wonders = listOf(
                        PlayerWonder(TheAppianWay),
                        PlayerWonder(TheColossus, buildingUnder = Garrison),
                        PlayerWonder(ThePyramids, buildingUnder = Stable),
                        PlayerWonder(TheGreatLibrary))),
                Player()))
        game = game.construct(TheAppianWay, Dispensary)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun the_hanging_gardens_gives_6_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 0, buildings = setOf(Press, Forum, Sawmill), wonders = listOf(
                        PlayerWonder(TheHangingGardens),
                        PlayerWonder(TheColossus, buildingUnder = Garrison),
                        PlayerWonder(ThePyramids, buildingUnder = Stable),
                        PlayerWonder(TheGreatLibrary))),
                Player()))
        game = game.construct(TheHangingGardens, Dispensary)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun the_temple_of_artemis_gives_12_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 4, buildings = setOf(Press, Glassblower, Brickyard), wonders = listOf(
                        PlayerWonder(TheTempleOfArtemis),
                        PlayerWonder(TheColossus, buildingUnder = Garrison),
                        PlayerWonder(ThePyramids, buildingUnder = Stable),
                        PlayerWonder(TheGreatLibrary))),
                Player()))
        game = game.construct(TheTempleOfArtemis, Dispensary)
        assertThat(game.players.first.coins).isEqualTo(12)
    }
}