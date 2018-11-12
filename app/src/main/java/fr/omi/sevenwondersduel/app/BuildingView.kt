package fr.omi.sevenwondersduel.app

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.widget.ImageView
import fr.omi.sevenwondersduel.R
import fr.omi.sevenwondersduel.SevenWondersDuel
import fr.omi.sevenwondersduel.material.*
import kotlinx.android.synthetic.main.activity_game.*

@SuppressLint("ViewConstructor")
class BuildingView(override val gameActivity: GameActivity, private val deck: Deck, val building: Building, var faceUp: Boolean) : ImageView(gameActivity), GameView {
    constructor(gameActivity: GameActivity, building: Building, faceUp: Boolean) : this(gameActivity, Deck.values().first { it.buildings.contains(building) }, building, faceUp)
    constructor(gameActivity: GameActivity, buildingCard: BuildingCard) : this(gameActivity, Deck.values().first { it.buildings.contains(buildingCard.building) }, buildingCard.building, buildingCard.faceUp)

    init {
        id = generateViewId()
        setImageResource(if (faceUp) getResource(building) else getResource(deck))
        contentDescription = resources.getString(if (faceUp) getContentDescription(building) else getContentDescription(deck))
        layoutParams = ConstraintLayout.LayoutParams(dpsToPx(35), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        adjustViewBounds = true
        layout.addView(this)
    }

    fun positionInStructure(row: Int, column: Int) {
        layout.transform {
            connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.BOTTOM, dpsToPx(row * 30))
            connect(id, ConstraintSet.START, R.id.layout, ConstraintSet.START, if (column > 0) dpsToPx(column * 40) else 0)
            connect(id, ConstraintSet.END, R.id.layout, ConstraintSet.END, if (column < 0) dpsToPx(-column * 40) else 0)
        }
    }

    fun positionForPlayer(player: Int, position: Int) {
        layout.transform {
            clear(id, ConstraintSet.START)
            clear(id, ConstraintSet.END)
            connect(id, ConstraintSet.TOP, R.id.board, ConstraintSet.BOTTOM, dpsToPx(position * 12))
            val constraint = if (player == 1) ConstraintSet.START else ConstraintSet.END
            connect(id, constraint, R.id.layout, constraint, dpsToPx(100))
        }
    }

    fun positionToNextBuildingPlace(game: SevenWondersDuel) {
        positionForPlayer(checkNotNull(game.currentPlayerNumber), game.currentPlayer.buildings.size)
        bringToFront()
    }

    fun positionUnder(wonderView: WonderView, player: Int) {
        putFaceDown()
        rotation = if (player == 1) 270F else 90F
        wonderView.bringToFront()
        layout.transform {
            clear(id, ConstraintSet.START)
            clear(id, ConstraintSet.END)
            clear(id, ConstraintSet.TOP)
            clear(id, ConstraintSet.BOTTOM)
            connect(id, ConstraintSet.TOP, wonderView.id, ConstraintSet.TOP)
            connect(id, ConstraintSet.BOTTOM, wonderView.id, ConstraintSet.BOTTOM)
            val constraint = if (player == 1) ConstraintSet.START else ConstraintSet.END
            connect(id, constraint, wonderView.id, constraint, dpsToPx(40))
        }
    }

    fun reveal() {
        if (!faceUp) {
            faceUp = true
            setImageResource(getResource(building))
            contentDescription = resources.getString(getContentDescription(building))
        }
    }

    private fun putFaceDown() {
        faceUp = false
        setImageResource(getResource(deck))
        contentDescription = resources.getString(getContentDescription(deck))
    }

    fun positionInDiscard(position: Int) {
        layout.transform {
            clear(id, ConstraintSet.START)
            clear(id, ConstraintSet.END)
            clear(id, ConstraintSet.TOP)
            clear(id, ConstraintSet.BOTTOM)
            connect(id, ConstraintSet.TOP, gameActivity.discardedCardsLayer.id, ConstraintSet.TOP, dpsToPx(40 + position / 7 * 60))
            connect(id, ConstraintSet.START, gameActivity.discardedCardsLayer.id, ConstraintSet.START, dpsToPx(position % 7 * 41 + 10))
        }
    }

