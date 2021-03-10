package com.skysam.hchirinos.go2shop.database.room

import androidx.room.Database
import androidx.room.RoomDatabase

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.room.entities.Shop

/**
 * Created by Hector Chirinos on 04/03/2021.
 */

@Database(entities = [Shop::class, ListWish::class, Product::class], version = 2)
abstract class ProductDB: RoomDatabase() {
    abstract fun product(): com.skysam.hchirinos.go2shop.database.room.daos.Product
    abstract fun listWish(): com.skysam.hchirinos.go2shop.database.room.daos.ListWish
    abstract fun shop(): com.skysam.hchirinos.go2shop.database.room.daos.Shop
}