package com.skysam.hchirinos.go2shop.common.model

data class ListWishModel (
    var id: String,
    var name: String,
    var product: List<ProductModel>,
    var total: Double
)