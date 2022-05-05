package com.skysam.hchirinos.go2shop.common.models

/**
 * Created by Hector Chirinos on 25/03/2021.
 */
data class ProductsToShopModel(
    val id: String,
    val name: String,
    val unit: String,
    val userId: String,
    val listId: String,
    val price: Double = 0.0,
    val quantity: Double = 1.0,
    var isCheckedToShop: Boolean = false,
    var isCheckedToStorage: Boolean = false,
    var deparment: String = ""
)