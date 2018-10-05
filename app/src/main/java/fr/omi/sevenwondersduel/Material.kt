package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.Age.*
import fr.omi.sevenwondersduel.Resource.Type.MANUFACTURED_GOOD
import fr.omi.sevenwondersduel.Resource.Type.RAW_GOOD

enum class Wonder(val effects: List<Effect>, val cost: Cost) {
    THE_APPIAN_WAY(listOf(VictoryPoints(3)), Cost(resources = mapOf(Resource.PAPYRUS to 1, Resource.CLAY to 2, Resource.STONE to 2))),
    CIRCUS_MAXIMUS(listOf(Shield(1), VictoryPoints(3)), Cost(resources = mapOf(Resource.GLASS to 1, Resource.WOOD to 1, Resource.STONE to 2))),
    THE_COLOSSUS(listOf(Shield(2), VictoryPoints(3)), Cost(resources = mapOf(Resource.GLASS to 1, Resource.CLAY to 3))),
    THE_GREAT_LIBRARY(listOf(VictoryPoints(4)), Cost(resources = mapOf(Resource.PAPYRUS to 1, Resource.GLASS to 1, Resource.WOOD to 3))),
    THE_GREAT_LIGHTHOUSE(listOf(VictoryPoints(4)), Cost(resources = mapOf(Resource.PAPYRUS to 2, Resource.STONE to 1, Resource.WOOD to 1))),
    THE_HANGING_GARDENS(listOf(VictoryPoints(3)), Cost(resources = mapOf(Resource.PAPYRUS to 1, Resource.GLASS to 1, Resource.WOOD to 2))),
    THE_MAUSOLEUM(listOf(VictoryPoints(2)), Cost(resources = mapOf(Resource.PAPYRUS to 1, Resource.GLASS to 2, Resource.CLAY to 2))),
    PIRAEUS(listOf(VictoryPoints(2)), Cost(resources = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.WOOD to 2))),
    THE_PYRAMIDS(listOf(VictoryPoints(9)), Cost(resources = mapOf(Resource.PAPYRUS to 1, Resource.STONE to 3))),
    THE_SPHINX(listOf(VictoryPoints(6)), Cost(resources = mapOf(Resource.GLASS to 2, Resource.CLAY to 1, Resource.STONE to 1))),
    THE_STATUE_OF_ZEUS(listOf(Shield(1), VictoryPoints(3)), Cost(resources = mapOf(Resource.PAPYRUS to 2, Resource.CLAY to 1, Resource.WOOD to 1, Resource.STONE to 1))),
    THE_TEMPLE_OF_ARTEMIS(listOf(), Cost(resources = mapOf(Resource.PAPYRUS to 1, Resource.GLASS to 1, Resource.STONE to 1, Resource.WOOD to 1)))
}

