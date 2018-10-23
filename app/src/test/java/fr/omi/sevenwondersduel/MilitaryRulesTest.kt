package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Age.AGE_I
import fr.omi.sevenwondersduel.material.Building.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MilitaryRulesTest {

    private val sampleAge1Structure = Game.createStructure(AGE_I, listOf(
            CLAY_RESERVE, THEATER,
            STONE_RESERVE, QUARRY, TAVERN,
            WORKSHOP, WOOD_RESERVE, GLASSWORKS, LOGGING_CAMP,
            PALISADE, GARRISON, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST))

    @Test
    fun the_conflict_pawn_moves_when_a_shield_is_build() {
        var game = Game(structure = sampleAge1Structure, currentPlayer = 1, conflictPawnPosition = 0)
        game = game.build(GUARD_TOWER)
        assertThat(game.conflictPawnPosition).isEqualTo(1)
    }

    @Test
    fun the_conflict_pawn_enter_a_zone_with_a_token() {
        var game = Game(structure = sampleAge1Structure, currentPlayer = 1, conflictPawnPosition = 2,
                players = Pair(Player(), Player(militaryTokensLooted = 0, coins = 7)))
        game = game.build(GUARD_TOWER)
        assertThat(game.conflictPawnPosition).isEqualTo(3)
        assertThat(game.players.second.militaryTokensLooted).isEqualTo(1)
        assertThat(game.players.second.coins).isEqualTo(5)
    }

    @Test
    fun two_military_token_might_be_looted_at_once() {
        var game = Game(conflictPawnPosition = 3,
                players = Pair(Player(), Player(militaryTokensLooted = 0, coins = 6)))
        game = game.moveConflictPawn(4)
        assertThat(game.players.second.militaryTokensLooted).isEqualTo(2)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun victory_by_military_supremacy() {
        var game = Game(conflictPawnPosition = 7)
        game = game.moveConflictPawn(2)
        assertThat(game.isOver()).isTrue()
        assertThat(game.getWinner()).isEqualTo(game.players.first)
        assertThat(game.currentPlayer).isNull()
    }
}