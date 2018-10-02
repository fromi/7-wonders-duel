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
                            val structure: List<Map<Int, Building>> = emptyList(),
                            val discardedCards: List<Building> = emptyList()) {

    fun choose(wonder: Wonder): SevenWondersDuel {
        require(wondersAvailable.contains(wonder)) { "This wonder is not available" }
        return copy(wondersAvailable = when {
            wondersAvailable.size > 2 -> wondersAvailable.minus(wonder)
            players.sumBy { it.wonders.size } < 4 -> remainingWonders().shuffled().asSequence().take(4).toSet()
            else -> emptySet()
        }, players = players.map { player ->
            when {
                player == currentPlayer() -> player.copy(wonders = player.wonders.plus(BuildableWonder(wonder)))
                wondersAvailable.size > 2 -> player
                else -> player.copy(wonders = player.wonders.plus(BuildableWonder(wondersAvailable.first { it != wonder })))
            }
        }, currentPlayerNumber = if (players.sumBy { it.wonders.size } in 0..3) 2 else 1,
                structure = createStructure(AGE_I))
    }

    fun build(building: Building): SevenWondersDuel {
        val player = currentPlayer()
        val opponent = opponentOf(player)
        return take(building)
                .copy(players = listOf(player.build(building, opponent), opponent).sortedBy { it.number })
                .applyEffects(building.effects)
                .continueGame()
    }

    fun discard(building: Building): SevenWondersDuel {
        val player = currentPlayer()
        val opponent = opponentOf(player)
        return take(building)
                .copy(players = listOf(player.discard(), opponent).sortedBy { it.number },
                        discardedCards = discardedCards.plus(building))
                .continueGame()
    }

    fun build(wonder: Wonder, buildingUsed: Building): SevenWondersDuel {
        val player = currentPlayer()
        val opponent = opponentOf(player)
        return take(buildingUsed)
                .copy(players = listOf(player.build(wonder, buildingUsed, opponent), opponent).sortedBy { it.number })
                .applyEffects(wonder.effects)
                .discardIf7WondersBuilt()
                .continueGame()
    }

    fun letOpponentBegin(): SevenWondersDuel {
        check(structure.sumBy { it.size } == 20) {"You can only let the opponent begin when starting Age II or Age III"}
        check(if (currentPlayerNumber == 1) conflictPawnPosition < 0 else conflictPawnPosition > 0) {"You can only let the opponent begin next Age when strictly weaker than him"}
        return copy(currentPlayerNumber = if (currentPlayerNumber == 1) 2 else 1)
    }

    private fun currentPlayer(): Player {
        return players.first { it.number == currentPlayerNumber }
    }

    private fun remainingWonders() = Wonder.values().toList().filter { wonder -> !wondersAvailable.contains(wonder) && players.none { player -> player.wonders.any { it.wonder == wonder } } }

    private fun opponentOf(player: Player) = players.first { it != player }

    fun accessibleBuildings(): Collection<Building> {
        return structure.mapIndexed { index, row -> row.filterKeys { index == structure.size - 1 || !(structure[index + 1].contains(it - 1) || structure[index + 1].contains(it + 1)) }.values }.flatten()
    }

    private fun take(building: Building): SevenWondersDuel {
        require(accessibleBuildings().contains(building)) { "This building cannot be taken" }
        return copy(structure = structure.map { row -> row.minus(row.keys.filter { row[it] == building }) })
    }

    private fun continueGame(): SevenWondersDuel {
        return if (structure.flatMap { it.values }.isEmpty()) {
            if (getCurrentAge() == 1) {
                prepareNextAge(AGE_II)
            } else {
                prepareNextAge(AGE_III)
            }
        }
        else copy(currentPlayerNumber = if (currentPlayerNumber == 1) 2 else 1)
    }

    private fun prepareNextAge(age: BuildingDeck): SevenWondersDuel {
        return copy(structure = createStructure(age), currentPlayerNumber = when {
            conflictPawnPosition < 0 -> 1
            conflictPawnPosition > 0 -> 2
            else -> currentPlayerNumber
        })
    }

    private fun getCurrentAge(): Int {
        val cardsInPlay = discardedCards.asSequence().plus(structure.flatMap { it.values }).plus(players.flatMap { it.buildings })
        return when {
            cardsInPlay.any { it.deck == AGE_III } -> 3
            cardsInPlay.any {it.deck == AGE_II} -> 2
            else -> 1
        }
    }

    private fun applyEffects(effects: List<Effect>): SevenWondersDuel {
        return if (effects.isEmpty()) this else effects.first().applyTo(this).applyEffects(effects.drop(1))
    }

    private fun discardIf7WondersBuilt(): SevenWondersDuel =
            if (players.sumBy { player -> player.wonders.count { it.isBuild() } } == 7)
                SevenWondersDuel(players = players.map { it.discardUnfinishedWonder() })
            else this

    fun moveConflictPawn(quantity: Int): SevenWondersDuel {
        return copy(conflictPawnPosition = if (currentPlayerNumber == 1) conflictPawnPosition + quantity else conflictPawnPosition - quantity)
                .lootMilitaryTokens()
    }

    private fun lootMilitaryTokens(): SevenWondersDuel {
        return when (conflictPawnPosition) {
            in -8..-6 -> if (players[0].militaryTokensLooted < 2) copy(players = listOf(players[0].lootSecondToken(), players[1])) else this
            in -5..-3 -> if (players[0].militaryTokensLooted < 1) copy(players = listOf(players[0].lootFirstToken(), players[1])) else this
            in 3..5 -> if (players[1].militaryTokensLooted < 2) copy(players = listOf(players[0], players[1].lootFirstToken())) else this
            in 6..8 -> if (players[1].militaryTokensLooted < 2) copy(players = listOf(players[0], players[1].lootSecondToken())) else this
            else -> this
        }
    }
}

