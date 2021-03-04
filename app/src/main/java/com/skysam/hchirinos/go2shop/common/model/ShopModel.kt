package com.skysam.hchirinos.go2shop.common.model

import java.util.*

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
data class ShopModel (
    var id: String,
    val list: List<ProductModel>,
    val total: Double,
    val date: Date
)