package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building.*
import fr.omi.sevenwondersduel.material.ProgressToken.*
import fr.omi.sevenwondersduel.material.Wonder.PIRAEUS
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ScienceAndProgressTest {

    private val sampleAge1Structure = Structure(AGE_I, listOf(
            CLAY_RESERVE, THEATER,
            STONE_RESERVE, QUARRY, TAVERN,
            WORKSHOP, WOOD_RESERVE, GLASSWORKS, LOGGING_CAMP,
            PALISADE, GARRISON, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST))

    private val sampleAge2Structure = Structure(AGE_II, listOf(
            BRICKYARD, SHELF_QUARRY, FORUM, LABORATORY, BARRACKS, LIBRARY,
            AQUEDUCT, BREWERY, SCHOOL, DRYING_ROOM, HORSE_BREEDERS,
            WALLS, PARADE_GROUND, STATUE, COURTHOUSE,
            CUSTOMS_HOUSE, SAWMILL, GLASSBLOWER,
            ROSTRUM, DISPENSARY))

    private val sampleAge3Structure = Structure(AGE_III, listOf(
            CIRCUS, MERCHANTS_GUILD,
            BUILDERS_GUILD, ARSENAL, PANTHEON,
            ARENA, LIGHTHOUSE, SENATE, PALACE,
            CHAMBER_OF_COMMERCE, PORT,
            ACADEMY, SHIPOWNERS_GUILD, GARDENS, TOWN_HALL,
            STUDY, OBSERVATORY, ARMORY,
            UNIVERSITY, FORTIFICATIONS))

    @Test
    fun after_I_get_a_pair_of_science_symbol_I_choose_a_progress_token() {
        var game = SevenWondersDuel(currentPlayer = 1, structure = sampleAge2Structure,
                progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY),
                players = Pair(Player(buildings = setOf(PHARMACIST)), Player()))
        game = game.build(DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.pendingActions).containsExactly(ProgressTokenToChoose(setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY)))
        game = game.choose(LAW)
        assertThat(game.players.first.progressTokens).contains(LAW)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    val gameWithProgressTokenToChoose = SevenWondersDuel(currentPlayer = 1, structure = Structure(AGE_II, listOf(BREWERY)),
            players = Pair(Player(coins = 100, wonders = listOf(BuildableWonder(PIRAEUS))), Player()),
            pendingActions = listOf(ProgressTokenToChoose(setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY))))

    @Test(expected = IllegalStateException::class)
    fun cannot_build_when_I_have_to_choose_a_progress_token() {
        gameWithProgressTokenToChoose.build(BREWERY)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_discard_when_I_have_to_choose_a_progress_token() {
        gameWithProgressTokenToChoose.discard(BREWERY)

    }

    @Test(expected = IllegalStateException::class)
    fun cannot_build_a_wonder_when_I_have_to_choose_a_progress_token() {
        gameWithProgressTokenToChoose.build(PIRAEUS, BREWERY)

    }


    @Test
    fun get_a_science_symbol_at_the_end_of_an_age() {
        var game = SevenWondersDuel(currentPlayer = 1, conflictPawnPosition = 0, structure = Structure(AGE_II, listOf(DISPENSARY)),
                progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY),
                players = Pair(Player(buildings = setOf(PHARMACIST)), Player()))
        game = game.build(DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.structure?.age).isEqualTo(AGE_II)
        assertThat(game.pendingActions).containsExactly(ProgressTokenToChoose(setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY)))
        game = game.choose(LAW)
        assertThat(game.structure?.age).isEqualTo(AGE_III)
        assertThat(game.currentPlayer).isEqualTo(1)
    }

    @Test
    fun after_I_get_a_single_science_symbol_I_do_not_choose_a_progress_token() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(PHARMACIST)
        assertThat(game.pendingActions).isEmpty()
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_choose_a_symbol_at_any_time() {
        SevenWondersDuel(progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY)).choose(LAW)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_choose_a_symbol_not_available() {
        val game = SevenWondersDuel(currentPlayer = 1, structure = sampleAge2Structure,
                progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY),
                players = Pair(Player(buildings = setOf(PHARMACIST)), Player()))
        game.build(DISPENSARY).choose(STRATEGY)
    }

    @Test
    fun victory_by_scientific_supremacy() {
        var game = SevenWondersDuel(currentPlayer = 1, structure = sampleAge3Structure,
                players = Pair(
                        Player(buildings = setOf(PHARMACIST, SCRIPTORIUM, SCHOOL, LABORATORY),
                                progressTokens = setOf(LAW)),
                        Player()))
        game = game.build(UNIVERSITY)
        assertThat(game.isOver()).isTrue()
        assertThat(game.getWinner()).isEqualTo(game.players.first)
        assertThat(game.currentPlayer).isNull()
    }
}