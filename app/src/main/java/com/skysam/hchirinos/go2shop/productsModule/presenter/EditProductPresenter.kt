package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos on 11/03/2021.
 */
interface EditProductPresenter {
    fun editToFirestore(product: Product)
    fun resultEditToFirestore(statusOk: Boolean, msg: String)
}