    companion object {
        fun getResource(building: Building): Int {
            return when (building) {
                LumberYard -> R.drawable.lumber_yard
                LoggingCamp -> R.drawable.logging_camp
                ClayPool -> R.drawable.clay_pool
                ClayPit -> R.drawable.clay_pit
                Quarry -> R.drawable.quarry
                StonePit -> R.drawable.stone_pit
                Glassworks -> R.drawable.glassworks
                Press -> R.drawable.press
                GuardTower -> R.drawable.guard_tower
                Stable -> R.drawable.stable
                Garrison -> R.drawable.garrison
                Palisade -> R.drawable.palisade
                Workshop -> R.drawable.workshop
                Apothecary -> R.drawable.apothecary
                Scriptorium -> R.drawable.scriptorium
                Pharmacist -> R.drawable.pharmacist
                Theater -> R.drawable.theater
                Altar -> R.drawable.altar
                Baths -> R.drawable.baths
                StoneReserve -> R.drawable.stone_reserve
                ClayReserve -> R.drawable.clay_reserve
                WoodReserve -> R.drawable.wood_reserve
                Tavern -> R.drawable.tavern
                Sawmill -> R.drawable.sawmill
                Brickyard -> R.drawable.brickyard
                ShelfQuarry -> R.drawable.shelf_quarry
                Glassblower -> R.drawable.glassblower
                DryingRoom -> R.drawable.drying_room
                Walls -> R.drawable.walls
                HorseBreeders -> R.drawable.horse_breeders
                Barracks -> R.drawable.barracks
                ArcheryRange -> R.drawable.archery_range
                ParadeGround -> R.drawable.parade_ground
                Library -> R.drawable.library
                Dispensary -> R.drawable.dispensary
                School -> R.drawable.school
                Laboratory -> R.drawable.laboratory
                Courthouse -> R.drawable.courthouse
                Statue -> R.drawable.statue
                Temple -> R.drawable.temple
                Aqueduct -> R.drawable.aqueduct
                Rostrum -> R.drawable.rostrum
                Forum -> R.drawable.forum
                Caravansery -> R.drawable.caravansery
                CustomsHouse -> R.drawable.customs_house
                Brewery -> R.drawable.brewery
                Arsenal -> R.drawable.arsenal
                Pretorium -> R.drawable.pretorium
                Fortifications -> R.drawable.fortifications
                SiegeWorkshop -> R.drawable.siege_workshop
                Circus -> R.drawable.circus
                Academy -> R.drawable.academy
                Study -> R.drawable.study
                University -> R.drawable.university
                Observatory -> R.drawable.observatory
                Palace -> R.drawable.palace
                TownHall -> R.drawable.town_hall
                Obelisk -> R.drawable.obelisk
                Gardens -> R.drawable.gardens
                Pantheon -> R.drawable.pantheon
                Senate -> R.drawable.senate
                ChamberOfCommerce -> R.drawable.chamber_of_commerce
                Port -> R.drawable.port
                Armory -> R.drawable.armory
                Lighthouse -> R.drawable.lighthouse
                Arena -> R.drawable.arena
                MerchantsGuild -> R.drawable.merchants_guild
                ShipownersGuild -> R.drawable.shipowners_guild
                BuildersGuild -> R.drawable.builders_guild
                MagistrateGuild -> R.drawable.magistrate_guild
                ScientistsGuild -> R.drawable.scientists_guild
                MoneylendersGuild -> R.drawable.moneylenders_guild
                TacticiansGuild -> R.drawable.tacticians_guild

            }
        }

        fun getResource(deck: Deck): Int {
            return when (deck) {
                Deck.AGE_I -> R.drawable.age_1_back
                Deck.AGE_II -> R.drawable.age_2_back
                Deck.AGE_III -> R.drawable.age_3_back
                Deck.GUILDS -> R.drawable.guild_back
            }
        }

        fun getContentDescription(building: Building): Int {
            return when (building) {
                LumberYard -> R.string.lumber_yard
                LoggingCamp -> R.string.logging_camp
                ClayPool -> R.string.clay_pool
                ClayPit -> R.string.clay_pit
                Quarry -> R.string.quarry
                StonePit -> R.string.stone_pit
                Glassworks -> R.string.glassworks
                Press -> R.string.press
                GuardTower -> R.string.guard_tower
                Stable -> R.string.stable
                Garrison -> R.string.garrison
                Palisade -> R.string.palisade
                Workshop -> R.string.workshop
                Apothecary -> R.string.apothecary
                Scriptorium -> R.string.scriptorium
                Pharmacist -> R.string.pharmacist
                Theater -> R.string.theater
                Altar -> R.string.altar
                Baths -> R.string.baths
                StoneReserve -> R.string.stone_reserve
                ClayReserve -> R.string.clay_reserve
                WoodReserve -> R.string.wood_reserve
                Tavern -> R.string.tavern
                Sawmill -> R.string.sawmill
                Brickyard -> R.string.brickyard
                ShelfQuarry -> R.string.shelf_quarry
                Glassblower -> R.string.glassblower
                DryingRoom -> R.string.drying_room
                Walls -> R.string.walls
                HorseBreeders -> R.string.horse_breeders
                Barracks -> R.string.barracks
                ArcheryRange -> R.string.archery_range
                ParadeGround -> R.string.parade_ground
                Library -> R.string.library
                Dispensary -> R.string.dispensary
                School -> R.string.school
                Laboratory -> R.string.laboratory
                Courthouse -> R.string.courthouse
                Statue -> R.string.statue
                Temple -> R.string.temple
                Aqueduct -> R.string.aqueduct
                Rostrum -> R.string.rostrum
                Forum -> R.string.forum
                Caravansery -> R.string.caravansery
                CustomsHouse -> R.string.customs_house
                Brewery -> R.string.brewery
                Arsenal -> R.string.arsenal
                Pretorium -> R.string.pretorium
                Fortifications -> R.string.fortifications
                SiegeWorkshop -> R.string.siege_workshop
                Circus -> R.string.circus
                Academy -> R.string.academy
                Study -> R.string.study
                University -> R.string.university
                Observatory -> R.string.observatory
                Palace -> R.string.palace
                TownHall -> R.string.town_hall
                Obelisk -> R.string.obelisk
                Gardens -> R.string.gardens
                Pantheon -> R.string.pantheon
                Senate -> R.string.senate
                ChamberOfCommerce -> R.string.chamber_of_commerce
                Port -> R.string.port
                Armory -> R.string.armory
                Lighthouse -> R.string.lighthouse
                Arena -> R.string.arena
                MerchantsGuild -> R.string.merchants_guild
                ShipownersGuild -> R.string.shipowners_guild
                BuildersGuild -> R.string.builders_guild
                MagistrateGuild -> R.string.magistrate_guild
                ScientistsGuild -> R.string.scientists_guild
                MoneylendersGuild -> R.string.moneylenders_guild
                TacticiansGuild -> R.string.tacticians_guild
            }
        }

        fun getContentDescription(deck: Deck): Int {
            return when (deck) {
                Deck.AGE_I -> R.string.age_1_back
                Deck.AGE_II -> R.string.age_2_back
                Deck.AGE_III -> R.string.age_3_back
                Deck.GUILDS -> R.string.guild_back
            }
        }
    }
}