data class Player(val number: Int, val militaryTokensLooted: Int = 0, val coins: Int = 7, val wonders: List<BuildableWonder> = emptyList(), val buildings: Set<Building> = emptySet()) {
    fun build(building: Building, opponent: Player): Player =
            if (buildings.contains(building.freeLink)) build(building) else pay(building.cost, opponent).build(building)

    fun discard(): Player = copy(coins = coins + 2 + buildings.count { it.type == COMMERCIAL })

    fun build(wonder: Wonder, buildingUsed: Building, opponent: Player): Player {
        require(wonders.any { it.wonder == wonder }) { "You do not have this wonder" }
        return pay(wonder.cost, opponent).copy(wonders = wonders.map { if (it.wonder == wonder) it.buildWith(buildingUsed) else it })
    }

    private fun pay(cost: Cost, opponent: Player): Player {
        val totalCost = cost.coins + tradingCost(cost.resources, opponent)
        //val totalCost = cost.coins + cost.resourcesTrading(this, opponent)
        require(coins >= totalCost) { "Not enough coins" }
        return copy(coins = coins - totalCost)
    }

    private fun tradingCost(resourcesCost: Map<Resource, Int>, opponent: Player): Int {
        return tradingCost(RAW_GOOD, resourcesCost, opponent) + tradingCost(MANUFACTURED_GOOD, resourcesCost, opponent)
    }

    private fun tradingCost(resourceType: Type, resourcesCost: Map<Resource, Int>, opponent: Player): Int {
        return resourcesCost.filter { it.key.type == resourceType }
                .flatMap { entry -> List(entry.value - productionOf(entry.key)) { tradingCost(entry.key, opponent) } }
                .sorted().dropLast(effects().count { it is ProductionOfAny && it.resourceType == resourceType })
                .sum()
    }

    private fun build(building: Building): Player {
        return copy(buildings = buildings.plus(building))
    }

    private fun tradingCost(resource: Resource, opponent: Player): Int =
            if (effects().any { it is FixTradingCostTo1 && it.resource == resource }) 1 else 2 + opponent.productionOf(resource)

    private fun productionOf(resource: Resource): Int =
            effects().asSequence().filterIsInstance<Production>().filter { it.resource == resource }.sumBy { it.quantity }

    private fun effects() = buildings.flatMap { it.effects.toList() }

    fun discardUnfinishedWonder(): Player {
        return copy(wonders = wonders.filter { it.isBuild() })
    }

    fun lootFirstToken(): Player {
        check(militaryTokensLooted < 1) {"First military token is already looted"}
        return copy(militaryTokensLooted = militaryTokensLooted + 1, coins = maxOf(coins - 2, 0))
    }

    fun lootSecondToken(): Player {
        check(militaryTokensLooted < 2) {"Second military token is already looted"}
        return if (militaryTokensLooted == 0) lootFirstToken().lootSecondToken() else copy(militaryTokensLooted = militaryTokensLooted + 1, coins = maxOf(coins - 5, 0))
    }
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
    return structureDescription.asSequence()
            .map {
                when (deck.size) {
                    0 -> emptyList()
                    in 1 until it.size -> it.take(deck.size)
                    else -> it
                }
            }.map { positions -> positions.associate { it to deck.removeAt(0) } }.toList()
}

