package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Age.AGE_I
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MilitaryRulesTest {

    private val sampleAge1Structure = Structure(AGE_I, listOf(
            ClayReserve, Theater,
            StoneReserve, Quarry, Tavern,
            Workshop, WoodReserve, Glassworks, LoggingCamp,
            Palisade, Garrison, Stable, LumberYard, StonePit,
            Baths, Press, Altar, ClayPit, GuardTower, Pharmacist))

    @Test
    fun the_conflict_pawn_moves_when_a_shield_is_build() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, currentPlayer = 1, conflictPawnPosition = 0)
        game = game.build(GuardTower)
        assertThat(game.conflictPawnPosition).isEqualTo(1)
    }

    @Test
    fun the_conflict_pawn_enter_a_zone_with_a_token() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, currentPlayer = 1, conflictPawnPosition = 2,
                players = Pair(Player(), Player(militaryTokensLooted = 0, coins = 7)))
        game = game.build(GuardTower)
        assertThat(game.conflictPawnPosition).isEqualTo(3)
        assertThat(game.players.second.militaryTokensLooted).isEqualTo(1)
        assertThat(game.players.second.coins).isEqualTo(5)
    }

    @Test
    fun two_military_token_might_be_looted_at_once() {
        var game = SevenWondersDuel(conflictPawnPosition = 3,
                players = Pair(Player(), Player(militaryTokensLooted = 0, coins = 6)))
        game = game.moveConflictPawn(4)
        assertThat(game.players.second.militaryTokensLooted).isEqualTo(2)
        assertThat(game.players.second.coins).isEqualTo(0)
    }

    @Test
    fun victory_by_military_supremacy() {
        var game = SevenWondersDuel(conflictPawnPosition = 7)
        game = game.moveConflictPawn(2)
        assertThat(game.isOver()).isTrue()
        assertThat(game.getWinner()).isEqualTo(game.players.first)
        assertThat(game.currentPlayer).isNull()
    }
}