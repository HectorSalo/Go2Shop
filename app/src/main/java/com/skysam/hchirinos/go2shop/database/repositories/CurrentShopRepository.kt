package com.skysam.hchirinos.go2shop.database.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.CurrentShop
import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 17/08/2022.
 */

object CurrentShopRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.CURRENT_SHOP)
    }

    fun saveCurrentShop(currentShop: CurrentShop) {
        val data = hashMapOf(
            Constants.NAME to currentShop.name,
            Constants.USER_ID to AuthAPI.getCurrenUser()!!.uid,
            Constants.PRODUCTOS to currentShop.listProducts,
            Constants.TOTAL to currentShop.total,
            Constants.DATE_CREATED to currentShop.dateCreated,
            Constants.RATE_CHANGE to currentShop.rateChange
        )
        getInstance()
            .document(AuthAPI.getCurrenUser()!!.uid)
            .set(data)
    }

    fun updateCurrentShop(products: MutableList<ProductsToShopModel>, total: Double) {
        getInstance().document(AuthAPI.getCurrenUser()!!.uid)
            .update(Constants.PRODUCTOS, products, Constants.TOTAL, total)
    }

    fun deleteCurrentShop() {
        getInstance().document(AuthAPI.getCurrenUser()!!.uid)
            .delete()
    }

    fun getCurrentShop(): Flow<CurrentShop?> {
        return callbackFlow {
            getInstance()
                //.document(AuthAPI.getCurrenUser()!!.uid)
                .document("tVULV16vK2aP3ZMutG8ymLBnBXm1")
                .get()
                .addOnSuccessListener {
                    val listProducts: MutableList<ProductsToShopModel> = mutableListOf()
                    if (it.get(Constants.PRODUCTOS) != null) {
                        @Suppress("UNCHECKED_CAST")
                        val products = it.data?.getValue(Constants.PRODUCTOS) as ArrayList<HashMap<String, Any>>
                        for (prod in products) {
                            val product = ProductsToShopModel(
                                Constants.LIST_ID,
                                prod[Constants.NAME].toString(),
                                prod[Constants.UNIT].toString(),
                                AuthAPI.getCurrenUser()!!.uid,
                                prod[Constants.LIST_ID].toString(),
                                prod[Constants.PRICE].toString().toDouble(),
                                prod[Constants.QUANTITY].toString().toDouble(),
                                prod[Constants.CHECKED_TO_SHOP] as Boolean,
                                prod[Constants.CHECKED_TO_STORAGE] as Boolean,
                                prod[Constants.DEPARMENT].toString()
                            )
                            listProducts.add(product)
                        }
                    }
                    if (it.getString(Constants.NAME) != null) {
                        val currentShop = CurrentShop(
                            it.id,
                            it.getString(Constants.NAME)!!,
                            it.getString(Constants.USER_ID)!!,
                            listProducts,
                            it.getDouble(Constants.TOTAL)!!,
                            it.getDate(Constants.DATE_CREATED)!!,
                            it.getDouble(Constants.RATE_CHANGE)!!
                        )
                        trySend(currentShop)
                    } else {
                        trySend(null)
                    }
                }
                .addOnFailureListener {
                    trySend(null)
                }
            awaitClose { }
        }
    }
}