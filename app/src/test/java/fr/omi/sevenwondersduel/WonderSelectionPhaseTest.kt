package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WonderSelectionPhaseTest {
    private val newGame = SevenWondersDuel()

    @Test
    fun first_player_should_pick_a_wonder_between_4() {
        assertThat(newGame.currentPlayer).isEqualTo(1)
        assertThat(newGame.wondersAvailable).size().isEqualTo(4)
    }

    @Test
    fun first_4_wonders_selection() {
        var game = SevenWondersDuel(wondersAvailable = setOf(THE_PYRAMIDS, THE_GREAT_LIGHTHOUSE, THE_COLOSSUS, THE_HANGING_GARDENS))
        game = game.choose(THE_PYRAMIDS)
        assertThat(game.wondersAvailable).containsExactly(THE_GREAT_LIGHTHOUSE, THE_COLOSSUS, THE_HANGING_GARDENS)
        assertThat(game.players.first.wonders).containsExactly(BuildableWonder(THE_PYRAMIDS))
        assertThat(game.currentPlayer).isEqualTo(2)
        game = game.choose(THE_GREAT_LIGHTHOUSE)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.players.second.wonders).containsExactly(BuildableWonder(THE_GREAT_LIGHTHOUSE))
        game = game.choose(THE_HANGING_GARDENS)
        assertThat(game.players.second.wonders).containsExactly(BuildableWonder(THE_GREAT_LIGHTHOUSE), BuildableWonder(THE_HANGING_GARDENS))
        assertThat(game.players.first.wonders).containsExactly(BuildableWonder(THE_PYRAMIDS), BuildableWonder(THE_COLOSSUS))
        assertThat(game.wondersAvailable).doesNotContain(THE_PYRAMIDS, THE_GREAT_LIGHTHOUSE, THE_COLOSSUS, THE_HANGING_GARDENS).size().isEqualTo(4)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_select_a_wonder_not_available() {
        val game = SevenWondersDuel(wondersAvailable = setOf(THE_PYRAMIDS, THE_GREAT_LIGHTHOUSE, THE_COLOSSUS, THE_HANGING_GARDENS))
        game.choose(THE_MAUSOLEUM)
    }

    @Test
    fun last_4_wonders_selection() {
        var game = SevenWondersDuel(players = Pair(Player(wonders = listOf(BuildableWonder(THE_COLOSSUS), BuildableWonder(THE_HANGING_GARDENS))),
                Player(wonders = listOf(BuildableWonder(THE_GREAT_LIGHTHOUSE), BuildableWonder(THE_MAUSOLEUM)))),
                wondersAvailable = setOf(THE_PYRAMIDS, THE_GREAT_LIBRARY, THE_STATUE_OF_ZEUS, THE_TEMPLE_OF_ARTEMIS),
                currentPlayer = 2)
        game = game.choose(THE_STATUE_OF_ZEUS)
        assertThat(game.wondersAvailable).containsExactly(THE_PYRAMIDS, THE_GREAT_LIBRARY, THE_TEMPLE_OF_ARTEMIS)
        assertThat(game.players.second.wonders).contains(BuildableWonder(THE_STATUE_OF_ZEUS))
        assertThat(game.currentPlayer).isEqualTo(1)
        game = game.choose(THE_GREAT_LIBRARY)
        assertThat(game.wondersAvailable).containsExactly(THE_PYRAMIDS, THE_TEMPLE_OF_ARTEMIS)
        assertThat(game.players.first.wonders).contains(BuildableWonder(THE_GREAT_LIBRARY))
        game = game.choose(THE_TEMPLE_OF_ARTEMIS)
        assertThat(game.players.first.wonders).containsExactly(BuildableWonder(THE_COLOSSUS), BuildableWonder(THE_HANGING_GARDENS),
                BuildableWonder(THE_GREAT_LIBRARY), BuildableWonder(THE_TEMPLE_OF_ARTEMIS))
        assertThat(game.players.second.wonders).containsExactly(BuildableWonder(THE_GREAT_LIGHTHOUSE), BuildableWonder(THE_MAUSOLEUM),
                BuildableWonder(THE_STATUE_OF_ZEUS), BuildableWonder(THE_PYRAMIDS))
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
    }
}