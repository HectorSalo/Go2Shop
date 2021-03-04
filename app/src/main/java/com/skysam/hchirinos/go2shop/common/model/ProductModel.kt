package com.skysam.hchirinos.go2shop.common.model

data class ProductModel(
    var id: String,
    var name: String,
    var unit: String,
    var price: Double = 1.0,
    var quantity: Double = 1.0
)
