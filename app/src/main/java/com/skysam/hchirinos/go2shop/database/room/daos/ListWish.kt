package com.skysam.hchirinos.go2shop.database.room.daos

import androidx.room.*
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
@Dao
interface ListWish {
    @Query("SELECT * FROM listasPendientes")
    suspend fun getAll(): MutableList<ListWish>

    @Query("SELECT * FROM listasPendientes WHERE id = :id")
    suspend fun getById(id: String): ListWish

    @Query("UPDATE listasPendientes SET listProducts= :list WHERE id = :id")
    suspend fun updateListProducts(id: String, list: MutableList<ProductsToListModel>)

    @Update
    suspend fun update(listWish: ListWish)

    @Insert
    suspend fun insert(listWish: ListWish)

    @Query ("DELETE FROM listasPendientes WHERE id = :id")
    suspend fun delete(id: String)

    @Query ("DELETE FROM listasPendientes")
    suspend fun deleteAll()
}