package com.skysam.hchirinos.go2shop.common.models

import java.util.*

/**
 * Created by Hector Chirinos on 17/08/2022.
 */

data class CurrentShop(
    val id: String,
    val name: String,
    val userId: String,
    val listProducts: MutableList<ProductsToShopModel>,
    val total: Double,
    val dateCreated: Date,
    val rateChange: Double
)
