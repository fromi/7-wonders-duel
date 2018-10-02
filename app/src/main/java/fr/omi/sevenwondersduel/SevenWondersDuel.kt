package fr.omi.sevenwondersduel

import fr.omi.sevenwondersduel.BuildingDeck.*
import fr.omi.sevenwondersduel.BuildingType.*
import fr.omi.sevenwondersduel.BuildingType.GUILD
import fr.omi.sevenwondersduel.Resource.*
import fr.omi.sevenwondersduel.Resource.Type.MANUFACTURED_GOOD
import fr.omi.sevenwondersduel.Resource.Type.RAW_GOOD

data class SevenWondersDuel(val players: List<Player> = listOf(Player(number = 1), Player(number = 2)),
                            val conflictPawnPosition: Int = 0,
                            val progressTokensAvailable: Set<ProgressToken> = ProgressToken.values().toList().shuffled().asSequence().take(5).toSet(),
                            val currentPlayerNumber: Int = 1,
                            val wondersAvailable: Set<Wonder> = Wonder.values().toList().shuffled().asSequence().take(4).toSet(),
                            val structure: List<Map<Int, Building>> = emptyList()) {

    private fun currentPlayer(): Player {
        return players.first { it.number == currentPlayerNumber }
    }

    fun choose(wonder: Wonder): SevenWondersDuel {
        require(wondersAvailable.contains(wonder))
        return copy(wondersAvailable = when {
            wondersAvailable.size > 2 -> wondersAvailable.minus(wonder)
            players.sumBy { it.wonders.size } < 4 -> remainingWonder().shuffled().asSequence().take(4).toSet()
            else -> emptySet()
        }, players = players.map { player ->
            when {
                player == currentPlayer() -> player.copy(wonders = player.wonders.plus(wonder))
                wondersAvailable.size > 2 -> player
                else -> player.copy(wonders = player.wonders.plus(wondersAvailable.first { it != wonder }))
            }
        }, currentPlayerNumber = if (players.sumBy { it.wonders.size } in 0..3) 2 else 1,
                structure = createStructure(AGE_I))
    }

    private fun remainingWonder() = Wonder.values().toList().filter { players.none { player -> player.wonders.contains(it) } && !wondersAvailable.contains(it) }

    fun build(building: Building): SevenWondersDuel {
        require(accessibleBuilding().contains(building))
        return copy(structure = structure.map { row -> row.minus(row.keys.filter { row[it] == building }) },
                players = players.map { if (it == currentPlayer()) it.copy(buildings = it.buildings.plus(building), coins = it.coins - it.cost(building, opponentOf(it))) else it },
                currentPlayerNumber = if (currentPlayerNumber == 1) 2 else 1)
    }

    private fun opponentOf(player: Player) = players.first { it != player }

    fun accessibleBuilding(): Collection<Building> {
        return structure.mapIndexed { index, row -> row.filterKeys { index == structure.size - 1 || !(structure[index + 1].contains(it - 1) || structure[index + 1].contains(it + 1)) }.values }.flatten()
    }
}

data class Player(val number: Int, val militaryTokensLooted: Int = 0, val coins: Int = 7, val wonders: Set<Wonder> = emptySet(), val buildings: Set<Building> = emptySet()) {
    fun cost(building: Building, opponent: Player): Int {
        return building.cost.coins + resourcesCost(building, opponent)
    }

    private fun resourcesCost(building: Building, opponent: Player): Int {
        return Resource.Type.values().sumBy { cost(it, building, opponent) }
    }

    private fun cost(resourceType: Resource.Type, building: Building, opponent: Player): Int {
        return building.cost.resources.filter { it.key.type == resourceType }
                .flatMap { entry -> List(entry.value - productionOf(entry.key)) { cost(entry.key, opponent) } }
                .sorted().dropLast(effects().count { it is ProductionOfAny && it.resourceType == resourceType })
                .sum()
    }

    private fun cost(resource: Resource, opponent: Player): Int {
        return if (effects().any { it is FixTradingCostTo1 && it.resource == resource }) 1 else 2 + opponent.productionOf(resource)
    }

    private fun productionOf(resource: Resource): Int {
        return effects().asSequence().filterIsInstance<Production>().filter { it.resource == resource }.sumBy { it.quantity }
    }

    private fun effects() = buildings.flatMap { it.effects.toList() }
}

