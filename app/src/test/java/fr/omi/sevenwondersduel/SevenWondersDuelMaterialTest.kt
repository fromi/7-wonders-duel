package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.material.*
import fr.omi.sevenwondersduel.material.Deck.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SevenWondersDuelMaterialTest {

    @Test
    fun there_are_23_age_I_buildings() {
        assertThat(AGE_I.buildings).size().isEqualTo(23)
    }

    @Test
    fun there_are_23_age_II_buildings() {
        assertThat(AGE_II.buildings).size().isEqualTo(23)
    }

    @Test
    fun there_are_20_age_III_buildings() {
        assertThat(AGE_III.buildings).size().isEqualTo(20)
    }

    @Test
    fun there_are_7_guilds() {
        assertThat(GUILDS.buildings).size().isEqualTo(7)
    }

    @Test
    fun there_are_9_raw_materials_buildings() {
        assertThat(values().flatMap { it.buildings }.filterIsInstance<RawMaterialBuilding>()).size().isEqualTo(9)
    }

    @Test
    fun there_are_4_manufactured_good_buildings() {
        assertThat(values().flatMap { it.buildings }.filterIsInstance<ManufactureBuilding>()).size().isEqualTo(4)
    }

    @Test
    fun there_are_14_military_buildings() {
        assertThat(values().flatMap { it.buildings }.filterIsInstance<MilitaryBuilding>()).size().isEqualTo(14)
    }

    @Test
    fun there_are_12_scientific_buildings() {
        assertThat(values().flatMap { it.buildings }.filterIsInstance<ScientificBuilding>()).size().isEqualTo(12)
    }

    @Test
    fun there_are_14_civilian_buildings() {
        assertThat(values().flatMap { it.buildings }.filterIsInstance<CivilianBuilding>()).size().isEqualTo(14)
    }

    @Test
    fun there_are_13_commercial_buildings() {
        assertThat(values().flatMap { it.buildings }.filterIsInstance<CommercialBuilding>()).size().isEqualTo(13)
    }

    @Test
    fun verify_total_resources_costs_on_building() {
        assertThat(values().flatMap { it.buildings }.sumBy { it.cost.coins }).isEqualTo(46)
        assertThat(values().flatMap { it.buildings }.sumBy { it.cost.resources[Resource.CLAY] ?: 0 }).isEqualTo(32)
        assertThat(values().flatMap { it.buildings }.sumBy { it.cost.resources[Resource.WOOD] ?: 0 }).isEqualTo(34)
        assertThat(values().flatMap { it.buildings }.sumBy { it.cost.resources[Resource.STONE] ?: 0 }).isEqualTo(33)
        assertThat(values().flatMap { it.buildings }.sumBy { it.cost.resources[Resource.GLASS] ?: 0 }).isEqualTo(21)
        assertThat(values().flatMap { it.buildings }.sumBy { it.cost.resources[Resource.PAPYRUS] ?: 0 }).isEqualTo(21)
    }

    @Test
    fun verify_total_resources_costs_on_wonders() {
        assertThat(wonders.sumBy { it.cost.resources[Resource.CLAY] ?: 0 }).isEqualTo(10)
        assertThat(wonders.sumBy { it.cost.resources[Resource.WOOD] ?: 0 }).isEqualTo(11)
        assertThat(wonders.sumBy { it.cost.resources[Resource.STONE] ?: 0 }).isEqualTo(12)
        assertThat(wonders.sumBy { it.cost.resources[Resource.GLASS] ?: 0 }).isEqualTo(9)
        assertThat(wonders.sumBy { it.cost.resources[Resource.PAPYRUS] ?: 0 }).isEqualTo(10)
    }
}