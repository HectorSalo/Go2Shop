package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.common.models.Product
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 29/04/2021.
 */

@OptIn(ExperimentalCoroutinesApi::class)
object ProductsRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.PRODUCTOS)
    }

    fun getProducts(): Flow<List<Product>> {
        return callbackFlow {
            val request = getInstance()
                .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val products: MutableList<Product> = mutableListOf()
                    for (doc in value!!) {
                        val product = Product(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.UNIT)!!,
                            doc.getString(Constants.USER_ID)!!,
                            doc.getDouble(Constants.PRICE)!!,
                            doc.getDouble(Constants.QUANTITY)!!
                        )
                        products.add(product)
                    }
                    trySend(products)
                }
            awaitClose { request.remove() }
        }
    }

    fun addProduct(product: Product) {
        val data = hashMapOf(
            Constants.NAME to product.name,
            Constants.UNIT to product.unit,
            Constants.USER_ID to product.userId,
            Constants.PRICE to product.price,
            Constants.QUANTITY to product.quantity
        )
        getInstance().add(data)
    }

    fun editProduct(product: Product) {
        val data = hashMapOf(
            Constants.NAME to product.name,
            Constants.UNIT to product.unit,
            Constants.USER_ID to product.userId,
            Constants.PRICE to product.price,
            Constants.QUANTITY to product.quantity
        )
        getInstance().document(product.id)
            .set(data)
    }

    fun deleteProducts(products: MutableList<Product>) {
        for (i in products.indices) {
            getInstance().document(products[i].id)
                .delete()
        }
    }
}