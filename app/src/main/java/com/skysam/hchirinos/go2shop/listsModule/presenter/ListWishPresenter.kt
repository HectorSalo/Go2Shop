package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product

interface ListWishPresenter {
    fun getProducts()

    fun resultGetProducts(products: MutableList<Product>)
}