package com.skysam.hchirinos.go2shop.common.models

/**
 * Created by Hector Chirinos on 04/03/2021.
 */

data class Product(
    val id: String,
    val name: String,
    val unit: String,
    val userId: String,
    val price: Double = 0.0,
    val quantity: Double = 1.0,
    var deparment: String = ""
)
