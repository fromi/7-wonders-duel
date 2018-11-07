package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GuildsTest {

    @Test
    fun merchant_guild_gives_1_coins_per_commercial_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(MerchantsGuild)), players = Pair(
                Player(coins = 0, buildings = setOf(ClayPool, LumberYard, Glassblower, Forum)),
                Player(coins = 7, buildings = setOf(Arena, Caravansery, CustomsHouse, ClayReserve))))
        game = game.construct(MerchantsGuild)
        assertThat(game.players.first.coins).isEqualTo(4)
    }

    @Test
    fun count_merchant_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(MerchantsGuild, Forum)),
                Player(buildings = setOf(ClayPool, ClayReserve, Caravansery, Armory))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
    }

    @Test
    fun shipowner_guild_gives_1_coins_per_material_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(ShipownersGuild)), players = Pair(
                Player(coins = 0, buildings = setOf(Brickyard, Glassblower, DryingRoom, Caravansery)),
                Player(coins = 7, buildings = setOf(ShelfQuarry))))
        game = game.construct(ShipownersGuild)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun count_shipowner_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(ShipownersGuild, Quarry, Brickyard, DryingRoom)),
                Player(buildings = setOf(ClayPool, ClayReserve, Caravansery, Armory))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
    }

    @Test
    fun count_builders_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(BuildersGuild),
                        wonders = listOf(
                                PlayerWonder(TheTempleOfArtemis, buildingUnder = Circus),
                                PlayerWonder(Piraeus),
                                PlayerWonder(TheGreatLibrary),
                                PlayerWonder(CircusMaximus))),
                Player(wonders = listOf(
                        PlayerWonder(ThePyramids, buildingUnder = Pharmacist),
                        PlayerWonder(TheHangingGardens, buildingUnder = Garrison),
                        PlayerWonder(TheColossus, buildingUnder = Obelisk),
                        PlayerWonder(TheGreatLighthouse)
                ))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(6)
    }

    @Test
    fun magistrates_guild_gives_1_coins_per_civilian_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(MagistrateGuild)), players = Pair(
                Player(coins = 4, buildings = setOf(LoggingCamp, Brickyard, Forum, Temple)),
                Player(coins = 7, buildings = setOf(Sawmill, Glassblower, Courthouse, Statue, Pantheon, Senate, Baths))))
        game = game.construct(MagistrateGuild)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun count_magistrates_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(MagistrateGuild, Quarry, Brickyard, DryingRoom)),
                Player(buildings = setOf(ClayPool, Palace, Courthouse, Obelisk, Statue, Altar, Armory))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(5)
    }

    @Test
    fun scientists_guild_gives_1_coins_per_scientific_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(ScientistsGuild)), players = Pair(
                Player(coins = 0, buildings = setOf(Sawmill, Brickyard, Apothecary, Library, Dispensary)),
                Player(coins = 7, buildings = setOf(Workshop))))
        game = game.construct(ScientistsGuild)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun count_scientists_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(ScientistsGuild, Quarry, Brickyard, DryingRoom, Scriptorium, Observatory)),
                Player(buildings = setOf(ClayPool, ClayReserve, Caravansery, Armory, Pharmacist, School))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(4)
    }

    @Test
    fun count_moneylender_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 5, buildings = setOf(MoneylendersGuild)),
                Player(coins = 19)))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(7)
    }

    @Test
    fun tacticians_guild_gives_1_coins_per_military_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(age = 3, buildings = listOf(TacticiansGuild)), players = Pair(
                Player(coins = 2, buildings = setOf(Quarry, DryingRoom, Caravansery, Palisade, Fortifications, Circus)),
                Player(coins = 7, buildings = setOf(ShelfQuarry, Walls, Arsenal, ArcheryRange))))
        game = game.construct(TacticiansGuild)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun count_tacticians_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(TacticiansGuild, Quarry, Brickyard, DryingRoom, Palisade, SiegeWorkshop, Circus, HorseBreeders)),
                Player(buildings = setOf(ClayPool, ClayReserve, Caravansery, Armory, Stable, Garrison))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(4)
    }
}