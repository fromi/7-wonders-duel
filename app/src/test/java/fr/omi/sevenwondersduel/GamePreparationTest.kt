package fr.omi.sevenwondersduel

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GamePreparationTest {
    private val newGame = SevenWondersDuel()

    @Test
    fun conflict_pawn_starts_neutral() {
        assertThat(newGame.conflictPawnPosition).isEqualTo(0)
    }

    @Test
    fun military_tokens_are_available_for_looting() {
        assertThat(newGame.players.first.militaryTokensLooted).isEqualTo(0)
        assertThat(newGame.players.second.militaryTokensLooted).isEqualTo(0)
    }

    @Test
    fun five_random_progress_tokens_are_available() {
        assertThat(newGame.progressTokensAvailable).size().isEqualTo(5)
    }

    @Test
    fun players_starts_with_7_coins() {
        assertThat(newGame.players.first.coins).isEqualTo(7)
        assertThat(newGame.players.second.coins).isEqualTo(7)
    }
}