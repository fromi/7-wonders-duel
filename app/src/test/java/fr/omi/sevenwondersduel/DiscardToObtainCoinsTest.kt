package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DiscardToObtainCoinsTest {

    private val sampleAge1Structure = Structure(age = 1, buildings = listOf(
            ClayReserve, Theater,
            StoneReserve, Quarry, Tavern,
            Workshop, WoodReserve, Glassworks, LoggingCamp,
            Palisade, Garrison, Stable, LumberYard, StonePit,
            Baths, Press, Altar, ClayPit, GuardTower, Pharmacist))

    @Test
    fun discard_for_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.discard(Baths)
        assertThat(game.players.first.coins).isEqualTo(9)
        assertThat(game.discardedCards).containsExactly(Baths)
    }

    @Test
    fun discard_for_more_coins() {
        var game = SevenWondersDuel(players = Pair(Player(buildings = setOf(Quarry, Tavern, ClayReserve)), Player()),
                structure = sampleAge1Structure)
        game = game.discard(Altar)
        assertThat(game.players.first.coins).isEqualTo(11)
        assertThat(game.discardedCards).containsExactly(Altar)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_discard_unaccessible_building() {
        val game = SevenWondersDuel(structure = sampleAge1Structure)
        game.discard(Stable)
    }
}