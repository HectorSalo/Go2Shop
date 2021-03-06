package com.skysam.hchirinos.go2shop.database.room.daos

import androidx.room.*
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import kotlinx.coroutines.flow.Flow

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
@Dao
interface Shop {
    @Query("SELECT * FROM compras ORDER BY dateCreated DESC")
    fun getAll(): Flow<MutableList<Shop>>

    @Query("SELECT * FROM compras WHERE id = :id")
    suspend fun getById(id: String): Shop?

    @Insert
    suspend fun insert(shop: Shop)

    @Update
    suspend fun update(shop: Shop)

    @Query ("DELETE FROM compras")
    suspend fun deleteAll()
}