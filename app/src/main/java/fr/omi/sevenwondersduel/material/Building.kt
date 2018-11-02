package fr.omi.sevenwondersduel.material

import fr.omi.sevenwondersduel.Player
import fr.omi.sevenwondersduel.effects.*
import fr.omi.sevenwondersduel.effects.ScientificSymbol.*
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoins
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoinsForMajority
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoinsForPlayer
import fr.omi.sevenwondersduel.effects.takecoins.TakeCoinsForPlayerBuildings
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPoints
import fr.omi.sevenwondersduel.effects.victorypoints.VictoryPointsForMajority
import fr.omi.sevenwondersduel.material.Construction.Cost
import fr.omi.sevenwondersduel.material.Resource.*
import fr.omi.sevenwondersduel.material.Resource.Type.MANUFACTURED_GOOD
import fr.omi.sevenwondersduel.material.Resource.Type.RAW_GOOD


sealed class Building(override val effects: List<Effect>, override val cost: Cost = Cost(), val freeLink: Building? = null) : Construction {
    constructor(effect: Effect, cost: Cost, freeLink: Building? = null) : this(listOf(effect), cost, freeLink)
}

sealed class RawMaterialBuilding(resource: Resource, quantity: Int = 1, cost: Cost = Cost()) : Building(Production(resource, quantity), cost)

sealed class ManufactureBuilding(resource: Resource, cost: Cost = Cost(), freeLink: Building? = null) : Building(Production(resource), cost, freeLink)

sealed class CivilianBuilding(victoryPoints: Int, cost: Cost = Cost(), freeLink: Building? = null) : Building(VictoryPoints(victoryPoints), cost, freeLink)

sealed class ScientificBuilding(effects: List<Effect>, cost: Cost, freeLink: Building?) : Building(effects, cost, freeLink) {
    constructor(scientificSymbol: ScientificSymbol, cost: Cost = Cost(), freeLink: Building? = null) : this(listOf(scientificSymbol), cost, freeLink)
    constructor(scientificSymbol: ScientificSymbol, victoryPoints: Int, cost: Cost = Cost(), freeLink: Building? = null) : this(listOf(scientificSymbol, VictoryPoints(victoryPoints)), cost, freeLink)
}

sealed class CommercialBuilding(effects: List<Effect>, cost: Cost = Cost(), freeLink: Building? = null) : Building(effects, cost, freeLink) {
    constructor(effect: Effect, cost: Cost = Cost(), freeLink: Building? = null) : this(listOf(effect), cost, freeLink)
}

sealed class MilitaryBuilding(shields: Int, cost: Cost = Cost(), freeLink: Building? = null) : Building(listOf(Shield(shields)), cost, freeLink)

sealed class GuildBuilding(effects: List<Effect>, cost: Cost) : Building(effects, cost) {
    constructor(majority: (Player) -> Int, victoryPoints: Int, cost: Cost) : this(listOf(VictoryPointsForMajority(victoryPoints, majority)), cost)
    constructor(majority: (Player) -> Int, coins: Int, victoryPoints: Int, cost: Cost) : this(listOf(TakeCoinsForMajority(coins, majority), VictoryPointsForMajority(victoryPoints, majority)), cost)
}

object LumberYard : RawMaterialBuilding(WOOD)
object LoggingCamp : RawMaterialBuilding(WOOD, cost = Cost(coins = 1))
object ClayPool : RawMaterialBuilding(CLAY)
object ClayPit : RawMaterialBuilding(CLAY, cost = Cost(coins = 1))
object Quarry : RawMaterialBuilding(STONE)
object StonePit : RawMaterialBuilding(STONE, cost = Cost(coins = 1))

object Glassworks : ManufactureBuilding(GLASS, Cost(coins = 1))
object Press : ManufactureBuilding(PAPYRUS, Cost(coins = 1))

object GuardTower : MilitaryBuilding(shields = 1)
object Stable : MilitaryBuilding(shields = 1, cost = Cost(resources = mapOf(WOOD to 1)))
object Garrison : MilitaryBuilding(shields = 1, cost = Cost(resources = mapOf(CLAY to 1)))
object Palisade : MilitaryBuilding(shields = 1, cost = Cost(coins = 2))

object Workshop : ScientificBuilding(PENDULUM, victoryPoints = 1, cost = Cost(resources = mapOf(PAPYRUS to 1)))
object Apothecary : ScientificBuilding(WHEEL, victoryPoints = 1, cost = Cost(resources = mapOf(GLASS to 1)))
object Scriptorium : ScientificBuilding(INKWELL, Cost(coins = 2))
object Pharmacist : ScientificBuilding(MORTAR, Cost(coins = 2))

