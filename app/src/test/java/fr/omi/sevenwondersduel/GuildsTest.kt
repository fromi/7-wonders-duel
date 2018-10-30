package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Building.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GuildsTest {

    @Test
    fun merchant_guild_gives_1_coins_per_commercial_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(Age.AGE_III, listOf(MERCHANTS_GUILD)), players = Pair(
                Player(coins = 0, buildings = setOf(CLAY_POOL, LUMBER_YARD, GLASSBLOWER, FORUM)),
                Player(coins = 7, buildings = setOf(ARENA, CARAVANSERY, CUSTOMS_HOUSE, CLAY_RESERVE))))
        game = game.build(MERCHANTS_GUILD)
        assertThat(game.players.first.coins).isEqualTo(4)
    }

    @Test
    fun count_merchant_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(MERCHANTS_GUILD, FORUM)),
                Player(buildings = setOf(CLAY_POOL, CLAY_RESERVE, CARAVANSERY, ARMORY))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
    }

    @Test
    fun shipowner_guild_gives_1_coins_per_material_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(Age.AGE_III, listOf(SHIPOWNERS_GUILD)), players = Pair(
                Player(coins = 0, buildings = setOf(BRICKYARD, GLASSBLOWER, DRYING_ROOM, CARAVANSERY)),
                Player(coins = 7, buildings = setOf(SHELF_QUARRY))))
        game = game.build(SHIPOWNERS_GUILD)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun count_shipowner_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(SHIPOWNERS_GUILD, QUARRY, BRICKYARD, DRYING_ROOM)),
                Player(buildings = setOf(CLAY_POOL, CLAY_RESERVE, CARAVANSERY, ARMORY))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
    }

    @Test
    fun count_builders_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(BUILDERS_GUILD),
                        wonders = listOf(
                                PlayerWonder(TheTempleOfArtemis, buildingUnder = CIRCUS),
                                PlayerWonder(Piraeus),
                                PlayerWonder(TheGreatLibrary),
                                PlayerWonder(CircusMaximus))),
                Player(wonders = listOf(
                        PlayerWonder(ThePyramids, buildingUnder = PHARMACIST),
                        PlayerWonder(TheHangingGardens, buildingUnder = GARRISON),
                        PlayerWonder(TheColossus, buildingUnder = OBELISK),
                        PlayerWonder(TheGreatLighthouse)
                ))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
    }

    @Test
    fun magistrates_guild_gives_1_coins_per_civilian_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(Age.AGE_III, listOf(MAGISTRATE_GUILD)), players = Pair(
                Player(coins = 4, buildings = setOf(LOGGING_CAMP, BRICKYARD, FORUM, TEMPLE)),
                Player(coins = 7, buildings = setOf(SAWMILL, GLASSBLOWER, COURTHOUSE, STATUE, PANTHEON, SENATE, BATHS))))
        game = game.build(MAGISTRATE_GUILD)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun count_magistrates_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(MAGISTRATE_GUILD, QUARRY, BRICKYARD, DRYING_ROOM)),
                Player(buildings = setOf(CLAY_POOL, PALACE, COURTHOUSE, OBELISK, STATUE, ALTAR, ARMORY))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(5)
    }

    @Test
    fun scientists_guild_gives_1_coins_per_scientific_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(Age.AGE_III, listOf(SCIENTISTS_GUILD)), players = Pair(
                Player(coins = 0, buildings = setOf(SAWMILL, BRICKYARD, APOTHECARY, LIBRARY, DISPENSARY)),
                Player(coins = 7, buildings = setOf(WORKSHOP))))
        game = game.build(SCIENTISTS_GUILD)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun count_scientists_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(SCIENTISTS_GUILD, QUARRY, BRICKYARD, DRYING_ROOM, SCRIPTORIUM, OBSERVATORY)),
                Player(buildings = setOf(CLAY_POOL, CLAY_RESERVE, CARAVANSERY, ARMORY, PHARMACIST, SCHOOL))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(4)
    }

    @Test
    fun count_moneylender_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 5, buildings = setOf(MONEYLENDERS_GUILD)),
                Player(coins = 19)))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(7)
    }

    @Test
    fun tacticians_guild_gives_1_coins_per_military_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = Structure(Age.AGE_III, listOf(TACTICIANS_GUILD)), players = Pair(
                Player(coins = 2, buildings = setOf(QUARRY, DRYING_ROOM, CARAVANSERY, PALISADE, FORTIFICATIONS, CIRCUS)),
                Player(coins = 7, buildings = setOf(SHELF_QUARRY, WALLS, ARSENAL, ARCHERY_RANGE))))
        game = game.build(TACTICIANS_GUILD)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun count_tacticians_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(TACTICIANS_GUILD, QUARRY, BRICKYARD, DRYING_ROOM, PALISADE, SIEGE_WORKSHOP, CIRCUS, HORSE_BREEDERS)),
                Player(buildings = setOf(CLAY_POOL, CLAY_RESERVE, CARAVANSERY, ARMORY, STABLE, GARRISON))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(4)
    }
}