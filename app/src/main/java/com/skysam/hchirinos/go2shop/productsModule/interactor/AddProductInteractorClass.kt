package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.Network
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.presenter.AddProductPresenter

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
class AddProductInteractorClass(private val addProductPresenter: AddProductPresenter): AddProductInteractor {

    override fun saveProductToFirestore(product: Product) {
        val data = hashMapOf(
            Constants.NAME to product.name,
            Constants.UNIT to product.unit,
            Constants.USER_ID to product.userId,
            Constants.PRICE to product.price,
            Constants.QUANTITY to product.quantity
        )
        FirestoreAPI.getProducts().add(data)
            .addOnSuccessListener { doc->
                addProductPresenter.resultSaveProductFirestore(true, doc.id)
            }
            .addOnFailureListener { e->
                addProductPresenter.resultSaveProductFirestore(false, e.toString())
            }

        if (!Network.isAvailable()) {
            addProductPresenter.resultSaveProductFirestore(true, "")
        }
    }
}