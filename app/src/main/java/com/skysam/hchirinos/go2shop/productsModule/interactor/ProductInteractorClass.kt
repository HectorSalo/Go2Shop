package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.common.Network
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos (Home) on 10/3/2021.
 */
class ProductInteractorClass(private val productPresenter: ProductPresenter): ProductInteractor, CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    override fun getProducts() {
        launch {
            val products = RoomDB.getInstance().product()
                .getAll()
            productPresenter.resultGetProducts(products)
        }
    }

    override fun deleteProducts(products: MutableList<Product>) {
        for (i in products.indices) {
            FirestoreAPI.getProductById(products[i].id).delete()
                .addOnSuccessListener {
                    if (i == products.lastIndex) {
                        productPresenter.resultDeleteProducts(true, "")
                    }
                }
                .addOnFailureListener { e-> productPresenter.resultDeleteProducts(false, e.toString()) }
        }

        if (!Network.isAvailable()) {
            productPresenter.resultDeleteProducts(true, "")
        }
    }
}