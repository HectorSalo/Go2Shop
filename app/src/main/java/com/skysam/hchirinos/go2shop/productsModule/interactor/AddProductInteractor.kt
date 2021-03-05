package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.common.model.ProductModel

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
interface AddProductInteractor {
    fun saveProductToFirestore(product: ProductModel)
    fun saveProductToRoom(id: String, product: ProductModel)
}