enum class Building(val deck: BuildingDeck = GUILDS, val type: BuildingType = BuildingType.GUILD, val effects: List<Effect>, val cost: Cost = Cost(), val freeLink: Building? = null) {
    LUMBER_YARD(AGE_I, BuildingType.RAW_MATERIAL, listOf(Production(Resource.WOOD))),
    LOGGING_CAMP(AGE_I, BuildingType.RAW_MATERIAL, listOf(Production(Resource.WOOD)), Cost(coins = 1)),
    CLAY_POOL(AGE_I, BuildingType.RAW_MATERIAL, listOf(Production(Resource.CLAY))),
    CLAY_PIT(AGE_I, BuildingType.RAW_MATERIAL, listOf(Production(Resource.CLAY)), Cost(coins = 1)),
    QUARRY(AGE_I, BuildingType.RAW_MATERIAL, listOf(Production(Resource.STONE))),
    STONE_PIT(AGE_I, BuildingType.RAW_MATERIAL, listOf(Production(Resource.STONE)), Cost(coins = 1)),
    GLASSWORKS(AGE_I, BuildingType.MANUFACTURE, listOf(Production(Resource.GLASS)), Cost(coins = 1)),
    PRESS(AGE_I, BuildingType.MANUFACTURE, listOf(Production(Resource.PAPYRUS)), Cost(coins = 1)),
    GUARD_TOWER(AGE_I, BuildingType.MILITARY, listOf(Shield(1))),
    STABLE(AGE_I, BuildingType.MILITARY, listOf(Shield(1)), Cost(resources = mapOf(Resource.WOOD to 1))),
    GARRISON(AGE_I, BuildingType.MILITARY, listOf(Shield(1)), Cost(resources = mapOf(Resource.CLAY to 1))),
    PALISADE(AGE_I, BuildingType.MILITARY, listOf(Shield(1)), Cost(coins = 2)),
    WORKSHOP(AGE_I, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.PENDULUM, VictoryPoints(1)), Cost(resources = mapOf(Resource.PAPYRUS to 1))),
    APOTHECARY(AGE_I, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.WHEEL, VictoryPoints(1)), Cost(resources = mapOf(Resource.GLASS to 1))),
    SCRIPTORIUM(AGE_I, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.INKWELL), Cost(coins = 2)),
    PHARMACIST(AGE_I, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.MORTAR), Cost(coins = 2)),
    THEATER(AGE_I, BuildingType.CIVILIAN, listOf(VictoryPoints(3))),
    ALTAR(AGE_I, BuildingType.CIVILIAN, listOf(VictoryPoints(3))),
    BATHS(AGE_I, BuildingType.CIVILIAN, listOf(VictoryPoints(3)), Cost(resources = mapOf(Resource.STONE to 1))),
    STONE_RESERVE(AGE_I, BuildingType.COMMERCIAL, listOf(FixTradingCostTo1(Resource.STONE)), Cost(coins = 3)),
    CLAY_RESERVE(AGE_I, BuildingType.COMMERCIAL, listOf(FixTradingCostTo1(Resource.CLAY)), Cost(coins = 3)),
    WOOD_RESERVE(AGE_I, BuildingType.COMMERCIAL, listOf(FixTradingCostTo1(Resource.WOOD)), Cost(coins = 3)),
    TAVERN(AGE_I, BuildingType.COMMERCIAL, listOf()),
    SAWMILL(AGE_II, BuildingType.RAW_MATERIAL, listOf(Production(Resource.WOOD, 2)), Cost(coins = 2)),
    BRICKYARD(AGE_II, BuildingType.RAW_MATERIAL, listOf(Production(Resource.CLAY, 2)), Cost(coins = 2)),
    SHELF_QUARRY(AGE_II, BuildingType.RAW_MATERIAL, listOf(Production(Resource.STONE, 2)), Cost(coins = 2)),
    GLASSBLOWER(AGE_II, BuildingType.MANUFACTURE, listOf(Production(Resource.GLASS))),
    DRYING_ROOM(AGE_II, BuildingType.MANUFACTURE, listOf(Production(Resource.PAPYRUS))),
    WALLS(AGE_II, BuildingType.MILITARY, listOf(Shield(2)), Cost(resources = mapOf(Resource.STONE to 2))),
    HORSE_BREEDERS(AGE_II, BuildingType.MILITARY, listOf(Shield(1)), Cost(resources = mapOf(Resource.CLAY to 1, Resource.WOOD to 1)), freeLink = STABLE),
    BARRACKS(AGE_II, BuildingType.MILITARY, listOf(Shield(1)), Cost(coins = 3), freeLink = GARRISON),
    ARCHERY_RANGE(AGE_II, BuildingType.MILITARY, listOf(Shield(2)), Cost(resources = mapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.PAPYRUS to 1))),
    PARADE_GROUND(AGE_II, BuildingType.MILITARY, listOf(Shield(2)), Cost(resources = mapOf(Resource.CLAY to 2, Resource.GLASS to 1))),
    LIBRARY(AGE_II, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.INKWELL, VictoryPoints(2)), Cost(resources = mapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 1)), freeLink = SCRIPTORIUM),
    DISPENSARY(AGE_II, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.MORTAR, VictoryPoints(2)), Cost(resources = mapOf(Resource.CLAY to 2, Resource.STONE to 1)), freeLink = PHARMACIST),
    SCHOOL(AGE_II, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.WHEEL, VictoryPoints(1)), Cost(resources = mapOf(Resource.WOOD to 1, Resource.PAPYRUS to 2))),
    LABORATORY(AGE_II, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.PENDULUM, VictoryPoints(1)), Cost(resources = mapOf(Resource.WOOD to 1, Resource.GLASS to 2))),
    TRIBUNAL(AGE_II, BuildingType.CIVILIAN, listOf(VictoryPoints(5)), Cost(resources = mapOf(Resource.WOOD to 2, Resource.GLASS to 1))),
    STATUE(AGE_II, BuildingType.CIVILIAN, listOf(VictoryPoints(4)), Cost(resources = mapOf(Resource.CLAY to 2)), freeLink = THEATER),
    TEMPLE(AGE_II, BuildingType.CIVILIAN, listOf(VictoryPoints(4)), Cost(resources = mapOf(Resource.WOOD to 1, Resource.PAPYRUS to 1)), freeLink = ALTAR),
    AQUEDUCT(AGE_II, BuildingType.CIVILIAN, listOf(VictoryPoints(5)), Cost(resources = mapOf(Resource.STONE to 3)), freeLink = BATHS),
    ROSTRUM(AGE_II, BuildingType.CIVILIAN, listOf(VictoryPoints(4)), Cost(resources = mapOf(Resource.STONE to 1, Resource.WOOD to 1))),
    FORUM(AGE_II, BuildingType.COMMERCIAL, listOf(ProductionOfAny(MANUFACTURED_GOOD)), Cost(coins = 3, resources = mapOf(Resource.CLAY to 1))),
    CARAVANSERY(AGE_II, BuildingType.COMMERCIAL, listOf(ProductionOfAny(RAW_GOOD)), Cost(coins = 2, resources = mapOf(Resource.GLASS to 1, Resource.PAPYRUS to 1))),
    CUSTOMS_HOUSE(AGE_II, BuildingType.COMMERCIAL, listOf(FixTradingCostTo1(Resource.PAPYRUS), FixTradingCostTo1(Resource.GLASS)), Cost(coins = 4)),
    BREWERY(AGE_II, BuildingType.COMMERCIAL, listOf()),
    ARSENAL(AGE_III, BuildingType.MILITARY, listOf(Shield(3)), Cost(resources = mapOf(Resource.CLAY to 3, Resource.WOOD to 2))),
    COURTHOUSE(AGE_III, BuildingType.MILITARY, listOf(Shield(3)), Cost(coins = 8)),
    FORTIFICATIONS(AGE_III, BuildingType.MILITARY, listOf(Shield(2)), Cost(resources = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.PAPYRUS to 1)), freeLink = PALISADE),
    SIEGE_WORKSHOP(AGE_III, BuildingType.MILITARY, listOf(Shield(2)), Cost(resources = mapOf(Resource.WOOD to 3, Resource.GLASS to 1)), freeLink = ARCHERY_RANGE),
    CIRCUS(AGE_III, BuildingType.MILITARY, listOf(Shield(2)), Cost(resources = mapOf(Resource.CLAY to 2, Resource.STONE to 2)), freeLink = PARADE_GROUND),
    ACADEMY(AGE_III, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.SUNDIAL, VictoryPoints(3)), Cost(resources = mapOf(Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 2))),
    STUDY(AGE_III, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.SUNDIAL, VictoryPoints(3)), Cost(resources = mapOf(Resource.WOOD to 2, Resource.GLASS to 1, Resource.PAPYRUS to 1))),
    UNIVERSITY(AGE_III, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.GYROSCOPE, VictoryPoints(2)), Cost(resources = mapOf(Resource.CLAY to 1, Resource.GLASS to 1, Resource.PAPYRUS to 1)), freeLink = SCHOOL),
    OBSERVATORY(AGE_III, BuildingType.SCIENTIFIC, listOf(ScientificSymbol.GYROSCOPE, VictoryPoints(2)), Cost(resources = mapOf(Resource.STONE to 1, Resource.PAPYRUS to 2)), freeLink = LABORATORY),
    PALACE(AGE_III, BuildingType.CIVILIAN, listOf(VictoryPoints(7)), Cost(resources = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.WOOD to 1, Resource.GLASS to 2))),
    TOWN_HALL(AGE_III, BuildingType.CIVILIAN, listOf(VictoryPoints(7)), Cost(resources = mapOf(Resource.STONE to 3, Resource.WOOD to 2))),
    OBELISK(AGE_III, BuildingType.CIVILIAN, listOf(VictoryPoints(5)), Cost(resources = mapOf(Resource.STONE to 2, Resource.GLASS to 1))),
    GARDENS(AGE_III, BuildingType.CIVILIAN, listOf(VictoryPoints(6)), Cost(resources = mapOf(Resource.CLAY to 2, Resource.WOOD to 2)), freeLink = STATUE),
    PANTHEON(AGE_III, BuildingType.CIVILIAN, listOf(VictoryPoints(6)), Cost(resources = mapOf(Resource.CLAY to 1, Resource.WOOD to 1, Resource.PAPYRUS to 2)), freeLink = TEMPLE),
    SENATE(AGE_III, BuildingType.CIVILIAN, listOf(VictoryPoints(5)), Cost(resources = mapOf(Resource.CLAY to 2, Resource.STONE to 1, Resource.PAPYRUS to 1)), freeLink = ROSTRUM),
    CHAMBER_OF_COMMERCE(AGE_III, BuildingType.COMMERCIAL, listOf(VictoryPoints(3)), Cost(resources = mapOf(Resource.PAPYRUS to 2))),
    PORT(AGE_III, BuildingType.COMMERCIAL, listOf(VictoryPoints(3)), Cost(resources = mapOf(Resource.WOOD to 1, Resource.GLASS to 1, Resource.PAPYRUS to 1))),
    ARMORY(AGE_III, BuildingType.COMMERCIAL, listOf(VictoryPoints(3)), Cost(resources = mapOf(Resource.STONE to 2, Resource.GLASS to 1))),
    LIGHTHOUSE(AGE_III, BuildingType.COMMERCIAL, listOf(VictoryPoints(3)), Cost(resources = mapOf(Resource.CLAY to 2, Resource.GLASS to 1)), freeLink = TAVERN),
    ARENA(AGE_III, BuildingType.COMMERCIAL, listOf(VictoryPoints(3)), Cost(resources = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.WOOD to 1)), freeLink = BREWERY),
    MERCHANTS_GUILD(effects = listOf(VictoryPointsForMajority(BuildingType.COMMERCIAL)), cost = Cost(resources = mapOf(Resource.CLAY to 1, Resource.WOOD to 1, Resource.GLASS to 1, Resource.PAPYRUS to 1))),
    SHIPOWNERS_GUILD(effects = listOf(VictoryPointsForMajority(BuildingType.RAW_MATERIAL, BuildingType.MANUFACTURE)), cost = Cost(resources = mapOf(Resource.CLAY to 1, Resource.STONE to 1, Resource.GLASS to 1, Resource.PAPYRUS to 1))),
    BUILDERS_GUILD(effects = listOf(VictoryPointsForMajority { player -> player.wonders.count { it.isBuild() } }), cost = Cost(resources = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.WOOD to 1, Resource.GLASS to 1))),
    MAGISTRATE_GUILD(effects = listOf(VictoryPointsForMajority(BuildingType.CIVILIAN)), cost = Cost(resources = mapOf(Resource.WOOD to 2, Resource.CLAY to 1, Resource.PAPYRUS to 1))),
    SCIENTISTS_GUILD(effects = listOf(VictoryPointsForMajority(BuildingType.SCIENTIFIC)), cost = Cost(resources = mapOf(Resource.CLAY to 2, Resource.WOOD to 2))),
    MONEYLENDERS_GUILD(effects = listOf(VictoryPointsForMajority { it.coins / 3 }), cost = Cost(resources = mapOf(Resource.STONE to 2, Resource.WOOD to 2))),
    TACTICIANS_GUILD(effects = listOf(VictoryPointsForMajority(BuildingType.MILITARY)), cost = Cost(resources = mapOf(Resource.STONE to 2, Resource.CLAY to 1, Resource.PAPYRUS to 1)))
}

