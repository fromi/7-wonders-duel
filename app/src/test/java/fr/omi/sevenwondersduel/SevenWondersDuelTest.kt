package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Age.*
import fr.omi.sevenwondersduel.Building.*
import fr.omi.sevenwondersduel.BuildingType.*
import fr.omi.sevenwondersduel.ProgressToken.*
import fr.omi.sevenwondersduel.Resource.*
import fr.omi.sevenwondersduel.Wonder.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SevenWondersDuelTest {
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
        assertThat(Building.values().filter { it.deck == GUILDS }).size().isEqualTo(7)
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
            PALISADE, GARRISON, STABLE, LUMBER_YARD, STONE_PIT,
            BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST))

    @Test
    fun build_first_building() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(19)
        assertThat(game.players.first.buildings).containsExactly(CLAY_PIT)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_unaccessible_building() {
        val game = SevenWondersDuel(structure = sampleAge1Structure)
        assertThat(game.accessibleBuildings()).containsExactly(BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST)
        game.build(STABLE)
    }

    @Test
    fun building_become_accessible_once_uncovered() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT).build(ALTAR)
        assertThat(game.accessibleBuildings()).containsExactlyInAnyOrder(BATHS, PRESS, GUARD_TOWER, STABLE, PHARMACIST)
        game = game.build(STABLE)
        assertThat(game.players.first.buildings).containsExactly(CLAY_PIT, STABLE)
        assertThat(game.players.second.buildings).containsExactly(ALTAR)
    }

    @Test
    fun pay_building_cost_in_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(CLAY_PIT)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun build_with_resources() {
        var game = SevenWondersDuel(players = Pair(Player(buildings = setOf(QUARRY)), Player()),
                structure = sampleAge1Structure)
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(7)
    }

    @Test
    fun trade_resource_for_2_coins() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun trade_resource_increases_when_opponent_produces() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(QUARRY))
        ))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(4)

        game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(QUARRY, STONE_PIT))
        ))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(3)
    }

    @Test
    fun resource_cost_can_be_set_to_1() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 1, buildings = setOf(STONE_RESERVE)),
                second = Player(buildings = setOf(QUARRY))
        ))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(0)
    }

    @Test
    fun produce_any_raw_good() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7, buildings = setOf(CARAVANSERY)),
                second = Player()))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(7)
    }

    @Test
    fun produce_any_raw_good_automatically_set_to_highest_resource_cost() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                first = Player(coins = 8, buildings = setOf(CARAVANSERY)),
                second = Player(buildings = setOf(QUARRY))
        ))
        game = game.build(ROSTRUM)
        assertThat(game.players.first.coins).isEqualTo(6)
    }

    @Test
    fun produce_any_raw_good_as_no_impact_on_trading_cost() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, players = Pair(
                first = Player(coins = 7),
                second = Player(buildings = setOf(CARAVANSERY))
        ))
        game = game.build(BATHS)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test
    fun build_for_free_with_chain() {
        var game = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                first = Player(coins = 5, buildings = setOf(PHARMACIST)),
                second = Player()
        ))
        game = game.build(DISPENSARY)
        assertThat(game.players.first.coins).isEqualTo(5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_build_if_not_enough_coins() {
        val game = SevenWondersDuel(players = Pair(Player(coins = 1), Player()), structure = sampleAge1Structure)
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
        assertThat(game.players.first.coins).isEqualTo(9)
        assertThat(game.discardedCards).containsExactly(BATHS)
    }

    @Test
    fun discard_for_more_coins() {
        var game = SevenWondersDuel(players = Pair(Player(buildings = setOf(QUARRY, TAVERN, CLAY_RESERVE)), Player()),
                structure = sampleAge1Structure)
        game = game.discard(ALTAR)
        assertThat(game.players.first.coins).isEqualTo(11)
        assertThat(game.discardedCards).containsExactly(ALTAR)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_discard_unaccessible_building() {
        val game = SevenWondersDuel(structure = sampleAge1Structure)
        assertThat(game.accessibleBuildings()).containsExactly(BATHS, PRESS, ALTAR, CLAY_PIT, GUARD_TOWER, PHARMACIST)
        game.discard(STABLE)
    }

    @Test
    fun build_a_wonder() {
        var game = SevenWondersDuel(players = Pair(Player(coins = 8, wonders = listOf(BuildableWonder(PIRAEUS))), Player()),
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
        var game = SevenWondersDuel(players = Pair(
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
        var game = SevenWondersDuel(structure = createStructure(AGE_II, listOf(TEMPLE)), currentAge = AGE_II)
        game = game.discard(TEMPLE)
        assertThat(game.structure.sumBy { it.size }).isEqualTo(20)
        assertThat(game.structure.flatMap { it.values }.count { it.deck == AGE_III }).isEqualTo(17)
        assertThat(game.structure.flatMap { it.values }.count { it.deck == GUILDS }).isEqualTo(3)
    }

    @Test
    fun weakest_military_chooses_which_player_begins_the_next_age() {
        var game = SevenWondersDuel(structure = createStructure(AGE_I, listOf(GARRISON)), currentPlayer = 1, conflictPawnPosition = 3)
        game = game.discard(GARRISON)
        assertThat(game.currentPlayer).isEqualTo(2)
        assertThat(game.letOpponentBegin().currentPlayer).isEqualTo(1)
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
        val game = SevenWondersDuel(structure = createStructure(AGE_II), conflictPawnPosition = 3, currentPlayer = 1)
        game.letOpponentBegin()
    }

    @Test
    fun the_conflict_pawn_moves_when_a_shield_is_build() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, currentPlayer = 1, conflictPawnPosition = 0)
        game = game.build(GUARD_TOWER)
        assertThat(game.conflictPawnPosition).isEqualTo(1)
    }

    @Test
    fun the_conflict_pawn_enter_a_zone_with_a_token() {
        var game = SevenWondersDuel(structure = sampleAge1Structure, currentPlayer = 1, conflictPawnPosition = 2,
                players = Pair(Player(), Player(militaryTokensLooted = 0, coins = 7)))
        game = game.build(GUARD_TOWER)
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

    private val sampleAge2Structure = createStructure(AGE_II, listOf(
            BRICKYARD, SHELF_QUARRY, FORUM, LABORATORY, BARRACKS, LIBRARY,
            AQUEDUCT, BREWERY, SCHOOL, DRYING_ROOM, HORSE_BREEDERS,
            WALLS, PARADE_GROUND, STATUE, TRIBUNAL,
            CUSTOMS_HOUSE, SAWMILL, GLASSBLOWER,
            ROSTRUM, DISPENSARY))

    @Test
    fun after_I_get_a_pair_of_science_symbol_I_choose_a_progress_token() {
        var game = SevenWondersDuel(currentPlayer = 1, structure = sampleAge2Structure,
                progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY),
                players = Pair(Player(buildings = setOf(PHARMACIST)), Player()))
        game = game.build(DISPENSARY)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.pendingActions).containsExactly(ChooseProgressToken(setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY)))
        game = game.choose(LAW)
        assertThat(game.players.first.progressTokens).contains(LAW)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    @Test
    fun after_I_get_a_single_science_symbol_I_do_not_choose_a_progress_token() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(PHARMACIST)
        assertThat(game.pendingActions).isEmpty()
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_choose_a_symbol_at_any_time() {
        SevenWondersDuel(progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY)).choose(LAW)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannot_choose_a_symbol_not_available() {
        val game = SevenWondersDuel(currentPlayer = 1, structure = sampleAge2Structure,
                progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY),
                players = Pair(Player(buildings = setOf(PHARMACIST)), Player()))
        game.build(DISPENSARY).choose(STRATEGY)
    }

    private val sampleAge3Structure = createStructure(AGE_III, listOf(
            CIRCUS, MERCHANTS_GUILD,
            BUILDERS_GUILD, ARSENAL, PANTHEON,
            ARENA, LIGHTHOUSE, SENATE, PALACE,
            CHAMBER_OF_COMMERCE, PORT,
            ACADEMY, SHIPOWNERS_GUILD, GARDENS, TOWN_HALL,
            STUDY, OBSERVATORY, ARMORY,
            UNIVERSITY, FORTIFICATIONS))

    @Test
    fun victory_by_scientific_supremacy() {
        var game = SevenWondersDuel(currentPlayer = 1, structure = sampleAge3Structure,
                players = Pair(
                        Player(buildings = setOf(PHARMACIST, SCRIPTORIUM, SCHOOL, LABORATORY),
                                progressTokens = setOf(LAW)),
                        Player()))
        game = game.build(UNIVERSITY)
        assertThat(game.isOver()).isTrue()
        assertThat(game.getWinner()).isEqualTo(game.players.first)
        assertThat(game.currentPlayer).isNull()
    }

    @Test
    fun civilian_victory() {
        var game = SevenWondersDuel(structure = createStructure(AGE_III, listOf(PORT)), players = Pair(
                Player(coins = 3, buildings = setOf(THEATER, ROSTRUM, AQUEDUCT, PALACE, OBELISK)),
                Player(coins = 0, buildings = emptySet())),
                currentPlayer = 2, currentAge = AGE_III)
        game = game.discard(PORT)
        assertThat(game.isOver()).isTrue()
        assertThat(game.getWinner()).isEqualTo(game.players.first)
    }

    @Test
    fun count_military_points() {
        var game = SevenWondersDuel(conflictPawnPosition = -8, players = Pair(first = Player(coins = 0), second = Player(coins = 1)))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(0)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(10)
        game = game.copy(conflictPawnPosition = 2)
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(2)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(0)
        game = game.copy(conflictPawnPosition = 4)
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(5)
        assertThat(game.countVictoryPoint(game.players.second)).isEqualTo(0)
    }

    @Test
    fun count_treasure() {
        var game = SevenWondersDuel(players = Pair(Player(coins = 17), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(5)
        game = SevenWondersDuel(players = Pair(Player(coins = 9), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(3)
        game = SevenWondersDuel(players = Pair(Player(coins = 4), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(1)
    }

    @Test
    fun count_civilian_buildings() {
        val game = SevenWondersDuel(players = Pair(Player(coins = 0, buildings = setOf(THEATER, PALACE)), Player()))
        assertThat(game.countVictoryPoint(game.players.first)).isEqualTo(10)
        val totalCivilianBuildingsPoints = Building.values().filter { it.type == CIVILIAN }.flatMap { it.effects }.asSequence()
                .filterIsInstance<VictoryPoints>().sumBy { it.count(game, game.players.first) }
        assertThat(totalCivilianBuildingsPoints).isEqualTo(67)
    }
}