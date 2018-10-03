package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Building.*
import fr.omi.sevenwondersduel.BuildingDeck.*
import fr.omi.sevenwondersduel.BuildingType.*
import fr.omi.sevenwondersduel.BuildingType.GUILD
import fr.omi.sevenwondersduel.Resource.*
import fr.omi.sevenwondersduel.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SevenWondersDuelTest {
    private val newGame = SevenWondersDuel()

    @Test
    fun there_are_two_players() {
        assertThat(newGame.players).size().isEqualTo(2)
    }

    @Test
    fun conflict_pawn_starts_neutral() {
        assertThat(newGame.conflictPawnPosition).isEqualTo(0)
    }

    @Test
    fun military_token_are_available_for_looting() {
        assertThat(newGame.players).allMatch { it.militaryTokensLooted == 0 }
    }

    @Test
    fun five_random_progress_tokens_are_available() {
        assertThat(newGame.progressTokensAvailable).size().isEqualTo(5)
    }

    @Test
    fun players_starts_with_7_coins() {
        assertThat(newGame.players).allMatch { it.coins == 7 }
    }

    @Test
    fun first_player_should_pick_a_wonder_between_4() {
        assertThat(newGame.currentPlayerNumber).isEqualTo(1)
        assertThat(newGame.wondersAvailable).size().isEqualTo(4)
    }

    @Test
    fun first_4_wonders_selection() {
        var game = SevenWondersDuel(wondersAvailable = setOf(THE_PYRAMIDS, THE_GREAT_LIGHTHOUSE, THE_COLOSSUS, THE_HANGING_GARDENS))
        game = game.choose(THE_PYRAMIDS)
        assertThat(game.wondersAvailable).containsExactly(THE_GREAT_LIGHTHOUSE, THE_COLOSSUS, THE_HANGING_GARDENS)
        assertThat(game.players[0].wonders).containsExactly(BuildableWonder(THE_PYRAMIDS))
        assertThat(game.currentPlayerNumber).isEqualTo(2)
        game = game.choose(THE_GREAT_LIGHTHOUSE)
        assertThat(game.currentPlayerNumber).isEqualTo(2)
        assertThat(game.players[1].wonders).containsExactly(BuildableWonder(THE_GREAT_LIGHTHOUSE))
        game = game.choose(THE_HANGING_GARDENS)
        assertThat(game.players[1].wonders).containsExactly(BuildableWonder(THE_GREAT_LIGHTHOUSE), BuildableWonder(THE_HANGING_GARDENS))
        assertThat(game.players[0].wonders).containsExactly(BuildableWonder(THE_PYRAMIDS), BuildableWonder(THE_COLOSSUS))
        assertThat(game.wondersAvailable).doesNotContain(THE_PYRAMIDS, THE_GREAT_LIGHTHOUSE, THE_COLOSSUS, THE_HANGING_GARDENS).size().isEqualTo(4)
        assertThat(game.currentPlayerNumber).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_select_a_wonder_not_available() {
        val game = SevenWondersDuel(wondersAvailable = setOf(THE_PYRAMIDS, THE_GREAT_LIGHTHOUSE, THE_COLOSSUS, THE_HANGING_GARDENS))
        game.choose(THE_MAUSOLEUM)
    }

    @Test
    fun last_4_wonders_selection() {
        var game = SevenWondersDuel(players = listOf(Player(number = 1, wonders = listOf(BuildableWonder(THE_COLOSSUS), BuildableWonder(THE_HANGING_GARDENS))),
                Player(number = 2, wonders = listOf(BuildableWonder(THE_GREAT_LIGHTHOUSE), BuildableWonder(THE_MAUSOLEUM)))),
                wondersAvailable = setOf(THE_PYRAMIDS, THE_GREAT_LIBRARY, THE_STATUE_OF_ZEUS, THE_TEMPLE_OF_ARTEMIS),
                currentPlayerNumber = 2)
        game = game.choose(THE_STATUE_OF_ZEUS)
        assertThat(game.wondersAvailable).containsExactly(THE_PYRAMIDS, THE_GREAT_LIBRARY, THE_TEMPLE_OF_ARTEMIS)
        assertThat(game.players[1].wonders).contains(BuildableWonder(THE_STATUE_OF_ZEUS))
        assertThat(game.currentPlayerNumber).isEqualTo(1)
        game = game.choose(THE_GREAT_LIBRARY)
        assertThat(game.wondersAvailable).containsExactly(THE_PYRAMIDS, THE_TEMPLE_OF_ARTEMIS)
        assertThat(game.players[0].wonders).contains(BuildableWonder(THE_GREAT_LIBRARY))
        game = game.choose(THE_TEMPLE_OF_ARTEMIS)
        assertThat(game.players[0].wonders).containsExactly(BuildableWonder(THE_COLOSSUS), BuildableWonder(THE_HANGING_GARDENS),
                BuildableWonder(THE_GREAT_LIBRARY), BuildableWonder(THE_TEMPLE_OF_ARTEMIS))
        assertThat(game.players[1].wonders).containsExactly(BuildableWonder(THE_GREAT_LIGHTHOUSE), BuildableWonder(THE_MAUSOLEUM),
                BuildableWonder(THE_STATUE_OF_ZEUS), BuildableWonder(THE_PYRAMIDS))
        assertThat(game.currentPlayerNumber).isEqualTo(1)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
    }

    @Test
    fun there_are_23_age_I_buildings() {
        assertThat(Building.values().filter { it.deck == AGE_I }).size().isEqualTo(23)
    }

    @Test
    fun there_are_23_age_II_buildings() {
        assertThat(Building.values().filter { it.deck == AGE_II }).size().isEqualTo(23)
    }

    @Test
    fun there_are_20_age_III_buildings() {
        assertThat(Building.values().filter { it.deck == AGE_III }).size().isEqualTo(20)
    }

    @Test
    fun there_are_7_guilds() {
        assertThat(Building.values().filter { it.deck == BuildingDeck.GUILD }).size().isEqualTo(7)
        assertThat(Building.values().filter { it.type == GUILD }).size().isEqualTo(7)
    }

    @Test
    fun there_are_9_raw_materials_buildings() {
        assertThat(Building.values().filter { it.type == RAW_MATERIAL }).size().isEqualTo(9)
    }

    @Test
    fun there_are_4_manufactured_good_buildings() {
        assertThat(Building.values().filter { it.type == MANUFACTURED_GOOD }).size().isEqualTo(4)
    }

    @Test
    fun there_are_14_military_buildings() {
        assertThat(Building.values().filter { it.type == MILITARY }).size().isEqualTo(14)
    }

    @Test
    fun there_are_12_scientific_buildings() {
        assertThat(Building.values().filter { it.type == SCIENTIFIC }).size().isEqualTo(12)
    }

    @Test
    fun there_are_14_civilian_buildings() {
        assertThat(Building.values().filter { it.type == CIVILIAN }).size().isEqualTo(14)
    }

    @Test
    fun there_are_13_commercial_buildings() {
        assertThat(Building.values().filter { it.type == COMMERCIAL }).size().isEqualTo(13)
    }

    private val sampleAge1Structure = createStructure(AGE_I, listOf(
            CLAY_RESERVE, THEATER,
            STONE_RESERVE, QUARRY, TAVERN,
            WORKSHOP, WOOD_RESERVE, GLASSWORKS, LOGGING_CAMP,
            PALISADE, PHARMACIST, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, GARRISON))

    @Test
    fun build_first_building() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(19)
        assertThat(game.players[0].buildings).containsExactly(CLAY_PIT)
        assertThat(game.currentPlayerNumber).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_unaccessible_building() {
        val game = SevenWondersDuel(structure = sampleAge1Structure)
        assertThat(game.accessibleBuildings()).containsExactly(BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, GARRISON)
        game.build(STABLE)
    }

    @Test
    fun building_become_accessible_once_uncovered() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT).build(ALTAR)
        assertThat(game.accessibleBuildings()).containsExactlyInAnyOrder(BATHS, PRESS, GUARD_TOWER, STABLE, GARRISON)
        game = game.build(STABLE)
        assertThat(game.players[0].buildings).containsExactly(CLAY_PIT, STABLE)
        assertThat(game.players[1].buildings).containsExactly(ALTAR)
    }

    @Test
    fun pay_building_cost_in_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT)
        assertThat(game.players[0].coins).isEqualTo(6)
    }

    @Test
    fun build_with_resources() {
        var game = SevenWondersDuel(players = listOf(Player(number = 1, buildings = setOf(QUARRY)), Player(number = 2)),
                structure = sampleAge1Structure)
        game = game.build(BATHS)
        assertThat(game.players[0].coins).isEqualTo(7)
    }

    @Test
    fun trade_resource_for_2_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(BATHS)
        assertThat(game.players[0].coins).isEqualTo(5)
    }

    @Test
    fun trade_resource_increases_when_opponent_produces() {
        val player = Player(number = 1, coins = 7, buildings = setOf())
        val opponent = Player(number = 2, buildings = setOf(QUARRY))
        assertThat(player.build(BATHS, opponent).coins).isEqualTo(4)
        val opponent2 = Player(number = 2, buildings = setOf(QUARRY, STONE_PIT))
        assertThat(player.build(BATHS, opponent2).coins).isEqualTo(3)
    }

    @Test
    fun resource_cost_can_be_set_to_1() {
        val player = Player(number = 1, coins = 1, buildings = setOf(STONE_RESERVE))
        val opponent = Player(number = 2, buildings = setOf(QUARRY))
        assertThat(player.build(BATHS, opponent).coins).isEqualTo(0)
    }

    @Test
    fun produce_any_raw_good() {
        val player = Player(number = 1, coins = 7, buildings = setOf(CARAVANSERY))
        val opponent = Player(number = 2)
        assertThat(player.build(BATHS, opponent).coins).isEqualTo(7)
    }

    @Test
    fun produce_any_raw_good_automatically_set_to_highest_resource_cost() {
        val player = Player(number = 1, coins = 8, buildings = setOf(CARAVANSERY))
        val opponent = Player(number = 2, buildings = setOf(QUARRY))
        assertThat(player.build(ROSTRUM, opponent).coins).isEqualTo(6)
    }

    @Test
    fun produce_any_raw_good_as_no_impact_on_trading_cost() {
        val player = Player(number = 1, coins = 7)
        val opponent = Player(number = 2, buildings = setOf(CARAVANSERY))
        assertThat(player.build(BATHS, opponent).coins).isEqualTo(5)
    }

    @Test
    fun build_for_free_with_chain() {
        val player = Player(number = 1, coins = 5, buildings = setOf(STABLE))
        val opponent = Player(number = 2)
        assertThat(player.build(HORSE_BREEDERS, opponent).coins).isEqualTo(5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_if_not_enough_coins() {
        val game = SevenWondersDuel(players = listOf(Player(number = 1, coins = 1), Player(number = 2)), structure = sampleAge1Structure)
        game.build(BATHS)
    }

    @Test
    fun verify_total_resources_costs_on_building() {
        assertThat(Building.values().sumBy { it.cost.coins }).isEqualTo(46)
        assertThat(Building.values().sumBy { it.cost.resources[CLAY] ?: 0 }).isEqualTo(32)
        assertThat(Building.values().sumBy { it.cost.resources[WOOD] ?: 0 }).isEqualTo(34)
        assertThat(Building.values().sumBy { it.cost.resources[STONE] ?: 0 }).isEqualTo(33)
        assertThat(Building.values().sumBy { it.cost.resources[GLASS] ?: 0 }).isEqualTo(21)
        assertThat(Building.values().sumBy { it.cost.resources[PAPYRUS] ?: 0 }).isEqualTo(21)
    }

    @Test
    fun discard_for_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.discard(BATHS)
        assertThat(game.players[0].coins).isEqualTo(9)
        assertThat(game.discardedCards).containsExactly(BATHS)
    }

    @Test
    fun discard_for_more_coins() {
        var game = SevenWondersDuel(players = listOf(Player(number = 1, buildings = setOf(QUARRY, TAVERN, CLAY_RESERVE)), Player(number = 2)),
                structure = sampleAge1Structure)
        game = game.discard(ALTAR)
        assertThat(game.players[0].coins).isEqualTo(11)
        assertThat(game.discardedCards).containsExactly(ALTAR)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_discard_unaccessible_building() {
        val game = SevenWondersDuel(structure = sampleAge1Structure)
        assertThat(game.accessibleBuildings()).containsExactly(BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, GARRISON)
        game.discard(STABLE)
    }

    @Test
    fun build_a_wonder() {
        var game = SevenWondersDuel(players = listOf(Player(number = 1, coins = 8, wonders = listOf(BuildableWonder(PIRAEUS))), Player(number = 2)),
                structure = sampleAge1Structure)
        game = game.build(PIRAEUS, PRESS)
        assertThat(game.players[0].wonders).containsExactly(BuildableWonder(PIRAEUS, builtWith = PRESS))
        assertThat(game.players[0].coins).isEqualTo(0)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_build_a_wonder_already_built() {
        val wonder = BuildableWonder(PIRAEUS, builtWith = PRESS)
        wonder.buildWith(STATUE)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_a_wonder_I_do_not_have() {
        val player = Player(number = 1, wonders = emptyList())
        player.build(PIRAEUS, buildingUsed = SCHOOL, opponent = Player(number = 2))
    }

    @Test
    fun once_7_wonders_built_remaining_wonder_is_discarded() {
        var game = SevenWondersDuel(players = listOf(
                Player(number = 1, coins = 20, wonders = listOf(
                        BuildableWonder(PIRAEUS, builtWith = STATUE),
                        BuildableWonder(THE_PYRAMIDS, builtWith = HORSE_BREEDERS),
                        BuildableWonder(THE_STATUE_OF_ZEUS, builtWith = ALTAR),
                        BuildableWonder(THE_COLOSSUS))),
                Player(number = 2, wonders = listOf(
                        BuildableWonder(THE_GREAT_LIBRARY, builtWith = PARADE_GROUND),
                        BuildableWonder(THE_HANGING_GARDENS, builtWith = TEMPLE),
                        BuildableWonder(THE_GREAT_LIGHTHOUSE, builtWith = GARRISON),
                        BuildableWonder(THE_MAUSOLEUM)
                ))
        ), structure = sampleAge1Structure)
        game = game.build(THE_COLOSSUS, buildingUsed = CLAY_PIT)
        assertThat(game.players[1].wonders).doesNotContain(BuildableWonder(THE_MAUSOLEUM))
    }

    @Test
    fun verify_total_resources_costs_on_wonders() {
        assertThat(Wonder.values().sumBy { it.cost.resources[CLAY] ?: 0 }).isEqualTo(10)
        assertThat(Wonder.values().sumBy { it.cost.resources[WOOD] ?: 0 }).isEqualTo(11)
        assertThat(Wonder.values().sumBy { it.cost.resources[STONE] ?: 0 }).isEqualTo(12)
        assertThat(Wonder.values().sumBy { it.cost.resources[GLASS] ?: 0 }).isEqualTo(9)
        assertThat(Wonder.values().sumBy { it.cost.resources[PAPYRUS] ?: 0 }).isEqualTo(10)
    }

    @Test
    fun end_of_age_I() {
        var game = SevenWondersDuel(structure = createStructure(AGE_I, listOf(GARRISON)))
        game = game.discard(GARRISON)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure.flatMap { it.values }).allMatch { it.deck == AGE_II }
    }

    @Test
    fun end_of_age_II() {
        var game = SevenWondersDuel(structure = createStructure(AGE_I, listOf(TEMPLE)))
        game = game.discard(TEMPLE)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure.flatMap { it.values }.count { it.deck == AGE_III }).isEqualTo(17)
        assertThat(game.structure.flatMap { it.values }.count { it.deck == BuildingDeck.GUILD }).isEqualTo(3)
    }

    @Test
    fun weakest_military_chooses_which_player_begins_the_next_age() {
        var game = SevenWondersDuel(structure = createStructure(AGE_I, listOf(GARRISON)), currentPlayerNumber = 1, conflictPawnPosition = 3)
        game = game.discard(GARRISON)
        assertThat(game.currentPlayerNumber).isEqualTo(2)
        assertThat(game.letOpponentBegin().currentPlayerNumber).isEqualTo(1)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_during_wonder_selection_phase() {
        newGame.letOpponentBegin()
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_in_the_middle_of_an_age() {
        val game = SevenWondersDuel(structure = createStructure(AGE_II, listOf(LIBRARY, SAWMILL)))
        game.letOpponentBegin()
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_if_military_equal() {
        val game = SevenWondersDuel(structure = createStructure(AGE_II), conflictPawnPosition = 0)
        game.letOpponentBegin()
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_let_opponent_begin_if_stronger_than_him() {
        val game = SevenWondersDuel(structure = createStructure(AGE_II), conflictPawnPosition = 3, currentPlayerNumber = 1)
        game.letOpponentBegin()
    }

    @Test
    fun the_conflict_pawn_moves_when_a_shield_is_build() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, currentPlayerNumber = 1, conflictPawnPosition = 0)
        game = game.build(GARRISON)
        assertThat(game.conflictPawnPosition).isEqualTo(1)
    }

    @Test
    fun the_conflict_pawn_enter_a_zone_with_a_token() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, currentPlayerNumber = 1, conflictPawnPosition = 2,
                players = listOf(Player(1), Player(2, militaryTokensLooted = 0, coins = 7)))
        game = game.build(GARRISON)
        assertThat(game.conflictPawnPosition).isEqualTo(3)
        assertThat(game.players[1].militaryTokensLooted).isEqualTo(1)
        assertThat(game.players[1].coins).isEqualTo(5)
    }

    @Test
    fun two_military_token_might_be_looted_at_once() {
        var game = SevenWondersDuel(currentPlayerNumber = 1, conflictPawnPosition = 3,
                players = listOf(Player(1), Player(2, militaryTokensLooted = 0, coins = 6)))
        game = game.moveConflictPawn(4)
        assertThat(game.players[1].militaryTokensLooted).isEqualTo(2)
        assertThat(game.players[1].coins).isEqualTo(0)
    }

    @Test
    fun victory_by_military_supremacy() {
        var game = SevenWondersDuel(currentPlayerNumber = 1, conflictPawnPosition = 7)
        game = game.moveConflictPawn(2)
        assertThat(game.isOver()).isTrue()
        assertThat(game.getWinner()?.number).isEqualTo(1)
        assertThat(game.currentPlayerNumber).isNull()
    }
}