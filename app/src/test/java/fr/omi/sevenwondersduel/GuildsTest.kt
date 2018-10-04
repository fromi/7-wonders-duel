package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Building.*
import fr.omi.sevenwondersduel.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GuildsTest {

    @Test
    fun count_merchant_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(MERCHANTS_GUILD, FORUM)),
                Player(buildings = setOf(CLAY_POOL, CLAY_RESERVE, CARAVANSERY, ARMORY))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
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
                                BuildableWonder(THE_TEMPLE_OF_ARTEMIS, CIRCUS),
                                BuildableWonder(PIRAEUS),
                                BuildableWonder(THE_GREAT_LIBRARY),
                                BuildableWonder(CIRCUS_MAXIMUS))),
                Player(wonders = listOf(
                        BuildableWonder(THE_PYRAMIDS, PHARMACIST),
                        BuildableWonder(THE_HANGING_GARDENS, GARRISON),
                        BuildableWonder(THE_COLOSSUS, OBELISK),
                        BuildableWonder(THE_GREAT_LIGHTHOUSE)
                ))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
    }

    @Test
    fun count_magistrates_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(MAGISTRATE_GUILD, QUARRY, BRICKYARD, DRYING_ROOM)),
                Player(buildings = setOf(CLAY_POOL, PALACE, TRIBUNAL, OBELISK, STATUE, ALTAR, ARMORY))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(5)
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
    fun count_tacticians_guild_victory_points() {
        val game = SevenWondersDuel(players = Pair(
                Player(coins = 0, buildings = setOf(TACTICIANS_GUILD, QUARRY, BRICKYARD, DRYING_ROOM, PALISADE, SIEGE_WORKSHOP, CIRCUS, HORSE_BREEDERS)),
                Player(buildings = setOf(CLAY_POOL, CLAY_RESERVE, CARAVANSERY, ARMORY, STABLE, GARRISON))))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(4)
    }
}