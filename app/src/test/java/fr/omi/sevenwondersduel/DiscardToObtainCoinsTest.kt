package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Age.AGE_I
import fr.omi.sevenwondersduel.material.Building.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DiscardToObtainCoinsTest {

    private val sampleAge1Structure = Game.createStructure(AGE_I, listOf(
            CLAY_RESERVE, THEATER,
            STONE_RESERVE, QUARRY, TAVERN,
            WORKSHOP, WOOD_RESERVE, GLASSWORKS, LOGGING_CAMP,
            PALISADE, GARRISON, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST))

    @Test
    fun discard_for_coins() {
        var game = Game(structure = sampleAge1Structure)
        game = game.discard(BATHS)
        assertThat(game.players.first.coins).isEqualTo(9)
        assertThat(game.discardedCards).containsExactly(BATHS)
    }

    @Test
    fun discard_for_more_coins() {
        var game = Game(players = Pair(Player(buildings = setOf(QUARRY, TAVERN, CLAY_RESERVE)), Player()),
                structure = sampleAge1Structure)
        game = game.discard(ALTAR)
        assertThat(game.players.first.coins).isEqualTo(11)
        assertThat(game.discardedCards).containsExactly(ALTAR)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_discard_unaccessible_building() {
        val game = Game(structure = sampleAge1Structure)
        assertThat(game.accessibleBuildings()).containsExactly(BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST)
        game.discard(STABLE)
    }
}