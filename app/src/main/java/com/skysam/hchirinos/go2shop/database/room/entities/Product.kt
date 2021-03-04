package com.skysam.hchirinos.go2shop.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skysam.hchirinos.go2shop.common.Constants

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
@Entity (tableName = Constants.PRODUCTOS)
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val unit: String,
    val price: Double = 1.0,
    val quantity: Double = 1.0
)
