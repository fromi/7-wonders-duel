package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.effects.ProgressTokenToChoose
import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.ProgressToken.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ScienceAndProgressTest {

    private val sampleAge1Structure = Structure(AGE_I, listOf(
            ClayReserve, Theater,
            StoneReserve, Quarry, Tavern,
            Workshop, WoodReserve, Glassworks, LoggingCamp,
            Palisade, Garrison, Stable, LumberYard, StonePit,
            Baths, Press, Altar, ClayPit, GuardTower, Pharmacist))

    private val sampleAge2Structure = Structure(AGE_II, listOf(
            Brickyard, ShelfQuarry, Forum, Laboratory, Barracks, Library,
            Aqueduct, Brewery, School, DryingRoom, HorseBreeders,
            Walls, ParadeGround, Statue, Courthouse,
            CustomsHouse, Sawmill, Glassblower,
            Rostrum, Dispensary))

    private val sampleAge3Structure = Structure(AGE_III, listOf(
            Circus, MerchantsGuild,
            BuildersGuild, Arsenal, Pantheon,
            Arena, Lighthouse, Senate, Palace,
            ChamberOfCommerce, Port,
            Academy, ShipownersGuild, Gardens, TownHall,
            Study, Observatory, Armory,
            University, Fortifications))

    @Test
    fun after_I_get_a_pair_of_science_symbol_I_choose_a_progress_token() {
        var game = SevenWondersDuel(currentPlayer = 1, structure = sampleAge2Structure,
                progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY),
                players = Pair(Player(buildings = setOf(Pharmacist)), Player()))
        game = game.build(Dispensary)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.pendingActions).containsExactly(ProgressTokenToChoose(setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY)))
        game = game.choose(LAW)
        assertThat(game.players.first.progressTokens).contains(LAW)
        assertThat(game.currentPlayer).isEqualTo(2)
    }

    val gameWithProgressTokenToChoose = SevenWondersDuel(currentPlayer = 1, structure = Structure(AGE_II, listOf(Brewery)),
            players = Pair(Player(coins = 100, wonders = listOf(PlayerWonder(Piraeus))), Player()),
            pendingActions = listOf(ProgressTokenToChoose(setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY))))

    @Test(expected = IllegalStateException::class)
    fun cannot_build_when_I_have_to_choose_a_progress_token() {
        gameWithProgressTokenToChoose.build(Brewery)
    }

    @Test(expected = IllegalStateException::class)
    fun cannot_discard_when_I_have_to_choose_a_progress_token() {
        gameWithProgressTokenToChoose.discard(Brewery)

    }

    @Test(expected = IllegalStateException::class)
    fun cannot_build_a_wonder_when_I_have_to_choose_a_progress_token() {
        gameWithProgressTokenToChoose.build(Piraeus, Brewery)

    }


    @Test
    fun get_a_science_symbol_at_the_end_of_an_age() {
        var game = SevenWondersDuel(currentPlayer = 1, conflictPawnPosition = 0, structure = Structure(AGE_II, listOf(Dispensary)),
                progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY),
                players = Pair(Player(buildings = setOf(Pharmacist)), Player()))
        game = game.build(Dispensary)
        assertThat(game.currentPlayer).isEqualTo(1)
        assertThat(game.structure?.age).isEqualTo(AGE_II)
        assertThat(game.pendingActions).containsExactly(ProgressTokenToChoose(setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY)))
        game = game.choose(LAW)
        assertThat(game.structure?.age).isEqualTo(AGE_III)
        assertThat(game.currentPlayer).isEqualTo(1)
    }

    @Test
    fun after_I_get_a_single_science_symbol_I_do_not_choose_a_progress_token() {
        var game = SevenWondersDuel(structure = sampleAge1Structure)
        game = game.build(Pharmacist)
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
                players = Pair(Player(buildings = setOf(Pharmacist)), Player()))
        game.build(Dispensary).choose(STRATEGY)
    }

    @Test
    fun victory_by_scientific_supremacy() {
        var game = SevenWondersDuel(currentPlayer = 1, structure = sampleAge3Structure,
                players = Pair(
                        Player(buildings = setOf(Pharmacist, Scriptorium, School, Laboratory),
                                progressTokens = setOf(LAW)),
                        Player()))
        game = game.build(University)
        assertThat(game.isOver()).isTrue()
        assertThat(game.getWinner()).isEqualTo(game.players.first)
        assertThat(game.currentPlayer).isNull()
    }
}