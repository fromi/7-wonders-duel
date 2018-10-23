package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.Age.AGE_I
import fr.omi.sevenwondersduel.material.Building.*
import fr.omi.sevenwondersduel.material.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WonderConstructionTest {

    private val sampleAge1Structure = Game.createStructure(AGE_I, listOf(
            CLAY_RESERVE, THEATER,
            STONE_RESERVE, QUARRY, TAVERN,
            WORKSHOP, WOOD_RESERVE, GLASSWORKS, LOGGING_CAMP,
            PALISADE, GARRISON, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST))

    @Test
    fun build_a_wonder() {
        var game = Game(players = Pair(Player(coins = 8, wonders = listOf(BuildableWonder(PIRAEUS))), Player()),
                structure = sampleAge1Structure)
        game = game.build(PIRAEUS, PRESS)
        assertThat(game.players.first.wonders).containsExactly(BuildableWonder(PIRAEUS, builtWith = PRESS))
        assertThat(game.players.first.coins).isEqualTo(0)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_build_a_wonder_already_built() {
        val wonder = BuildableWonder(PIRAEUS, builtWith = PRESS)
        wonder.buildWith(STATUE)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_a_wonder_I_do_not_have() {
        val player = Player(wonders = emptyList())
        player.build(PIRAEUS, buildingUsed = SCHOOL)
    }

    @Test
    fun once_7_wonders_built_remaining_wonder_is_discarded() {
        var game = Game(players = Pair(
                Player(coins = 20, wonders = listOf(
                        BuildableWonder(PIRAEUS, builtWith = STATUE),
                        BuildableWonder(THE_PYRAMIDS, builtWith = HORSE_BREEDERS),
                        BuildableWonder(THE_STATUE_OF_ZEUS, builtWith = ALTAR),
                        BuildableWonder(THE_COLOSSUS))),
                Player(wonders = listOf(
                        BuildableWonder(THE_GREAT_LIBRARY, builtWith = PARADE_GROUND),
                        BuildableWonder(THE_HANGING_GARDENS, builtWith = TEMPLE),
                        BuildableWonder(THE_GREAT_LIGHTHOUSE, builtWith = GARRISON),
                        BuildableWonder(THE_MAUSOLEUM)
                ))
        ), structure = sampleAge1Structure)
        game = game.build(THE_COLOSSUS, buildingUsed = CLAY_PIT)
        assertThat(game.players.second.wonders).doesNotContain(BuildableWonder(THE_MAUSOLEUM))
    }
}