package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Building.*
import fr.omi.sevenwondersduel.ProgressToken.AGRICULTURE
import fr.omi.sevenwondersduel.ProgressToken.URBANISM
import fr.omi.sevenwondersduel.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TakeCoinsEffectsTest {

    private val sampleAge1Structure = createStructure(Age.AGE_I, listOf(
            CLAY_RESERVE, THEATER,
            STONE_RESERVE, QUARRY, GUARD_TOWER,
            WORKSHOP, WOOD_RESERVE, GLASSWORKS, LOGGING_CAMP,
            PALISADE, GARRISON, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, TAVERN, PHARMACIST))

    private val sampleAge2Structure = createStructure(Age.AGE_II, listOf(
            BRICKYARD, SHELF_QUARRY, FORUM, LABORATORY, BARRACKS, LIBRARY,
            AQUEDUCT, ROSTRUM, SCHOOL, DRYING_ROOM, HORSE_BREEDERS,
            WALLS, PARADE_GROUND, STATUE, TRIBUNAL,
            CUSTOMS_HOUSE, SAWMILL, GLASSBLOWER,
            BREWERY, DISPENSARY))

    @Test
    fun tavern_gives_4_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(Player(coins = 0), Player(coins = 7)))
        game = game.build(TAVERN)
        assertThat(game.players.first.coins).isEqualTo(4)
    }

    @Test
    fun brewery_gives_6_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(Player(coins = 0), Player(coins = 7)))
        game = game.build(BREWERY)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun chamber_of_commerce_gives_3_coins_per_manufacture_building_I_have() {
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(CHAMBER_OF_COMMERCE)),
                players = Pair(Player(coins = 0, buildings = setOf(PRESS, DRYING_ROOM)), Player(coins = 7, buildings = setOf(GLASSWORKS))))
        game = game.build(CHAMBER_OF_COMMERCE)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun port_gives_2_coins_per_raw_material_building_I_have() {
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(PORT)), players = Pair(
                Player(coins = 0, buildings = setOf(PRESS, GLASSWORKS, LOGGING_CAMP, BRICKYARD)),
                Player(coins = 7, buildings = setOf(CLAY_POOL, STONE_PIT, SAWMILL, SHELF_QUARRY))))
        game = game.build(PORT)
        assertThat(game.players.first.coins).isEqualTo(4)
    }

    @Test
    fun armory_gives_1_coins_per_military_building_I_have() {
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(ARMORY)), players = Pair(
                Player(coins = 0, buildings = setOf(SHELF_QUARRY, ARSENAL, GLASSBLOWER)),
                Player(coins = 7, buildings = setOf(GUARD_TOWER, WALLS))))
        game = game.build(ARMORY)
        assertThat(game.players.first.coins).isEqualTo(1)
    }

    @Test
    fun lighthouse_gives_1_coins_per_commercial_building_I_have_including_it() {
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(LIGHTHOUSE)), players = Pair(
                Player(coins = 0, buildings = setOf(CLAY_PIT, FORUM, CARAVANSERY, CLAY_RESERVE, BREWERY)),
                Player(coins = 7, buildings = setOf(ARENA))))
        game = game.build(LIGHTHOUSE)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun arena_gives_2_coins_per_wonder_I_built() {
        var game = SevenWondersDuel(structure = createStructure(Age.AGE_III, listOf(ARENA)), players = Pair(
                Player(coins = 0, buildings = setOf(BREWERY), wonders = listOf(
                        BuildableWonder(PIRAEUS),
                        BuildableWonder(THE_COLOSSUS, builtWith = GARRISON),
                        BuildableWonder(THE_PYRAMIDS, builtWith = STABLE),
                        BuildableWonder(THE_GREAT_LIBRARY))),
                Player(coins = 7, buildings = setOf(ARENA), wonders = listOf(
                        BuildableWonder(THE_HANGING_GARDENS, builtWith = LOGGING_CAMP),
                        BuildableWonder(THE_STATUE_OF_ZEUS, builtWith = BRICKYARD),
                        BuildableWonder(THE_APPIAN_WAY, builtWith = PALACE),
                        BuildableWonder(CIRCUS_MAXIMUS, builtWith = MONEYLENDERS_GUILD)))))
        game = game.build(ARENA)
        assertThat(game.players.first.coins).isEqualTo(4)
    }

    @Test
    fun agriculture_gives_6_coins() {
        var game = SevenWondersDuel(pendingActions = listOf(ChooseProgressToken(setOf(AGRICULTURE))),
                players = Pair(Player(coins = 0), Player()))
        game = game.choose(AGRICULTURE)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun urbanism_gives_6_coins() {
        var game = SevenWondersDuel(pendingActions = listOf(ChooseProgressToken(setOf(URBANISM))),
                players = Pair(Player(coins = 0), Player()))
        game = game.choose(URBANISM)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun the_appian_way_gives_3_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 8, buildings = setOf(FORUM), wonders = listOf(
                        BuildableWonder(THE_APPIAN_WAY),
                        BuildableWonder(THE_COLOSSUS, builtWith = GARRISON),
                        BuildableWonder(THE_PYRAMIDS, builtWith = STABLE),
                        BuildableWonder(THE_GREAT_LIBRARY))),
                Player()))
        game = game.build(THE_APPIAN_WAY, DISPENSARY)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun the_hanging_gardens_gives_6_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 0, buildings = setOf(PRESS, FORUM, SAWMILL), wonders = listOf(
                        BuildableWonder(THE_HANGING_GARDENS),
                        BuildableWonder(THE_COLOSSUS, builtWith = GARRISON),
                        BuildableWonder(THE_PYRAMIDS, builtWith = STABLE),
                        BuildableWonder(THE_GREAT_LIBRARY))),
                Player()))
        game = game.build(THE_HANGING_GARDENS, DISPENSARY)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun the_temple_of_artemis_gives_12_coins() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 4, buildings = setOf(PRESS, GLASSBLOWER, BRICKYARD), wonders = listOf(
                        BuildableWonder(THE_TEMPLE_OF_ARTEMIS),
                        BuildableWonder(THE_COLOSSUS, builtWith = GARRISON),
                        BuildableWonder(THE_PYRAMIDS, builtWith = STABLE),
                        BuildableWonder(THE_GREAT_LIBRARY))),
                Player()))
        game = game.build(THE_TEMPLE_OF_ARTEMIS, DISPENSARY)
        assertThat(game.players.first.coins).isEqualTo(12)
    }
}