package com.skysam.hchirinos.go2shop.common.models

/**
 * Created by Hector Chirinos (Home) on 3/11/2021.
 */
data class ListShared(
    val id: String,
    var name: String,
    var usersId: MutableList<String>,
    val listProducts: MutableList<ProductsToListModel>,
    val total: Double,
    val dateCreated: Long,
    var userOwner: String
)