package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos (Home) on 10/3/2021.
 */
interface ProductInteractor {
    fun getProducts()
    fun deleteProducts(products: MutableList<Product>)
}