object Theater : CivilianBuilding(victoryPoints = 3)
object Altar : CivilianBuilding(victoryPoints = 3)
object Baths : CivilianBuilding(victoryPoints = 3, cost = Cost(resources = mapOf(STONE to 1)))

object StoneReserve : CommercialBuilding(FixTradingCostTo1(STONE), Cost(coins = 3))
object ClayReserve : CommercialBuilding(FixTradingCostTo1(CLAY), Cost(coins = 3))
object WoodReserve : CommercialBuilding(FixTradingCostTo1(WOOD), Cost(coins = 3))
object Tavern : CommercialBuilding(TakeCoins(4))

object Sawmill : RawMaterialBuilding(WOOD, quantity = 2, cost = Cost(coins = 2))
object Brickyard : RawMaterialBuilding(CLAY, quantity = 2, cost = Cost(coins = 2))
object ShelfQuarry : RawMaterialBuilding(STONE, quantity = 2, cost = Cost(coins = 2))

object Glassblower : ManufactureBuilding(GLASS)
object DryingRoom : ManufactureBuilding(PAPYRUS)

object Walls : MilitaryBuilding(shields = 2, cost = Cost(resources = mapOf(STONE to 2)))
object HorseBreeders : MilitaryBuilding(shields = 1, cost = Cost(resources = mapOf(CLAY to 1, WOOD to 1)), freeLink = Stable)
object Barracks : MilitaryBuilding(shields = 1, cost = Cost(coins = 3), freeLink = Garrison)
object ArcheryRange : MilitaryBuilding(shields = 2, cost = Cost(resources = mapOf(STONE to 1, WOOD to 1, PAPYRUS to 1)))
object ParadeGround : MilitaryBuilding(shields = 2, cost = Cost(resources = mapOf(CLAY to 2, GLASS to 1)))

object Library : ScientificBuilding(INKWELL, victoryPoints = 2, cost = Cost(resources = mapOf(STONE to 1, WOOD to 1, GLASS to 1)), freeLink = Scriptorium)
object Dispensary : ScientificBuilding(MORTAR, victoryPoints = 2, cost = Cost(resources = mapOf(CLAY to 2, STONE to 1)), freeLink = Pharmacist)
object School : ScientificBuilding(WHEEL, victoryPoints = 1, cost = Cost(resources = mapOf(WOOD to 1, PAPYRUS to 2)))
object Laboratory : ScientificBuilding(PENDULUM, victoryPoints = 1, cost = Cost(resources = mapOf(WOOD to 1, GLASS to 2)))

object Courthouse : CivilianBuilding(victoryPoints = 5, cost = Cost(resources = mapOf(WOOD to 2, GLASS to 1)))
object Statue : CivilianBuilding(victoryPoints = 4, cost = Cost(resources = mapOf(CLAY to 2)), freeLink = Theater)
object Temple : CivilianBuilding(victoryPoints = 4, cost = Cost(resources = mapOf(WOOD to 1, PAPYRUS to 1)), freeLink = Altar)
object Aqueduct : CivilianBuilding(victoryPoints = 5, cost = Cost(resources = mapOf(STONE to 3)), freeLink = Baths)
object Rostrum : CivilianBuilding(victoryPoints = 4, cost = Cost(resources = mapOf(STONE to 1, WOOD to 1)))

object Forum : CommercialBuilding(ProductionOfAny(MANUFACTURED_GOOD), Cost(coins = 3, resources = mapOf(CLAY to 1)))
object Caravansery : CommercialBuilding(ProductionOfAny(RAW_GOOD), Cost(coins = 2, resources = mapOf(GLASS to 1, PAPYRUS to 1)))
object CustomsHouse : CommercialBuilding(listOf(FixTradingCostTo1(PAPYRUS), FixTradingCostTo1(GLASS)), Cost(coins = 4))
object Brewery : CommercialBuilding(TakeCoins(6))

object Arsenal : MilitaryBuilding(shields = 3, cost = Cost(resources = mapOf(CLAY to 3, WOOD to 2)))
object Pretorium : MilitaryBuilding(shields = 3, cost = Cost(coins = 8))
object Fortifications : MilitaryBuilding(shields = 2, cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, PAPYRUS to 1)), freeLink = Palisade)
object SiegeWorkshop : MilitaryBuilding(shields = 2, cost = Cost(resources = mapOf(WOOD to 3, GLASS to 1)), freeLink = ArcheryRange)
object Circus : MilitaryBuilding(shields = 2, cost = Cost(resources = mapOf(CLAY to 2, STONE to 2)), freeLink = ParadeGround)

