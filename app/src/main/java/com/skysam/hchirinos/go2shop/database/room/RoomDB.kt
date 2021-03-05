package com.skysam.hchirinos.go2shop.database.room

import androidx.room.Room
import com.skysam.hchirinos.go2shop.common.GoToShop

object RoomDB {
    const val NAME_DB_ROOM = "goToShop"
    fun getInstance(): ProductDB {
        return Room.databaseBuilder(GoToShop.GoToShop.getContext(),
        ProductDB::class.java, NAME_DB_ROOM)
            .fallbackToDestructiveMigration()
            .build()
    }
}