package com.skysam.hchirinos.go2shop.database.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
@Dao
interface Product {
    @Query("SELECT * FROM productos")
    suspend fun getAll(): MutableList<Product>

    @Query("SELECT * FROM productos WHERE name = :name")
    suspend fun getByName(name: String): Product

    @Update
    suspend fun update(product: Product)

    @Insert
    suspend fun insert(product: Product)
}