enum class ProgressToken {
    AGRICULTURE, ARCHITECTURE, ECONOMY, LAW, MASONRY, MATHEMATICS, PHILOSOPHY, STRATEGY, THEOLOGY, URBANISM
}

enum class Wonder(val effects: List<Effect>, val cost: Cost) {
    THE_APPIAN_WAY(listOf(), Cost(resources = mapOf(PAPYRUS to 1, CLAY to 2, STONE to 2))),
    CIRCUS_MAXIMUS(listOf(Shield(1)), Cost(resources = mapOf(GLASS to 1, WOOD to 1, STONE to 2))),
    THE_COLOSSUS(listOf(Shield(2)), Cost(resources = mapOf(GLASS to 1, CLAY to 3))),
    THE_GREAT_LIBRARY(listOf(), Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, WOOD to 3))),
    THE_GREAT_LIGHTHOUSE(listOf(), Cost(resources = mapOf(PAPYRUS to 2, STONE to 1, WOOD to 1))),
    THE_HANGING_GARDENS(listOf(), Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, WOOD to 2))),
    THE_MAUSOLEUM(listOf(), Cost(resources = mapOf(PAPYRUS to 1, GLASS to 2, CLAY to 2))),
    PIRAEUS(listOf(), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 2))),
    THE_PYRAMIDS(listOf(), Cost(resources = mapOf(PAPYRUS to 1, STONE to 3))),
    THE_SPHINX(listOf(), Cost(resources = mapOf(GLASS to 2, CLAY to 1, STONE to 1))),
    THE_STATUE_OF_ZEUS(listOf(Shield(1)), Cost(resources = mapOf(PAPYRUS to 2, CLAY to 1, WOOD to 1, STONE to 1))),
    THE_TEMPLE_OF_ARTEMIS(listOf(), Cost(resources = mapOf(PAPYRUS to 1, GLASS to 1, STONE to 1, WOOD to 1)))
}

data class BuildableWonder(val wonder: Wonder, val builtWith: Building? = null) {
    fun buildWith(building: Building): BuildableWonder {
        check(!isBuild()) { "This wonder is already built" }
        return copy(builtWith = building)
    }

    fun isBuild(): Boolean = builtWith != null
}

