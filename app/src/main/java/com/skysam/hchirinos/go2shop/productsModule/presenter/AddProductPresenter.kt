package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.common.model.ProductModel

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
interface AddProductPresenter {
    fun saveProductToFirestore(product: ProductModel)

    fun resultSaveProductFirestore(statusOk: Boolean, msg: String, product: ProductModel?)
    fun resultSaveProductFirestore()
}