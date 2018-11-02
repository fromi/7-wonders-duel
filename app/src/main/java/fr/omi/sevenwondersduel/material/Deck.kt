package fr.omi.sevenwondersduel.material

enum class Deck(val buildings: Set<Building>) {
    AGE_I(setOf(LumberYard, LoggingCamp, ClayPool, ClayPit, Quarry, StonePit,
            Glassworks, Press,
            GuardTower, Stable, Garrison, Palisade,
            Workshop, Apothecary, Scriptorium, Pharmacist,
            Theater, Altar, Baths,
            StoneReserve, ClayReserve, WoodReserve, Tavern)),
    AGE_II(setOf(Sawmill, Brickyard, ShelfQuarry,
            Glassblower, DryingRoom,
            Walls, HorseBreeders, Barracks, ArcheryRange, ParadeGround,
            Library, Dispensary, School, Laboratory,
            Courthouse, Statue, Temple, Aqueduct, Rostrum,
            Forum, Caravansery, CustomsHouse, Brewery
    )),
    AGE_III(setOf(Arsenal, Pretorium, Fortifications, SiegeWorkshop, Circus,
            Academy, Study, University, Observatory,
            Palace, TownHall, Obelisk, Gardens, Pantheon, Senate,
            ChamberOfCommerce, Port, Armory, Lighthouse, Arena
    )),
    GUILDS(setOf(MerchantsGuild, ShipownersGuild, BuildersGuild, MagistrateGuild, ScientistsGuild, MoneylendersGuild, TacticiansGuild));
}