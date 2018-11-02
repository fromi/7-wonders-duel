package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WonderSelectionPhaseTest {
    private val newGame = SevenWondersDuel()

    @Test
    fun first_player_should_pick_a_wonder_between_4() {
        assertThat(newGame.currentPlayer).isEqualTo(newGame.players.first)
        assertThat(newGame.wondersAvailable).size().isEqualTo(4)
    }

    @Test
    fun first_4_wonders_selection() {
        var game = SevenWondersDuel(wondersAvailable = setOf(ThePyramids, TheGreatLighthouse, TheColossus, TheHangingGardens))
        game = game.choose(ThePyramids)
        assertThat(game.wondersAvailable).containsExactly(TheGreatLighthouse, TheColossus, TheHangingGardens)
        assertThat(game.players.first.wonders).containsExactly(PlayerWonder(ThePyramids))
        assertThat(game.currentPlayer).isEqualTo(game.players.second)
        game = game.choose(TheGreatLighthouse)
        assertThat(game.currentPlayer).isEqualTo(game.players.second)
        assertThat(game.players.second.wonders).containsExactly(PlayerWonder(TheGreatLighthouse))
        game = game.choose(TheHangingGardens)
        assertThat(game.players.second.wonders).containsExactly(PlayerWonder(TheGreatLighthouse), PlayerWonder(TheHangingGardens))
        assertThat(game.players.first.wonders).containsExactly(PlayerWonder(ThePyramids), PlayerWonder(TheColossus))
        assertThat(game.wondersAvailable).doesNotContain(ThePyramids, TheGreatLighthouse, TheColossus, TheHangingGardens).size().isEqualTo(4)
        assertThat(game.currentPlayer).isEqualTo(game.players.second)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_select_a_wonder_not_available() {
        val game = SevenWondersDuel(wondersAvailable = setOf(ThePyramids, TheGreatLighthouse, TheColossus, TheHangingGardens))
        game.choose(TheMausoleum)
    }

    @Test
    fun last_4_wonders_selection() {
        var game = SevenWondersDuel(players = Pair(Player(wonders = listOf(PlayerWonder(TheColossus), PlayerWonder(TheHangingGardens))),
                Player(wonders = listOf(PlayerWonder(TheGreatLighthouse), PlayerWonder(TheMausoleum)))),
                wondersAvailable = setOf(ThePyramids, TheGreatLibrary, TheStatueOfZeus, TheTempleOfArtemis),
                currentPlayerNumber = 2)
        game = game.choose(TheStatueOfZeus)
        assertThat(game.wondersAvailable).containsExactly(ThePyramids, TheGreatLibrary, TheTempleOfArtemis)
        assertThat(game.players.second.wonders).contains(PlayerWonder(TheStatueOfZeus))
        assertThat(game.currentPlayer).isEqualTo(game.players.first)
        game = game.choose(TheGreatLibrary)
        assertThat(game.wondersAvailable).containsExactly(ThePyramids, TheTempleOfArtemis)
        assertThat(game.players.first.wonders).contains(PlayerWonder(TheGreatLibrary))
        game = game.choose(TheTempleOfArtemis)
        assertThat(game.players.first.wonders).containsExactly(PlayerWonder(TheColossus), PlayerWonder(TheHangingGardens),
                PlayerWonder(TheGreatLibrary), PlayerWonder(TheTempleOfArtemis))
        assertThat(game.players.second.wonders).containsExactly(PlayerWonder(TheGreatLighthouse), PlayerWonder(TheMausoleum),
                PlayerWonder(TheStatueOfZeus), PlayerWonder(ThePyramids))
        assertThat(game.currentPlayer).isEqualTo(game.players.first)
        assertThat(game.structure?.sumBy { it.size }).isEqualTo(20)
    }
}