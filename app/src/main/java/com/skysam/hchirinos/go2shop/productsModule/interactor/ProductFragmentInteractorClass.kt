package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.common.Network
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductFragmentPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos (Home) on 10/3/2021.
 */
class ProductFragmentInteractorClass(private val productFragmentPresenter: ProductFragmentPresenter): ProductFragmentInteractor {

    override fun deleteProducts(products: MutableList<Product>) {
        for (i in products.indices) {
            FirestoreAPI.getProductById(products[i].id).delete()
                .addOnSuccessListener {
                    if (i == products.lastIndex) {
                        productFragmentPresenter.resultDeleteProducts(true, "")
                    }
                }
                .addOnFailureListener { e-> productFragmentPresenter.resultDeleteProducts(false, e.toString()) }
        }

        if (!Network.isAvailable()) {
            productFragmentPresenter.resultDeleteProducts(true, "")
        }
    }
}