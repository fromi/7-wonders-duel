package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EndOfAnAgeTest {

    @Test
    fun end_of_age_I() {
        var game = SevenWondersDuel(structure = Structure(age = 1, buildings = listOf(Garrison)))
        game = game.discard(Garrison)
        assertThat(game.structure?.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure?.flatMap { it.values }?.map { it.building }?.intersect(Deck.AGE_II.buildings)).size().isEqualTo(20)
    }

    @Test
    fun end_of_age_II() {
        var game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Temple)))
        game = game.discard(Temple)
        assertThat(game.structure?.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure?.flatMap { it.values }?.map { it.building }?.intersect(Deck.AGE_III.buildings)).size().isEqualTo(17)
        assertThat(game.structure?.flatMap { it.values }?.map { it.building }?.intersect(Deck.GUILDS.buildings)).size().isEqualTo(3)
    }

    @Test
    fun weakest_military_chooses_which_player_begins_the_next_age() {
        var game = SevenWondersDuel(structure = Structure(age = 1, buildings = listOf(Press)), currentPlayer = 1, conflictPawnPosition = 3)
        game = game.discard(Press)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.choosePlayerBeginningNextAge(1).currentPlayer).isEqualTo(1)
    }

    @Test
    fun weakest_military_can_begin_the_next_age() {
        var game = SevenWondersDuel(structure = Structure(age = 1, buildings = listOf(Press)), currentPlayer = 1, conflictPawnPosition = 3)
        game = game.discard(Press)
        val nextAgeAccessibleCard = game.structure!!.last().values.first()
        game = game.choosePlayerBeginningNextAge(1).discard(nextAgeAccessibleCard.building)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.pendingActions).isEmpty()
    }

    @Test
    fun if_military_equality_last_active_player_chooses_witch_player_begins_next_age() {
        var game = SevenWondersDuel(structure = Structure(age = 1, buildings = listOf(Press)), currentPlayer = 1, conflictPawnPosition = 0)
        game = game.discard(Press)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.choosePlayerBeginningNextAge(1).currentPlayer).isEqualTo(1)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_during_wonder_selection_phase() {
        SevenWondersDuel().choosePlayerBeginningNextAge(1)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_in_the_middle_of_an_age() {
        val game = SevenWondersDuel(structure = Structure(age = 2, buildings = listOf(Library, Sawmill)))
        game.choosePlayerBeginningNextAge(1)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_if_I_was_not_last_active_player_on_previous_age() {
        val game = SevenWondersDuel(structure = Structure(age = 1, buildings = listOf(Press)), conflictPawnPosition = 0)
        game.discard(Press).choosePlayerBeginningNextAge(2).choosePlayerBeginningNextAge(1)
    }
}