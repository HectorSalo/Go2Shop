package com.skysam.hchirinos.go2shop.productsModule.ui

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos (Home) on 10/3/2021.
 */
interface ProductView {
    fun resultGetProducts(products: MutableList<Product>)
    fun resultDeleteProducts(statusOk: Boolean, msg: String)
}