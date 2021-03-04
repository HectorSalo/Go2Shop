package com.skysam.hchirinos.go2shop.database.room.daos

import androidx.room.*
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
@Dao
interface ListWish {
    @Query("SELECT * FROM listasPendientes WHERE name = :name")
    suspend fun getByName(name: String): ListWish

    @Update
    suspend fun update(listWish: ListWish)

    @Insert
    suspend fun insert(listWish: ListWish)

    @Delete
    suspend fun delete(listWish: ListWish)
}