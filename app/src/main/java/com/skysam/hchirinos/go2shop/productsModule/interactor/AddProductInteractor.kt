package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
interface AddProductInteractor {
    fun saveProductToFirestore(product: Product)
    fun saveProductToRoom(id: String, product: Product)
}