fun createStructure(age: BuildingDeck): List<Map<Int, Building>> {
    val deck = when (age) {
        AGE_I, AGE_II -> Building.values().filter { it.deck == age }.shuffled()
        else -> {
            val threeRandomGuilds = Building.values().filter { it.deck == BuildingDeck.GUILD }.shuffled().take(3)
            val age3BuildingsBut3 = Building.values().filter { it.deck == AGE_III }.shuffled().drop(3)
            age3BuildingsBut3.plus(threeRandomGuilds).shuffled()
        }
    }
    return createStructure(age, deck)
}

fun createStructure(age: BuildingDeck, buildings: List<Building>): List<Map<Int, Building>> {
    val deck = buildings.toMutableList()
    val structureDescription = when (age) {
        AGE_I -> listOf(listOf(-1, 1), listOf(-2, 0, 2), listOf(-3, -1, 1, 3), listOf(-4, -2, 0, 2, 4), listOf(-5, -3, -1, 1, 3, 5))
        AGE_II -> listOf(listOf(-5, -3, -1, 1, 3, 5), listOf(-4, -2, 0, 2, 4), listOf(-3, -1, 1, 3), listOf(-2, 0, 2), listOf(-1, 1))
        else -> listOf(listOf(-1, 1), listOf(-2, 0, 2), listOf(-3, -1, 1, 3), listOf(-2, 2), listOf(-3, -1, 1, 3), listOf(-2, 0, 2), listOf(-1, 1))
    }
    return structureDescription.map { positions -> positions.associate { it to deck.removeAt(0) } }
}

enum class ProgressToken {
    AGRICULTURE, ARCHITECTURE, ECONOMY, LAW, MASONRY, MATHEMATICS, PHILOSOPHY, STRATEGY, THEOLOGY, URBANISM
}

enum class Wonder {
    THE_APPIAN_WAY, CIRCUS_MAXIMUS, THE_COLOSSUS, THE_GREAT_LIBRARY, THE_GREAT_LIGHTHOUSE, THE_HANGING_GARDENS,
    THE_MAUSOLEUM, PIRAEUS, THE_PYRAMIDS, THE_SPHINX, THE_STATUE_OF_ZEUS, THE_TEMPLE_OF_ARTEMIS
}

