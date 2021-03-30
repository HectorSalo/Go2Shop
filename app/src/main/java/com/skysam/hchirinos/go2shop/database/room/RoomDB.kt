package com.skysam.hchirinos.go2shop.database.room

import androidx.room.Room
import com.skysam.hchirinos.go2shop.common.GoToShop
import com.skysam.hchirinos.go2shop.common.classView.ProductsSavedToList
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object RoomDB: CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private const val NAME_DB_ROOM = "goToShop"

    fun getInstance(): ProductDB {
        return Room.databaseBuilder(GoToShop.GoToShop.getContext(),
        ProductDB::class.java, NAME_DB_ROOM)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun deleteListWishToRoom(id: String) {
        launch {
            getInstance().listWish()
                .delete(id)
        }
    }

    fun updateListWishToRoom(list: ListWish) {
        launch {
            getInstance().listWish()
                .update(list)
        }
    }

    fun saveListWishToRoom(list: ListWish) {
        launch {
            getInstance().listWish()
                .insert(list)
        }
    }

    fun saveListShopToRoom(list: Shop) {
        launch {
            getInstance().shop()
                    .insert(list)
        }
    }

    fun deleteProductToRoom(product: Product) {
        launch {
            getInstance().product()
                .delete(product)
        }
    }

    fun updateProductToRoom(product: Product) {
        launch {
            getInstance().product()
                .update(product)
        }
    }

    fun saveProductToRoom(product: Product) {
        launch {
            getInstance().product()
                .insert(product)
        }
    }
}