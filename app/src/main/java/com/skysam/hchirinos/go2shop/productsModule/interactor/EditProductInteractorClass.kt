package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.Network
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.presenter.EditProductPresenter

/**
 * Created by Hector Chirinos on 11/03/2021.
 */
class EditProductInteractorClass(private val editProductPresenter: EditProductPresenter): EditProductInteractor {
    override fun editToFirestore(product: Product) {
        val data = hashMapOf(
            Constants.NAME to product.name,
            Constants.UNIT to product.unit,
            Constants.USER_ID to product.userId,
            Constants.PRICE to product.price,
            Constants.QUANTITY to product.quantity
        )
        FirestoreAPI.getProductById(product.id)
            .set(data)
            .addOnSuccessListener { editProductPresenter.resultEditToFirestore(true, "") }
            .addOnFailureListener { e-> editProductPresenter.resultEditToFirestore(false, e.toString()) }

        if (!Network.isAvailable()) {
            editProductPresenter.resultEditToFirestore(true, "")
        }
    }
}