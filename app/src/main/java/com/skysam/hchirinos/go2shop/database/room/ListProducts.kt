package com.skysam.hchirinos.go2shop.database.room

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
data class ListProducts(
    val list: MutableList<Product> = mutableListOf()
)
