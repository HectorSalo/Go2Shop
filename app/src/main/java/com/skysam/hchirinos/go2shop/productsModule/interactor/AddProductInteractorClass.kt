package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.model.ProductModel
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.presenter.AddProductPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
class AddProductInteractorClass(private val addProductPresenter: AddProductPresenter): AddProductInteractor, CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun saveProductToFirestore(product: ProductModel) {
        val data = hashMapOf(
            Constants.NAME to product.name,
            Constants.UNIT to product.unit,
            Constants.USER_ID to product.userId,
            Constants.PRICE to product.price,
            Constants.QUANTITY to product.quantity
        )
        FirestoreAPI.getProducts().add(data)
            .addOnSuccessListener { doc->
                addProductPresenter.resultSaveProductFirestore(true, doc.id, product)
            }
            .addOnFailureListener { e->
                addProductPresenter.resultSaveProductFirestore(false, e.toString(), null)
            }
    }

    override fun saveProductToRoom(id: String, product: ProductModel) {
        val productDB = Product(
            id,
            product.name,
            product.unit
        )
        launch {
            RoomDB.getInstance().product().insert(productDB)
            addProductPresenter.resultSaveProductFirestore()
        }
    }
}