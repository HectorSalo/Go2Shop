package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 03/05/2021.
 */
object StorageRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.STORAGE)
    }

    fun getProductsFromStorage(): Flow<List<StorageModel>> {
        return callbackFlow {
            val request = getInstance()
                .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
                .orderBy(Constants.NAME, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    val products: MutableList<StorageModel> = mutableListOf()
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        offer(products)
                        return@addSnapshotListener
                    }

                    for (doc in value!!) {
                        val product = StorageModel(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.UNIT)!!,
                            doc.getString(Constants.USER_ID)!!,
                            doc.getDouble(Constants.QUANTITY)!!,
                            doc.getDate(Constants.DATE_CREATED)!!,
                            doc.getDouble(Constants.QUANTITY_REMAINING)!!
                        )
                        products.add(product)
                    }
                    offer(products)
                }
            awaitClose { request.remove() }
        }
    }

    fun saveProductsToStorge(products: MutableList<StorageModel>) {
        for (product in products) {
            val data = hashMapOf(
                Constants.NAME to product.name,
                Constants.USER_ID to product.userId,
                Constants.TOTAL_LIST_WISH to shop.total,
                Constants.DATE_CREATED to date,
                Constants.RATE_CHANGE to shop.rateChange
            )
            ShopRepository.getInstance()
                .add(data)
                .addOnSuccessListener { doc ->
                    ShopRepository.saveProductsInList(shop, doc.id)
                }
        }
    }

    fun updateProductsToStorage(products: MutableList<StorageModel>) {

    }
}