enum class Building(val deck: BuildingDeck = BuildingDeck.GUILD, val type: BuildingType = GUILD, val effects: Set<Effect> = emptySet(), val cost: Cost = Cost()) {
    LUMBER_YARD(AGE_I, RAW_MATERIAL, setOf(Production(WOOD))),
    LOGGING_CAMP(AGE_I, RAW_MATERIAL, setOf(Production(WOOD)), Cost(coins = 1)),
    CLAY_POOL(AGE_I, RAW_MATERIAL, setOf(Production(CLAY))),
    CLAY_PIT(AGE_I, RAW_MATERIAL, setOf(Production(CLAY)), Cost(coins = 1)),
    QUARRY(AGE_I, RAW_MATERIAL, setOf(Production(STONE))),
    STONE_PIT(AGE_I, RAW_MATERIAL, setOf(Production(STONE)), Cost(coins = 1)),
    GLASSWORKS(AGE_I, BuildingType.MANUFACTURED_GOOD, setOf(Production(GLASS)), Cost(coins = 1)),
    PRESS(AGE_I, BuildingType.MANUFACTURED_GOOD, setOf(Production(PAPYRUS)), Cost(coins = 1)),
    GUARD_TOWER(AGE_I, MILITARY, setOf()),
    STABLE(AGE_I, MILITARY, setOf(), Cost(resources = mapOf(WOOD to 1))),
    GARRISON(AGE_I, MILITARY, setOf(), Cost(resources = mapOf(CLAY to 1))),
    PALISADE(AGE_I, MILITARY, setOf(), Cost(coins = 2)),
    WORKSHOP(AGE_I, SCIENTIFIC, setOf(), Cost(resources = mapOf(PAPYRUS to 1))),
    APOTHECARY(AGE_I, SCIENTIFIC, setOf(), Cost(resources = mapOf(GLASS to 1))),
    SCRIPTORIUM(AGE_I, SCIENTIFIC, setOf(), Cost(coins = 2)),
    PHARMACIST(AGE_I, SCIENTIFIC, setOf(), Cost(coins = 2)),
    THEATER(AGE_I, CIVILIAN, setOf()),
    ALTAR(AGE_I, CIVILIAN, setOf()),
    BATHS(AGE_I, CIVILIAN, setOf(), Cost(resources = mapOf(STONE to 1))),
    STONE_RESERVE(AGE_I, COMMERCIAL, setOf(FixTradingCostTo1(STONE)), Cost(coins = 3)),
    CLAY_RESERVE(AGE_I, COMMERCIAL, setOf(FixTradingCostTo1(CLAY)), Cost(coins = 3)),
    WOOD_RESERVE(AGE_I, COMMERCIAL, setOf(FixTradingCostTo1(WOOD)), Cost(coins = 3)),
    TAVERN(AGE_I, COMMERCIAL, setOf()),
    SAWMILL(AGE_II, RAW_MATERIAL, setOf(Production(WOOD, 2)), Cost(coins = 2)),
    BRICKYARD(AGE_II, RAW_MATERIAL, setOf(Production(CLAY, 2)), Cost(coins = 2)),
    SHELF_QUARRY(AGE_II, RAW_MATERIAL, setOf(Production(STONE, 2)), Cost(coins = 2)),
    GLASSBLOWER(AGE_II, BuildingType.MANUFACTURED_GOOD, setOf(Production(GLASS))),
    DRYING_ROOM(AGE_II, BuildingType.MANUFACTURED_GOOD, setOf(Production(PAPYRUS))),
    WALLS(AGE_II, MILITARY, setOf(), Cost(resources = mapOf(STONE to 2))),
    HORSE_BREEDERS(AGE_II, MILITARY, setOf(), Cost(resources = mapOf(CLAY to 1, WOOD to 1))),
    BARRACKS(AGE_II, MILITARY, setOf(), Cost(coins = 3)),
    ARCHERY_RANGE(AGE_II, MILITARY, setOf(), Cost(resources = mapOf(STONE to 1, WOOD to 1, PAPYRUS to 1))),
    PARADE_GROUND(AGE_II, MILITARY, setOf(), Cost(resources = mapOf(CLAY to 2, GLASS to 1))),
    LIBRARY(AGE_II, SCIENTIFIC, setOf(), Cost(resources = mapOf(STONE to 1, WOOD to 1, GLASS to 1))),
    DISPENSARY(AGE_II, SCIENTIFIC, setOf(), Cost(resources = mapOf(CLAY to 2, STONE to 1))),
    SCHOOL(AGE_II, SCIENTIFIC, setOf(), Cost(resources = mapOf(WOOD to 1, PAPYRUS to 2))),
    LABORATORY(AGE_II, SCIENTIFIC, setOf(), Cost(resources = mapOf(WOOD to 1, GLASS to 2))),
    TRIBUNAL(AGE_II, CIVILIAN, setOf(), Cost(resources = mapOf(WOOD to 2, GLASS to 1))),
    STATUE(AGE_II, CIVILIAN, setOf(), Cost(resources = mapOf(CLAY to 2))),
    TEMPLE(AGE_II, CIVILIAN, setOf(), Cost(resources = mapOf(WOOD to 1, PAPYRUS to 1))),
    AQUEDUCT(AGE_II, CIVILIAN, setOf(), Cost(resources = mapOf(STONE to 3))),
    ROSTRUM(AGE_II, CIVILIAN, setOf(), Cost(resources = mapOf(STONE to 1, WOOD to 1))),
    FORUM(AGE_II, COMMERCIAL, setOf(ProductionOfAny(MANUFACTURED_GOOD)), Cost(coins = 3, resources = mapOf(CLAY to 1))),
    CARAVANSERY(AGE_II, COMMERCIAL, setOf(ProductionOfAny(RAW_GOOD)), Cost(coins = 2, resources = mapOf(GLASS to 1, PAPYRUS to 1))),
    CUSTOMS_HOUSE(AGE_II, COMMERCIAL, setOf(FixTradingCostTo1(PAPYRUS), FixTradingCostTo1(GLASS)), Cost(coins = 4)),
    BREWERY(AGE_II, COMMERCIAL, setOf()),
    ARSENAL(AGE_III, MILITARY, setOf(), Cost(resources = mapOf(CLAY to 3, WOOD to 2))),
    COURTHOUSE(AGE_III, MILITARY, setOf(), Cost(coins = 8)),
    FORTIFICATIONS(AGE_III, MILITARY, setOf(), Cost(resources = mapOf(STONE to 2, CLAY to 1, PAPYRUS to 1))),
    SIEGE_WORKSHOP(AGE_III, MILITARY, setOf(), Cost(resources = mapOf(WOOD to 3, GLASS to 1))),
    CIRCUS(AGE_III, MILITARY, setOf(), Cost(resources = mapOf(CLAY to 2, STONE to 2))),
    ACADEMY(AGE_III, SCIENTIFIC, setOf(), Cost(resources = mapOf(STONE to 1, WOOD to 1, GLASS to 2))),
    STUDY(AGE_III, SCIENTIFIC, setOf(), Cost(resources = mapOf(WOOD to 2, GLASS to 1, PAPYRUS to 1))),
    UNIVERSITY(AGE_III, SCIENTIFIC, setOf(), Cost(resources = mapOf(CLAY to 1, GLASS to 1, PAPYRUS to 1))),
    OBSERVATORY(AGE_III, SCIENTIFIC, setOf(), Cost(resources = mapOf(STONE to 1, PAPYRUS to 2))),
    PALACE(AGE_III, CIVILIAN, setOf(), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 1, GLASS to 2))),
    TOWN_HALL(AGE_III, CIVILIAN, setOf(), Cost(resources = mapOf(STONE to 3, WOOD to 2))),
    OBELISK(AGE_III, CIVILIAN, setOf(), Cost(resources = mapOf(STONE to 2, GLASS to 1))),
    GARDENS(AGE_III, CIVILIAN, setOf(), Cost(resources = mapOf(CLAY to 2, WOOD to 2))),
    PANTHEON(AGE_III, CIVILIAN, setOf(), Cost(resources = mapOf(CLAY to 1, WOOD to 1, PAPYRUS to 2))),
    SENATE(AGE_III, CIVILIAN, setOf(), Cost(resources = mapOf(CLAY to 2, STONE to 1, PAPYRUS to 1))),
    CHAMBER_OF_COMMERCE(AGE_III, COMMERCIAL, setOf(), Cost(resources = mapOf(PAPYRUS to 2))),
    PORT(AGE_III, COMMERCIAL, setOf(), Cost(resources = mapOf(WOOD to 1, GLASS to 1, PAPYRUS to 1))),
    ARMORY(AGE_III, COMMERCIAL, setOf(), Cost(resources = mapOf(STONE to 2, GLASS to 1))),
    LIGHTHOUSE(AGE_III, COMMERCIAL, setOf(), Cost(resources = mapOf(CLAY to 2, GLASS to 1))),
    ARENA(AGE_III, COMMERCIAL, setOf(), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 1))),
    MERCHANTS_GUILD(cost = Cost(resources = mapOf(CLAY to 1, WOOD to 1, GLASS to 1, PAPYRUS to 1))),
    SHIPOWNERS_GUILD(cost = Cost(resources = mapOf(CLAY to 1, STONE to 1, GLASS to 1, PAPYRUS to 1))),
    BUILDERS_GUILD(cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, WOOD to 1, GLASS to 1))),
    MAGISTRATE_GUILD(cost = Cost(resources = mapOf(WOOD to 2, CLAY to 1, PAPYRUS to 1))),
    SCIENTISTS_GUILD(cost = Cost(resources = mapOf(CLAY to 2, WOOD to 2))),
    MONEYLENDERS_GUILD(cost = Cost(resources = mapOf(STONE to 2, WOOD to 2))),
    TACTICIANS_GUILD(cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, PAPYRUS to 1)))
}

enum class BuildingDeck {
    AGE_I, AGE_II, AGE_III, GUILD
}

enum class BuildingType {
    RAW_MATERIAL, MANUFACTURED_GOOD, CIVILIAN, SCIENTIFIC, COMMERCIAL, MILITARY, GUILD
}

data class Cost(val coins: Int = 0, val resources: Map<Resource, Int> = emptyMap())

enum class Resource(val type: Type) {
    CLAY(RAW_GOOD), WOOD(RAW_GOOD), STONE(RAW_GOOD), GLASS(MANUFACTURED_GOOD), PAPYRUS(MANUFACTURED_GOOD);

    enum class Type {
        RAW_GOOD, MANUFACTURED_GOOD
    }
}

interface Effect

data class Production(val resource: Resource, val quantity: Int = 1) : Effect
data class FixTradingCostTo1(val resource: Resource) : Effect
data class ProductionOfAny(val resourceType: Type) : Effect