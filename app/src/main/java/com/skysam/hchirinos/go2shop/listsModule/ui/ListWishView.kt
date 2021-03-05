package com.skysam.hchirinos.go2shop.listsModule.ui

import com.skysam.hchirinos.go2shop.database.room.entities.Product

interface ListWishView {
    fun resultGetProducts(productsName: MutableList<String>)
}
