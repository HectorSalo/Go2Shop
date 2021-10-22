package com.skysam.hchirinos.go2shop.common.models

import java.util.*

/**
 * Created by Hector Chirinos on 03/05/2021.
 */
data class StorageModel(
    var id: String,
    val name: String,
    var unit: String,
    val userId: String,
    var quantityFromShop: Double,
    var dateShop: Date,
    var quantityRemaining: Double,
    var price: Double,
)
