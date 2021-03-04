package com.skysam.hchirinos.go2shop.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skysam.hchirinos.go2shop.common.Constants

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
@Entity (tableName = Constants.SHOP)
data class Shop(
    @PrimaryKey val id: String,
    val listProducts: String,
    val total: Double,
    val dateLong: Long
)
