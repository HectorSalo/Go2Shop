package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
interface AddProductPresenter {
    fun saveProductToFirestore(product: Product)

    fun resultSaveProductFirestore(statusOk: Boolean, msg: String)
}