package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
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
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    val products: MutableList<StorageModel> = mutableListOf()
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        trySend(products)
                        return@addSnapshotListener
                    }

                    for (doc in value!!) {
                        var price = 0.0
                        if (doc.getDouble(Constants.PRICE) != null){
                            price = doc.getDouble(Constants.PRICE)!!
                        }
                        val product = StorageModel(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.UNIT)!!,
                            doc.getString(Constants.USER_ID)!!,
                            doc.getDouble(Constants.QUANTITY)!!,
                            doc.getDate(Constants.DATE_CREATED)!!,
                            doc.getDouble(Constants.QUANTITY_REMAINING)!!,
                            price
                        )
                        products.add(product)
                    }
                    trySend(products)
                }
            awaitClose { request.remove() }
        }
    }

    fun saveProductsToStorge(products: MutableList<StorageModel>) {
        for (product in products) {
            val data = hashMapOf(
                Constants.NAME to product.name,
                Constants.UNIT to product.unit,
                Constants.USER_ID to product.userId,
                Constants.QUANTITY to product.quantityFromShop,
                Constants.DATE_CREATED to product.dateShop,
                Constants.QUANTITY_REMAINING to product.quantityRemaining,
                Constants.PRICE to product.price
            )
            getInstance()
                .add(data)
        }
    }

    fun updateProductsToStorage(products: MutableList<StorageModel>) {
        for (product in products) {
            val data: Map<String, Any> = hashMapOf(
                Constants.UNIT to product.unit,
                Constants.QUANTITY to product.quantityFromShop,
                Constants.DATE_CREATED to product.dateShop,
                Constants.QUANTITY_REMAINING to product.quantityRemaining,
                Constants.PRICE to product.price
            )
            getInstance()
                .document(product.id)
                .update(data)
        }
    }

    fun updateUnitFromProductToStorage(product: StorageModel) {
        getInstance()
            .document(product.id)
            .update(Constants.QUANTITY_REMAINING, product.quantityRemaining)
    }

    fun editProductToStorage(product: StorageModel) {
        val data: Map<String, Any> = hashMapOf(
            Constants.NAME to product.name,
            Constants.UNIT to product.unit
        )
        getInstance()
            .document(product.id)
            .update(data)
    }

    fun deleteProductToStorage(product: StorageModel) {
        getInstance().document(product.id)
            .delete()
    }
}