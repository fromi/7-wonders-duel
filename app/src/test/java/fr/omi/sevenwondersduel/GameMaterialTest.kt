package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Age.*
import fr.omi.sevenwondersduel.BuildingType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GameMaterialTest {

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
        assertThat(Building.values().filter { it.type == MANUFACTURE }).size().isEqualTo(4)
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

    @Test
    fun verify_total_resources_costs_on_building() {
        assertThat(Building.values().sumBy { it.cost.coins }).isEqualTo(46)
        assertThat(Building.values().sumBy { it.cost.resources[Resource.CLAY] ?: 0 }).isEqualTo(32)
        assertThat(Building.values().sumBy { it.cost.resources[Resource.WOOD] ?: 0 }).isEqualTo(34)
        assertThat(Building.values().sumBy { it.cost.resources[Resource.STONE] ?: 0 }).isEqualTo(33)
        assertThat(Building.values().sumBy { it.cost.resources[Resource.GLASS] ?: 0 }).isEqualTo(21)
        assertThat(Building.values().sumBy { it.cost.resources[Resource.PAPYRUS] ?: 0 }).isEqualTo(21)
    }

    @Test
    fun verify_total_resources_costs_on_wonders() {
        assertThat(Wonder.values().sumBy { it.cost.resources[Resource.CLAY] ?: 0 }).isEqualTo(10)
        assertThat(Wonder.values().sumBy { it.cost.resources[Resource.WOOD] ?: 0 }).isEqualTo(11)
        assertThat(Wonder.values().sumBy { it.cost.resources[Resource.STONE] ?: 0 }).isEqualTo(12)
        assertThat(Wonder.values().sumBy { it.cost.resources[Resource.GLASS] ?: 0 }).isEqualTo(9)
        assertThat(Wonder.values().sumBy { it.cost.resources[Resource.PAPYRUS] ?: 0 }).isEqualTo(10)
    }
}