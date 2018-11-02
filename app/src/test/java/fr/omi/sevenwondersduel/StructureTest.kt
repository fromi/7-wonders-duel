package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.ProgressToken.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StructureTest {

    private val sampleAge2Structure = Structure(AGE_II, listOf(
            Brickyard, ShelfQuarry, Forum, Laboratory, Barracks, Library,
            Aqueduct, Rostrum, School, DryingRoom, HorseBreeders,
            Walls, ParadeGround, Statue, Courthouse,
            CustomsHouse, Sawmill, Glassblower,
            Brewery, Dispensary))

    @Test
    fun pair_row_cards_are_face_up_at_the_beginning() {
        val age1 = Structure(AGE_I)
        assertThat(age1[0].values).allMatch { it.faceUp }
        assertThat(age1[1].values).noneMatch { it.faceUp }
        assertThat(age1[2].values).allMatch { it.faceUp }
        assertThat(age1[3].values).noneMatch { it.faceUp }
        assertThat(age1[4].values).allMatch { it.faceUp }
        val age2 = Structure(AGE_II)
        assertThat(age2[0].values).allMatch { it.faceUp }
        assertThat(age2[1].values).noneMatch { it.faceUp }
        assertThat(age2[2].values).allMatch { it.faceUp }
        assertThat(age2[3].values).noneMatch { it.faceUp }
        assertThat(age2[4].values).allMatch { it.faceUp }
        val age3 = Structure(AGE_III)
        assertThat(age3[0].values).allMatch { it.faceUp }
        assertThat(age3[1].values).noneMatch { it.faceUp }
        assertThat(age3[2].values).allMatch { it.faceUp }
        assertThat(age3[3].values).noneMatch { it.faceUp }
        assertThat(age3[4].values).allMatch { it.faceUp }
        assertThat(age3[5].values).noneMatch { it.faceUp }
        assertThat(age3[6].values).allMatch { it.faceUp }
    }

    @Test
    fun after_a_card_is_taken_free_cards_below_are_revealed_for_next_turn() {
        var game = SevenWondersDuel(structure = sampleAge2Structure)
        game = game.build(Brewery)
        assertThat(game.structure!![3][-2]?.faceUp).isTrue()
        assertThat(game.structure!![3][0]?.faceUp).isFalse()
        assertThat(game.structure!![3][2]?.faceUp).isFalse()
        game = game.discard(Dispensary)
        assertThat(game.structure!![3][-2]?.faceUp).isTrue()
        assertThat(game.structure!![3][0]?.faceUp).isTrue()
        assertThat(game.structure!![3][2]?.faceUp).isTrue()
    }

    @Test
    fun cards_are_revealed_after_I_finished_current_action() {
        var progressTokenExample = SevenWondersDuel(structure = sampleAge2Structure,
                progressTokensAvailable = setOf(LAW, ECONOMY, MASONRY, MATHEMATICS, THEOLOGY),
                players = Pair(Player(buildings = setOf(Pharmacist)), Player()))
        progressTokenExample = progressTokenExample.build(Dispensary)
        assertThat(progressTokenExample.structure!![3][2]?.faceUp).isFalse()
        progressTokenExample = progressTokenExample.choose(LAW)
        assertThat(progressTokenExample.structure!![3][2]?.faceUp).isTrue()

        var statueOfZeusExample = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 42, wonders = listOf(PlayerWonder(TheStatueOfZeus))),
                Player(buildings = setOf(Sawmill))))
        statueOfZeusExample = statueOfZeusExample.build(TheStatueOfZeus, Dispensary)
        assertThat(statueOfZeusExample.structure!![3][2]?.faceUp).isFalse()
        statueOfZeusExample = statueOfZeusExample.destroy(Sawmill)
        assertThat(statueOfZeusExample.structure!![3][2]?.faceUp).isTrue()

        var mausoleumExample = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 10, wonders = listOf(PlayerWonder(TheMausoleum))),
                Player()), discardedCards = listOf(Walls, Courthouse))
        mausoleumExample = mausoleumExample.build(TheMausoleum, Dispensary)
        assertThat(mausoleumExample.structure!![3][2]?.faceUp).isFalse()
        mausoleumExample = mausoleumExample.build(Courthouse)
        assertThat(mausoleumExample.structure!![3][2]?.faceUp).isTrue()

        var playAgainExample = SevenWondersDuel(structure = sampleAge2Structure, players = Pair(
                Player(coins = 10, wonders = listOf(PlayerWonder(Piraeus))), Player()))
        playAgainExample = playAgainExample.build(Piraeus, Dispensary)
        assertThat(playAgainExample.structure!![3][2]?.faceUp).isTrue()
    }
}