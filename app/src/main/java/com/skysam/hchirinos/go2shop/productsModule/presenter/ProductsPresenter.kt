package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product

interface ProductsPresenter {
    fun getProducts()

    fun resultGetProducts(products: MutableList<Product>)
}