object Academy : ScientificBuilding(SUNDIAL, victoryPoints = 3, cost = Cost(resources = mapOf(STONE to 1, WOOD to 1, GLASS to 2)))
object Study : ScientificBuilding(SUNDIAL, victoryPoints = 3, cost = Cost(resources = mapOf(WOOD to 2, GLASS to 1, PAPYRUS to 1)))
object University : ScientificBuilding(GYROSCOPE, victoryPoints = 2, cost = Cost(resources = mapOf(CLAY to 1, GLASS to 1, PAPYRUS to 1)), freeLink = School)
object Observatory : ScientificBuilding(GYROSCOPE, victoryPoints = 2, cost = Cost(resources = mapOf(STONE to 1, PAPYRUS to 2)), freeLink = Laboratory)

object Palace : CivilianBuilding(victoryPoints = 7, cost = Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 1, GLASS to 2)))
object TownHall : CivilianBuilding(victoryPoints = 7, cost = Cost(resources = mapOf(STONE to 3, WOOD to 2)))
object Obelisk : CivilianBuilding(victoryPoints = 5, cost = Cost(resources = mapOf(STONE to 2, GLASS to 1)))
object Gardens : CivilianBuilding(victoryPoints = 6, cost = Cost(resources = mapOf(CLAY to 2, WOOD to 2)), freeLink = Statue)
object Pantheon : CivilianBuilding(victoryPoints = 6, cost = Cost(resources = mapOf(CLAY to 1, WOOD to 1, PAPYRUS to 2)), freeLink = Temple)
object Senate : CivilianBuilding(victoryPoints = 5, cost = Cost(resources = mapOf(CLAY to 2, STONE to 1, PAPYRUS to 1)), freeLink = Rostrum)

object ChamberOfCommerce : CommercialBuilding(listOf(TakeCoinsForPlayerBuildings(3) { it is ManufactureBuilding }, VictoryPoints(3)), Cost(resources = mapOf(PAPYRUS to 2)))
object Port : CommercialBuilding(listOf(TakeCoinsForPlayerBuildings(2) { it is RawMaterialBuilding }, VictoryPoints(3)), Cost(resources = mapOf(WOOD to 1, GLASS to 1, PAPYRUS to 1)))
object Armory : CommercialBuilding(listOf(TakeCoinsForPlayerBuildings(1) { it is MilitaryBuilding }, VictoryPoints(3)), Cost(resources = mapOf(STONE to 2, GLASS to 1)))
object Lighthouse : CommercialBuilding(listOf(TakeCoinsForPlayerBuildings(1) { it is CommercialBuilding }, VictoryPoints(3)), Cost(resources = mapOf(CLAY to 2, GLASS to 1)), freeLink = Tavern)
object Arena : CommercialBuilding(listOf(TakeCoinsForPlayer { player -> 2 * player.wonders.count { it.isBuild() } }, VictoryPoints(3)), Cost(resources = mapOf(CLAY to 1, STONE to 1, WOOD to 1)), freeLink = Brewery)

object MerchantsGuild : GuildBuilding(majority = { player -> player.buildings.count { it is CommercialBuilding } }, coins = 1, victoryPoints = 1, cost = Cost(resources = mapOf(CLAY to 1, WOOD to 1, GLASS to 1, PAPYRUS to 1)))
object ShipownersGuild : GuildBuilding(majority = { player -> player.buildings.count { it is RawMaterialBuilding || it is ManufactureBuilding } }, coins = 1, victoryPoints = 1, cost = Cost(resources = mapOf(CLAY to 1, STONE to 1, GLASS to 1, PAPYRUS to 1)))
object BuildersGuild : GuildBuilding(majority = { player -> player.wonders.count { it.isBuild() } }, victoryPoints = 2, cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, WOOD to 1, GLASS to 1)))
object MagistrateGuild : GuildBuilding(majority = { player -> player.buildings.count { it is CivilianBuilding } }, coins = 1, victoryPoints = 1, cost = Cost(resources = mapOf(WOOD to 2, CLAY to 1, PAPYRUS to 1)))
object ScientistsGuild : GuildBuilding(majority = { player -> player.buildings.count { it is ScientificBuilding } }, coins = 1, victoryPoints = 1, cost = Cost(resources = mapOf(CLAY to 2, WOOD to 2)))
object MoneylendersGuild : GuildBuilding(majority = { it.coins / 3 }, victoryPoints = 1, cost = Cost(resources = mapOf(STONE to 2, WOOD to 2)))
object TacticiansGuild : GuildBuilding(majority = { player -> player.buildings.count { it is MilitaryBuilding } }, coins = 1, victoryPoints = 1, cost = Cost(resources = mapOf(STONE to 2, CLAY to 1, PAPYRUS to 1)))
