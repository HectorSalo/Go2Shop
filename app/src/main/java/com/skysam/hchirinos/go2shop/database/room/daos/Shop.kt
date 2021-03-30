package com.skysam.hchirinos.go2shop.database.room.daos

import androidx.room.*
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Shop

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
@Dao
interface Shop {
    @Query("SELECT * FROM compras")
    suspend fun getAll(): MutableList<Shop>

    @Query("SELECT * FROM compras WHERE id = :id")
    suspend fun getById(id: String): Shop

    @Update
    suspend fun update(shop: Shop)

    @Insert
    suspend fun insert(shop: Shop)

    @Delete
    suspend fun delete(shop: Shop)
}