enum class Building(val deck: BuildingDeck = BuildingDeck.GUILD, val type: BuildingType = GUILD, val effects: List<Effect>, val cost: Cost = Cost(), val freeLink: Building? = null) {
    LUMBER_YARD(AGE_I, RAW_MATERIAL, listOf(Production(WOOD))),
    LOGGING_CAMP(AGE_I, RAW_MATERIAL, listOf(Production(WOOD)), Cost(coins = 1)),
    CLAY_POOL(AGE_I, RAW_MATERIAL, listOf(Production(CLAY))),
    CLAY_PIT(AGE_I, RAW_MATERIAL, listOf(Production(CLAY)), Cost(coins = 1)),
    QUARRY(AGE_I, RAW_MATERIAL, listOf(Production(STONE))),
    STONE_PIT(AGE_I, RAW_MATERIAL, listOf(Production(STONE)), Cost(coins = 1)),
    GLASSWORKS(AGE_I, BuildingType.MANUFACTURED_GOOD, listOf(Production(GLASS)), Cost(coins = 1)),
    PRESS(AGE_I, BuildingType.MANUFACTURED_GOOD, listOf(Production(PAPYRUS)), Cost(coins = 1)),
    GUARD_TOWER(AGE_I, MILITARY, listOf(Shield(1))),
    STABLE(AGE_I, MILITARY, listOf(Shield(1)), Cost(resources = mapOf(WOOD to 1))),
    GARRISON(AGE_I, MILITARY, listOf(Shield(1)), Cost(resources = mapOf(CLAY to 1))),
    PALISADE(AGE_I, MILITARY, listOf(Shield(1)), Cost(coins = 2)),
    WORKSHOP(AGE_I, SCIENTIFIC, listOf(), Cost(resources = mapOf(PAPYRUS to 1))),
    APOTHECARY(AGE_I, SCIENTIFIC, listOf(), Cost(resources = mapOf(GLASS to 1))),
    SCRIPTORIUM(AGE_I, SCIENTIFIC, listOf(), Cost(coins = 2)),
    PHARMACIST(AGE_I, SCIENTIFIC, listOf(), Cost(coins = 2)),
    THEATER(AGE_I, CIVILIAN, listOf()),
    ALTAR(AGE_I, CIVILIAN, listOf()),
    BATHS(AGE_I, CIVILIAN, listOf(), Cost(resources = mapOf(STONE to 1))),
    STONE_RESERVE(AGE_I, COMMERCIAL, listOf(FixTradingCostTo1(STONE)), Cost(coins = 3)),
    CLAY_RESERVE(AGE_I, COMMERCIAL, listOf(FixTradingCostTo1(CLAY)), Cost(coins = 3)),
    WOOD_RESERVE(AGE_I, COMMERCIAL, listOf(FixTradingCostTo1(WOOD)), Cost(coins = 3)),
    TAVERN(AGE_I, COMMERCIAL, listOf()),
    SAWMILL(AGE_II, RAW_MATERIAL, listOf(Production(WOOD, 2)), Cost(coins = 2)),
    BRICKYARD(AGE_II, RAW_MATERIAL, listOf(Production(CLAY, 2)), Cost(coins = 2)),
    SHELF_QUARRY(AGE_II, RAW_MATERIAL, listOf(Production(STONE, 2)), Cost(coins = 2)),
    GLASSBLOWER(AGE_II, BuildingType.MANUFACTURED_GOOD, listOf(Production(GLASS))),
    DRYING_ROOM(AGE_II, BuildingType.MANUFACTURED_GOOD, listOf(Production(PAPYRUS))),
    WALLS(AGE_II, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(STONE to 2))),
    HORSE_BREEDERS(AGE_II, MILITARY, listOf(Shield(1)), Cost(resources = mapOf(CLAY to 1, WOOD to 1)), freeLink = STABLE),
    BARRACKS(AGE_II, MILITARY, listOf(Shield(1)), Cost(coins = 3), freeLink = GARRISON),
    ARCHERY_RANGE(AGE_II, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(STONE to 1, WOOD to 1, PAPYRUS to 1))),
    PARADE_GROUND(AGE_II, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(CLAY to 2, GLASS to 1))),
    LIBRARY(AGE_II, SCIENTIFIC, listOf(), Cost(resources = mapOf(STONE to 1, WOOD to 1, GLASS to 1)), freeLink = SCRIPTORIUM),
    DISPENSARY(AGE_II, SCIENTIFIC, listOf(), Cost(resources = mapOf(CLAY to 2, STONE to 1)), freeLink = PHARMACIST),
    SCHOOL(AGE_II, SCIENTIFIC, listOf(), Cost(resources = mapOf(WOOD to 1, PAPYRUS to 2))),
    LABORATORY(AGE_II, SCIENTIFIC, listOf(), Cost(resources = mapOf(WOOD to 1, GLASS to 2))),
    TRIBUNAL(AGE_II, CIVILIAN, listOf(), Cost(resources = mapOf(WOOD to 2, GLASS to 1))),
    STATUE(AGE_II, CIVILIAN, listOf(), Cost(resources = mapOf(CLAY to 2)), freeLink = THEATER),
    TEMPLE(AGE_II, CIVILIAN, listOf(), Cost(resources = mapOf(WOOD to 1, PAPYRUS to 1)), freeLink = ALTAR),
    AQUEDUCT(AGE_II, CIVILIAN, listOf(), Cost(resources = mapOf(STONE to 3)), freeLink = BATHS),
    ROSTRUM(AGE_II, CIVILIAN, listOf(), Cost(resources = mapOf(STONE to 1, WOOD to 1))),
    FORUM(AGE_II, COMMERCIAL, listOf(ProductionOfAny(MANUFACTURED_GOOD)), Cost(coins = 3, resources = mapOf(CLAY to 1))),
    CARAVANSERY(AGE_II, COMMERCIAL, listOf(ProductionOfAny(RAW_GOOD)), Cost(coins = 2, resources = mapOf(GLASS to 1, PAPYRUS to 1))),
    CUSTOMS_HOUSE(AGE_II, COMMERCIAL, listOf(FixTradingCostTo1(PAPYRUS), FixTradingCostTo1(GLASS)), Cost(coins = 4)),
    BREWERY(AGE_II, COMMERCIAL, listOf()),
    ARSENAL(AGE_III, MILITARY, listOf(Shield(3)), Cost(resources = mapOf(CLAY to 3, WOOD to 2))),
    COURTHOUSE(AGE_III, MILITARY, listOf(Shield(3)), Cost(coins = 8)),
    FORTIFICATIONS(AGE_III, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(STONE to 2, CLAY to 1, PAPYRUS to 1)), freeLink = PALISADE),
    SIEGE_WORKSHOP(AGE_III, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(WOOD to 3, GLASS to 1)), freeLink = ARCHERY_RANGE),
    CIRCUS(AGE_III, MILITARY, listOf(Shield(2)), Cost(resources = mapOf(CLAY to 2, STONE to 2)), freeLink = PARADE_GROUND),
    ACADEMY(AGE_III, SCIENTIFIC, listOf(), Cost(resources = mapOf(STONE to 1, WOOD to 1, GLASS to 2))),
    STUDY(AGE_III, SCIENTIFIC, listOf(), Cost(resources = mapOf(WOOD to 2, GLASS to 1, PAPYRUS to 1))),
    UNIVERSITY(AGE_III, SCIENTIFIC, listOf(), Cost(resources = mapOf(CLAY to 1, GLASS to 1, PAPYRUS to 1)), freeLink = SCHOOL),
    OBSERVATORY(AGE_III, SCIENTIFIC, listOf(), Cost(resources = mapOf(STONE to 1, PAPYRUS to 2)), freeLink = LABORATORY),
    PALACE(AGE_III, CIVILIAN, listOf(), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 1, GLASS to 2))),
    TOWN_HALL(AGE_III, CIVILIAN, listOf(), Cost(resources = mapOf(STONE to 3, WOOD to 2))),
    OBELISK(AGE_III, CIVILIAN, listOf(), Cost(resources = mapOf(STONE to 2, GLASS to 1))),
    GARDENS(AGE_III, CIVILIAN, listOf(), Cost(resources = mapOf(CLAY to 2, WOOD to 2)), freeLink = STATUE),
    PANTHEON(AGE_III, CIVILIAN, listOf(), Cost(resources = mapOf(CLAY to 1, WOOD to 1, PAPYRUS to 2)), freeLink = TEMPLE),
    SENATE(AGE_III, CIVILIAN, listOf(), Cost(resources = mapOf(CLAY to 2, STONE to 1, PAPYRUS to 1)), freeLink = ROSTRUM),
    CHAMBER_OF_COMMERCE(AGE_III, COMMERCIAL, listOf(), Cost(resources = mapOf(PAPYRUS to 2))),
    PORT(AGE_III, COMMERCIAL, listOf(), Cost(resources = mapOf(WOOD to 1, GLASS to 1, PAPYRUS to 1))),
    ARMORY(AGE_III, COMMERCIAL, listOf(), Cost(resources = mapOf(STONE to 2, GLASS to 1))),
    LIGHTHOUSE(AGE_III, COMMERCIAL, listOf(), Cost(resources = mapOf(CLAY to 2, GLASS to 1)), freeLink = TAVERN),
    ARENA(AGE_III, COMMERCIAL, listOf(), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 1)), freeLink = BREWERY),
    MERCHANTS_GUILD(effects = listOf(), cost = Cost(resources = mapOf(CLAY to 1, WOOD to 1, GLASS to 1, PAPYRUS to 1))),
    SHIPOWNERS_GUILD(effects = listOf(), cost = Cost(resources = mapOf(CLAY to 1, STONE to 1, GLASS to 1, PAPYRUS to 1))),
    BUILDERS_GUILD(effects = listOf(), cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, WOOD to 1, GLASS to 1))),
    MAGISTRATE_GUILD(effects = listOf(), cost = Cost(resources = mapOf(WOOD to 2, CLAY to 1, PAPYRUS to 1))),
    SCIENTISTS_GUILD(effects = listOf(), cost = Cost(resources = mapOf(CLAY to 2, WOOD to 2))),
    MONEYLENDERS_GUILD(effects = listOf(), cost = Cost(resources = mapOf(STONE to 2, WOOD to 2))),
    TACTICIANS_GUILD(effects = listOf(), cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, PAPYRUS to 1)))
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

interface Effect {
    fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game
    }
}

data class Production(val resource: Resource, val quantity: Int = 1) : Effect
data class FixTradingCostTo1(val resource: Resource) : Effect
data class ProductionOfAny(val resourceType: Type) : Effect
data class Shield(val quantity: Int) : Effect {
    override fun applyTo(game: SevenWondersDuel): SevenWondersDuel {
        return game.moveConflictPawn(quantity)
    }
}