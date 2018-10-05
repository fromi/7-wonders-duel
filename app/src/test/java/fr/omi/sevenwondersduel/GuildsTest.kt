package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Building.*
import fr.omi.sevenwondersduel.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GuildsTest {

    @Test
    fun merchant_guild_gives_1_coins_per_commercial_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(MERCHANTS_GUILD)), players = Pair(
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
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(SHIPOWNERS_GUILD)), players = Pair(
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
                                BuildableWonder(THE_TEMPLE_OF_ARTEMIS, builtWith = CIRCUS),
                                BuildableWonder(PIRAEUS),
                                BuildableWonder(THE_GREAT_LIBRARY),
                                BuildableWonder(CIRCUS_MAXIMUS))),
                Player(wonders = listOf(
                        BuildableWonder(THE_PYRAMIDS, builtWith = PHARMACIST),
                        BuildableWonder(THE_HANGING_GARDENS, builtWith = GARRISON),
                        BuildableWonder(THE_COLOSSUS, builtWith = OBELISK),
                        BuildableWonder(THE_GREAT_LIGHTHOUSE)
                ))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
    }

    @Test
    fun magistrates_guild_gives_1_coins_per_civilian_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(MAGISTRATE_GUILD)), players = Pair(
                Player(coins = 4, buildings = setOf(LOGGING_CAMP, BRICKYARD, FORUM, TEMPLE)),
                Player(coins = 7, buildings = setOf(SAWMILL, GLASSBLOWER, TRIBUNAL, STATUE, PANTHEON, SENATE, BATHS))))
        game = game.build(MAGISTRATE_GUILD)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun count_magistrates_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(MAGISTRATE_GUILD, QUARRY, BRICKYARD, DRYING_ROOM)),
                Player(buildings = setOf(CLAY_POOL, PALACE, TRIBUNAL, OBELISK, STATUE, ALTAR, ARMORY))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(5)
    }

    @Test
    fun scientists_guild_gives_1_coins_per_scientific_buildings_in_city_which_has_the_most() {
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(SCIENTISTS_GUILD)), players = Pair(
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
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(TACTICIANS_GUILD)), players = Pair(
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