package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Age.*
import fr.omi.sevenwondersduel.Building.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EndOfAnAgeTest {

    @Test
    fun end_of_age_I() {
        var game = SevenWondersDuel(structure = createStructure(AGE_I, listOf(GARRISON)))
        game = game.discard(GARRISON)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure.flatMap { it.values }).allMatch { it.deck == AGE_II }
    }

    @Test
    fun end_of_age_II() {
        var game = SevenWondersDuel(structure = createStructure(AGE_II, listOf(TEMPLE)), currentAge = AGE_II)
        game = game.discard(TEMPLE)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure.flatMap { it.values }.count { it.deck == AGE_III }).isEqualTo(17)
        assertThat(game.structure.flatMap { it.values }.count { it.deck == GUILDS }).isEqualTo(3)
    }

    @Test
    fun weakest_military_chooses_which_player_begins_the_next_age() {
        var game = SevenWondersDuel(structure = createStructure(AGE_I, listOf(GARRISON)), currentPlayer = 1, conflictPawnPosition = 3)
        game = game.discard(GARRISON)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.letOpponentBegin().currentPlayer).isEqualTo(1)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_during_wonder_selection_phase() {
        SevenWondersDuel().letOpponentBegin()
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_in_the_middle_of_an_age() {
        val game = SevenWondersDuel(structure = createStructure(AGE_II, listOf(LIBRARY, SAWMILL)))
        game.letOpponentBegin()
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_if_military_equal() {
        val game = SevenWondersDuel(structure = createStructure(AGE_II), conflictPawnPosition = 0)
        game.letOpponentBegin()
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_if_stronger_than_him() {
        val game = SevenWondersDuel(structure = createStructure(AGE_II), conflictPawnPosition = 3, currentPlayer = 1)
        game.letOpponentBegin()
    }
}