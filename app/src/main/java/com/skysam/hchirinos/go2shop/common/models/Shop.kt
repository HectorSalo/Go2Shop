package com.skysam.hchirinos.go2shop.common.models

/**
 * Created by Hector Chirinos on 04/03/2021.
 */

data class Shop(
        val id: String,
        val name: String,
        val userId: String,
        val listProducts: MutableList<ProductsToListModel>,
        val total: Double,
        val dateCreated: Long,
        val rateChange: Double
)
