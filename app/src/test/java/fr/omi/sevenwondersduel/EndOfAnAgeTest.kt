package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building.*
import fr.omi.sevenwondersduel.material.Building.Deck.Guilds
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EndOfAnAgeTest {

    @Test
    fun end_of_age_I() {
        var game = SevenWondersDuel(structure = SevenWondersDuel.createStructure(AGE_I, listOf(GARRISON)))
        game = game.discard(GARRISON)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure.flatMap { it.values }).allMatch { it.deck == AGE_II }
    }

    @Test
    fun end_of_age_II() {
        var game = SevenWondersDuel(structure = SevenWondersDuel.createStructure(AGE_II, listOf(TEMPLE)), currentAge = AGE_II)
        game = game.discard(TEMPLE)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure.flatMap { it.values }.count { it.deck == AGE_III }).isEqualTo(17)
        assertThat(game.structure.flatMap { it.values }.count { it.deck == Guilds }).isEqualTo(3)
    }

    @Test
    fun weakest_military_chooses_which_player_begins_the_next_age() {
        var game = SevenWondersDuel(structure = SevenWondersDuel.createStructure(AGE_I, listOf(PRESS)), currentPlayer = 1, conflictPawnPosition = 3)
        game = game.discard(PRESS)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.choosePlayerBeginningNextAge(1).currentPlayer).isEqualTo(1)
    }

    @Test
    fun weakest_military_can_begin_the_next_age() {
        var game = SevenWondersDuel(structure = SevenWondersDuel.createStructure(AGE_I, listOf(PRESS)), currentPlayer = 1, conflictPawnPosition = 3)
        game = game.discard(PRESS)
        val nextAgeAccessibleBuilding = game.structure.last().values.first()
        game = game.choosePlayerBeginningNextAge(1).discard(nextAgeAccessibleBuilding)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.pendingActions).isEmpty()
    }

    @Test
    fun if_military_equality_last_active_player_chooses_witch_player_begins_next_age() {
        var game = SevenWondersDuel(structure = SevenWondersDuel.createStructure(AGE_I, listOf(PRESS)), currentPlayer = 1, conflictPawnPosition = 0)
        game = game.discard(PRESS)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.choosePlayerBeginningNextAge(1).currentPlayer).isEqualTo(1)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_during_wonder_selection_phase() {
        SevenWondersDuel().choosePlayerBeginningNextAge(1)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_in_the_middle_of_an_age() {
        val game = SevenWondersDuel(structure = SevenWondersDuel.createStructure(AGE_II, listOf(LIBRARY, SAWMILL)))
        game.choosePlayerBeginningNextAge(1)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_if_I_was_not_last_active_player_on_previous_age() {
        val game = SevenWondersDuel(structure = SevenWondersDuel.createStructure(AGE_I, listOf(PRESS)), conflictPawnPosition = 0)
        game.discard(PRESS).choosePlayerBeginningNextAge(2).choosePlayerBeginningNextAge(1)
    }
}