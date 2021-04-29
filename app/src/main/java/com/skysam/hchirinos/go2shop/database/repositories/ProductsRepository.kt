package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 29/04/2021.
 */
object ProductsRepository {
    private fun getInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun getProducts(): Flow<List<Product>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.PRODUCTOS)
                .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
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
                    offer(products)
                }
            awaitClose { request.remove() }
        }
    }
}