package com.skysam.hchirinos.go2shop.common.models

import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
data class ListWish(
    val id: String,
    val name: String,
    var userId: String,
    val listProducts: MutableList<ProductsToListModel>,
    val total: Double,
    val dateCreated: Long,
    val lastEdited: Long,
    var isShare: Boolean = false,
    var nameUserShared: String? = null
)
