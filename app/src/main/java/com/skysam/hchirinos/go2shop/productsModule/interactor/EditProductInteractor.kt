package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos on 11/03/2021.
 */
interface EditProductInteractor {
    fun editToFirestore(product: Product)
}