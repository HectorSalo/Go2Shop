package com.skysam.hchirinos.go2shop.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.database.room.ProductsConverter

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
@Entity (tableName = Constants.LIST_WISH)
data class ListWish(
    @PrimaryKey val id: String,
    val name: String,
    val userId: String,
    @TypeConverters(ProductsConverter::class)
    val listProducts: MutableList<Product>,
    val total: Double
)