enum class ProgressToken(val effects: List<Effect>) {
    AGRICULTURE(listOf(VictoryPoints(4))),
    ARCHITECTURE(listOf()),
    ECONOMY(listOf()),
    LAW(listOf(ScientificSymbol.BALANCE)),
    MASONRY(listOf()),
    MATHEMATICS(listOf(VictoryPointsForPlayer { it.progressTokens.size * 3 })),
    PHILOSOPHY(listOf(VictoryPoints(7))),
    STRATEGY(listOf()),
    THEOLOGY(listOf()),
    URBANISM(listOf())
}

interface BuildingDeck {
    fun age(): Age
}

object GUILDS : BuildingDeck {
    override fun age(): Age {
        return AGE_III
    }
}

enum class Age : BuildingDeck {
    AGE_I, AGE_II, AGE_III;

    override fun age(): Age {
        return this
    }
}

enum class BuildingType {
    RAW_MATERIAL, MANUFACTURE, CIVILIAN, SCIENTIFIC, COMMERCIAL, MILITARY, GUILD
}

data class Cost(val coins: Int = 0, val resources: Map<Resource, Int> = emptyMap())
enum class Resource(val type: Type) {
    CLAY(RAW_GOOD), WOOD(RAW_GOOD), STONE(RAW_GOOD), GLASS(MANUFACTURED_GOOD), PAPYRUS(MANUFACTURED_GOOD);

    enum class Type {
        RAW_GOOD, MANUFACTURED_GOOD
    }
}