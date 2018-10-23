package fr.omi.sevenwondersduel.material

import fr.omi.sevenwondersduel.effects.*
import fr.omi.sevenwondersduel.effects.ScientificSymbol.*
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoins
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoinsForMajorityBuildings
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoinsForPlayer
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoinsForPlayerBuildings
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPoints
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPointsForMajority
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPointsForMajorityBuildings
import fr.omi.sevenwondersduel.material.Age.*
import fr.omi.sevenwondersduel.material.Building.Deck.Guilds
import fr.omi.sevenwondersduel.material.Building.Type.*
import fr.omi.sevenwondersduel.material.Construction.Cost
import fr.omi.sevenwondersduel.material.Resource.*
import fr.omi.sevenwondersduel.material.Resource.Type.MANUFACTURED_GOOD
import fr.omi.sevenwondersduel.material.Resource.Type.RAW_GOOD

enum class Building(val deck: Deck = Guilds, val type: Type = GUILD, override val effects: List<Effect>, override val cost: Cost = Cost(), val freeLink: Building? = null) : Construction {
    LUMBER_YARD(AGE_I, RAW_MATERIAL, listOf(Production(WOOD))),
    LOGGING_CAMP(AGE_I, RAW_MATERIAL, listOf(Production(WOOD)), Cost(coins = 1)),
    CLAY_POOL(AGE_I, RAW_MATERIAL, listOf(Production(CLAY))),
    CLAY_PIT(AGE_I, RAW_MATERIAL, listOf(Production(CLAY)), Cost(coins = 1)),
    QUARRY(AGE_I, RAW_MATERIAL, listOf(Production(STONE))),
    STONE_PIT(AGE_I, RAW_MATERIAL, listOf(Production(STONE)), Cost(coins = 1)),
    GLASSWORKS(AGE_I, MANUFACTURE, listOf(Production(GLASS)), Cost(coins = 1)),
    PRESS(AGE_I, MANUFACTURE, listOf(Production(PAPYRUS)), Cost(coins = 1)),
    GUARD_TOWER(AGE_I, MILITARY, listOf(Shield())),
    STABLE(AGE_I, MILITARY, listOf(Shield()), Cost(resources = mapOf(WOOD to 1))),
    GARRISON(AGE_I, MILITARY, listOf(Shield()), Cost(resources = mapOf(CLAY to 1))),
    PALISADE(AGE_I, MILITARY, listOf(Shield()), Cost(coins = 2)),
    WORKSHOP(AGE_I, SCIENTIFIC, listOf(PENDULUM, VictoryPoints(1)), Cost(resources = mapOf(PAPYRUS to 1))),
    APOTHECARY(AGE_I, SCIENTIFIC, listOf(WHEEL, VictoryPoints(1)), Cost(resources = mapOf(GLASS to 1))),
    SCRIPTORIUM(AGE_I, SCIENTIFIC, listOf(INKWELL), Cost(coins = 2)),
    PHARMACIST(AGE_I, SCIENTIFIC, listOf(MORTAR), Cost(coins = 2)),
    THEATER(AGE_I, CIVILIAN, listOf(VictoryPoints(3))),
    ALTAR(AGE_I, CIVILIAN, listOf(VictoryPoints(3))),
    BATHS(AGE_I, CIVILIAN, listOf(VictoryPoints(3)), Cost(resources = mapOf(STONE to 1))),
    STONE_RESERVE(AGE_I, COMMERCIAL, listOf(FixTradingCostTo1(STONE)), Cost(coins = 3)),
    CLAY_RESERVE(AGE_I, COMMERCIAL, listOf(FixTradingCostTo1(CLAY)), Cost(coins = 3)),
    WOOD_RESERVE(AGE_I, COMMERCIAL, listOf(FixTradingCostTo1(WOOD)), Cost(coins = 3)),
    TAVERN(AGE_I, COMMERCIAL, listOf(TakeCoins(4))),
    SAWMILL(AGE_II, RAW_MATERIAL, listOf(Production(WOOD, 2)), Cost(coins = 2)),
    BRICKYARD(AGE_II, RAW_MATERIAL, listOf(Production(CLAY, 2)), Cost(coins = 2)),
    SHELF_QUARRY(AGE_II, RAW_MATERIAL, listOf(Production(STONE, 2)), Cost(coins = 2)),
    GLASSBLOWER(AGE_II, MANUFACTURE, listOf(Production(GLASS))),
    DRYING_ROOM(AGE_II, MANUFACTURE, listOf(Production(PAPYRUS))),
    WALLS(AGE_II, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(STONE to 2))),
    HORSE_BREEDERS(AGE_II, MILITARY, listOf(Shield()), Cost(resources = mapOf(CLAY to 1, WOOD to 1)), freeLink = STABLE),
    BARRACKS(AGE_II, MILITARY, listOf(Shield()), Cost(coins = 3), freeLink = GARRISON),
    ARCHERY_RANGE(AGE_II, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(STONE to 1, WOOD to 1, PAPYRUS to 1))),
    PARADE_GROUND(AGE_II, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(CLAY to 2, GLASS to 1))),
    LIBRARY(AGE_II, SCIENTIFIC, listOf(INKWELL, VictoryPoints(2)), Cost(resources = mapOf(STONE to 1, WOOD to 1, GLASS to 1)), freeLink = SCRIPTORIUM),
    DISPENSARY(AGE_II, SCIENTIFIC, listOf(MORTAR, VictoryPoints(2)), Cost(resources = mapOf(CLAY to 2, STONE to 1)), freeLink = PHARMACIST),
    SCHOOL(AGE_II, SCIENTIFIC, listOf(WHEEL, VictoryPoints(1)), Cost(resources = mapOf(WOOD to 1, PAPYRUS to 2))),
    LABORATORY(AGE_II, SCIENTIFIC, listOf(PENDULUM, VictoryPoints(1)), Cost(resources = mapOf(WOOD to 1, GLASS to 2))),
    TRIBUNAL(AGE_II, CIVILIAN, listOf(VictoryPoints(5)), Cost(resources = mapOf(WOOD to 2, GLASS to 1))),
    STATUE(AGE_II, CIVILIAN, listOf(VictoryPoints(4)), Cost(resources = mapOf(CLAY to 2)), freeLink = THEATER),
    TEMPLE(AGE_II, CIVILIAN, listOf(VictoryPoints(4)), Cost(resources = mapOf(WOOD to 1, PAPYRUS to 1)), freeLink = ALTAR),
    AQUEDUCT(AGE_II, CIVILIAN, listOf(VictoryPoints(5)), Cost(resources = mapOf(STONE to 3)), freeLink = BATHS),
    ROSTRUM(AGE_II, CIVILIAN, listOf(VictoryPoints(4)), Cost(resources = mapOf(STONE to 1, WOOD to 1))),
    FORUM(AGE_II, COMMERCIAL, listOf(ProductionOfAny(MANUFACTURED_GOOD)), Cost(coins = 3, resources = mapOf(CLAY to 1))),
    CARAVANSERY(AGE_II, COMMERCIAL, listOf(ProductionOfAny(RAW_GOOD)), Cost(coins = 2, resources = mapOf(GLASS to 1, PAPYRUS to 1))),
    CUSTOMS_HOUSE(AGE_II, COMMERCIAL, listOf(FixTradingCostTo1(PAPYRUS), FixTradingCostTo1(GLASS)), Cost(coins = 4)),
    BREWERY(AGE_II, COMMERCIAL, listOf(TakeCoins(6))),
    ARSENAL(AGE_III, MILITARY, listOf(Shield(3)), Cost(resources = mapOf(CLAY to 3, WOOD to 2))),
    COURTHOUSE(AGE_III, MILITARY, listOf(Shield(3)), Cost(coins = 8)),
    FORTIFICATIONS(AGE_III, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(STONE to 2, CLAY to 1, PAPYRUS to 1)), freeLink = PALISADE),
    SIEGE_WORKSHOP(AGE_III, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(WOOD to 3, GLASS to 1)), freeLink = ARCHERY_RANGE),
    CIRCUS(AGE_III, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(CLAY to 2, STONE to 2)), freeLink = PARADE_GROUND),
    ACADEMY(AGE_III, SCIENTIFIC, listOf(SUNDIAL, VictoryPoints(3)), Cost(resources = mapOf(STONE to 1, WOOD to 1, GLASS to 2))),
    STUDY(AGE_III, SCIENTIFIC, listOf(SUNDIAL, VictoryPoints(3)), Cost(resources = mapOf(WOOD to 2, GLASS to 1, PAPYRUS to 1))),
    UNIVERSITY(AGE_III, SCIENTIFIC, listOf(GYROSCOPE, VictoryPoints(2)), Cost(resources = mapOf(CLAY to 1, GLASS to 1, PAPYRUS to 1)), freeLink = SCHOOL),
    OBSERVATORY(AGE_III, SCIENTIFIC, listOf(GYROSCOPE, VictoryPoints(2)), Cost(resources = mapOf(STONE to 1, PAPYRUS to 2)), freeLink = LABORATORY),
    PALACE(AGE_III, CIVILIAN, listOf(VictoryPoints(7)), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 1, GLASS to 2))),
    TOWN_HALL(AGE_III, CIVILIAN, listOf(VictoryPoints(7)), Cost(resources = mapOf(STONE to 3, WOOD to 2))),
    OBELISK(AGE_III, CIVILIAN, listOf(VictoryPoints(5)), Cost(resources = mapOf(STONE to 2, GLASS to 1))),
    GARDENS(AGE_III, CIVILIAN, listOf(VictoryPoints(6)), Cost(resources = mapOf(CLAY to 2, WOOD to 2)), freeLink = STATUE),
    PANTHEON(AGE_III, CIVILIAN, listOf(VictoryPoints(6)), Cost(resources = mapOf(CLAY to 1, WOOD to 1, PAPYRUS to 2)), freeLink = TEMPLE),
    SENATE(AGE_III, CIVILIAN, listOf(VictoryPoints(5)), Cost(resources = mapOf(CLAY to 2, STONE to 1, PAPYRUS to 1)), freeLink = ROSTRUM),
    CHAMBER_OF_COMMERCE(AGE_III, COMMERCIAL, listOf(TakeCoinsForPlayerBuildings(MANUFACTURE, 3), VictoryPoints(3)), Cost(resources = mapOf(PAPYRUS to 2))),
    PORT(AGE_III, COMMERCIAL, listOf(TakeCoinsForPlayerBuildings(RAW_MATERIAL, 2), VictoryPoints(3)), Cost(resources = mapOf(WOOD to 1, GLASS to 1, PAPYRUS to 1))),
    ARMORY(AGE_III, COMMERCIAL, listOf(TakeCoinsForPlayerBuildings(MILITARY), VictoryPoints(3)), Cost(resources = mapOf(STONE to 2, GLASS to 1))),
    LIGHTHOUSE(AGE_III, COMMERCIAL, listOf(TakeCoinsForPlayerBuildings(COMMERCIAL), VictoryPoints(3)), Cost(resources = mapOf(CLAY to 2, GLASS to 1)), freeLink = TAVERN),
    ARENA(AGE_III, COMMERCIAL, listOf(TakeCoinsForPlayer { player -> 2 * player.wonders.count { it.isBuild() } }, VictoryPoints(3)), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 1)), freeLink = BREWERY),
    MERCHANTS_GUILD(Guilds, effects = listOf(TakeCoinsForMajorityBuildings(COMMERCIAL), VictoryPointsForMajorityBuildings(COMMERCIAL)), cost = Cost(resources = mapOf(CLAY to 1, WOOD to 1, GLASS to 1, PAPYRUS to 1))),
    SHIPOWNERS_GUILD(Guilds, effects = listOf(TakeCoinsForMajorityBuildings(RAW_MATERIAL, MANUFACTURE), VictoryPointsForMajorityBuildings(RAW_MATERIAL, MANUFACTURE)), cost = Cost(resources = mapOf(CLAY to 1, STONE to 1, GLASS to 1, PAPYRUS to 1))),
    BUILDERS_GUILD(Guilds, effects = listOf(VictoryPointsForMajority { player -> player.wonders.count { it.isBuild() } }), cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, WOOD to 1, GLASS to 1))),
    MAGISTRATE_GUILD(Guilds, effects = listOf(TakeCoinsForMajorityBuildings(CIVILIAN), VictoryPointsForMajorityBuildings(CIVILIAN)), cost = Cost(resources = mapOf(WOOD to 2, CLAY to 1, PAPYRUS to 1))),
    SCIENTISTS_GUILD(Guilds, effects = listOf(TakeCoinsForMajorityBuildings(SCIENTIFIC), VictoryPointsForMajorityBuildings(SCIENTIFIC)), cost = Cost(resources = mapOf(CLAY to 2, WOOD to 2))),
    MONEYLENDERS_GUILD(Guilds, effects = listOf(VictoryPointsForMajority { it.coins / 3 }), cost = Cost(resources = mapOf(STONE to 2, WOOD to 2))),
    TACTICIANS_GUILD(Guilds, effects = listOf(TakeCoinsForMajorityBuildings(MILITARY), VictoryPointsForMajorityBuildings(MILITARY)), cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, PAPYRUS to 1)));

    interface Deck {
        fun age(): Age

        object Guilds : Deck {
            override fun age(): Age {
                return AGE_III
            }
        }
    }

    enum class Type {
        RAW_MATERIAL, MANUFACTURE, CIVILIAN, SCIENTIFIC, COMMERCIAL, MILITARY, GUILD
    }
}