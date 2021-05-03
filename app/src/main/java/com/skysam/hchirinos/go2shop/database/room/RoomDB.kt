package com.skysam.hchirinos.go2shop.database.room

import androidx.room.Room
import com.skysam.hchirinos.go2shop.common.GoToShop
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

    fun saveListShopToRoom(list: Shop) {
        launch {
            getInstance().shop()
                    .insert(list)
        }
    }
}