package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Age.AGE_I
import fr.omi.sevenwondersduel.material.Building.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WonderConstructionTest {

    private val sampleAge1Structure = Structure(AGE_I, listOf(
            CLAY_RESERVE, THEATER,
            STONE_RESERVE, QUARRY, TAVERN,
            WORKSHOP, WOOD_RESERVE, GLASSWORKS, LOGGING_CAMP,
            PALISADE, GARRISON, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST))

    @Test
    fun build_a_wonder() {
        var game = SevenWondersDuel(players = Pair(Player(coins = 8, wonders = listOf(PlayerWonder(Piraeus))), Player()),
                structure = sampleAge1Structure)
        game = game.build(Piraeus, PRESS)
        assertThat(game.players.first.wonders).containsExactly(PlayerWonder(Piraeus, buildingUnder = PRESS))
        assertThat(game.players.first.coins).isEqualTo(0)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_build_a_wonder_already_built() {
        val wonder = PlayerWonder(Piraeus, buildingUnder = PRESS)
        wonder.buildWith(STATUE)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_a_wonder_I_do_not_have() {
        val player = Player(wonders = emptyList())
        player.build(Piraeus, buildingUsed = SCHOOL)
    }

    @Test
    fun once_7_wonders_built_remaining_wonder_is_discarded() {
        var game = SevenWondersDuel(players = Pair(
                Player(coins = 20, wonders = listOf(
                        PlayerWonder(Piraeus, buildingUnder = STATUE),
                        PlayerWonder(ThePyramids, buildingUnder = HORSE_BREEDERS),
                        PlayerWonder(TheStatueOfZeus, buildingUnder = ALTAR),
                        PlayerWonder(TheColossus))),
                Player(wonders = listOf(
                        PlayerWonder(TheGreatLibrary, buildingUnder = PARADE_GROUND),
                        PlayerWonder(TheHangingGardens, buildingUnder = TEMPLE),
                        PlayerWonder(TheGreatLighthouse, buildingUnder = GARRISON),
                        PlayerWonder(TheMausoleum)
                ))
        ), structure = sampleAge1Structure)
        game = game.build(TheColossus, buildingUsed = CLAY_PIT)
        assertThat(game.players.second.wonders).doesNotContain(PlayerWonder(TheMausoleum))
    }
}