package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WonderConstructionTest {

    private val sampleAge1Structure = Structure(age = 1, buildings = listOf(
            ClayReserve, Theater,
            StoneReserve, Quarry, Tavern,
            Workshop, WoodReserve, Glassworks, LoggingCamp,
            Palisade, Garrison, Stable, LumberYard, StonePit,
            Baths, Press, Altar, ClayPit, GuardTower, Pharmacist))

    @Test
    fun build_a_wonder() {
        var game = SevenWondersDuel(players = Pair(Player(coins = 8, wonders = listOf(PlayerWonder(Piraeus))), Player()),
                structure = sampleAge1Structure)
        game = game.construct(Piraeus, Press)
        assertThat(game.players.first.wonders).containsExactly(PlayerWonder(Piraeus, buildingUnder = Press))
        assertThat(game.players.first.coins).isEqualTo(0)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_build_a_wonder_already_built() {
        val wonder = PlayerWonder(Piraeus, buildingUnder = Press)
        wonder.constructWith(Statue)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_a_wonder_I_do_not_have() {
        val player = Player(wonders = emptyList())
        player.construct(Piraeus, buildingUsed = School)
    }

    @Test
    fun once_7_wonders_built_remaining_wonder_is_discarded() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                Player(coins = 20, wonders = listOf(
                        PlayerWonder(Piraeus, buildingUnder = Statue),
                        PlayerWonder(ThePyramids, buildingUnder = HorseBreeders),
                        PlayerWonder(TheStatueOfZeus, buildingUnder = Altar),
                        PlayerWonder(TheColossus))),
                Player(wonders = listOf(
                        PlayerWonder(TheGreatLibrary, buildingUnder = ParadeGround),
                        PlayerWonder(TheHangingGardens, buildingUnder = Temple),
                        PlayerWonder(TheGreatLighthouse, buildingUnder = Garrison),
                        PlayerWonder(TheMausoleum)
                ))
        ))
        game = game.construct(TheColossus, buildingUsed = ClayPit)
        assertThat(game.players.second.wonders).doesNotContain(PlayerWonder(TheMausoleum))
    }
}