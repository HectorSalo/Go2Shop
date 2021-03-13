package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos (Home) on 10/3/2021.
 */
interface ProductFragmentPresenter {
    fun deleteProducts(products: MutableList<Product>)

    fun resultDeleteProducts(statusOk: Boolean, msg: String)
}