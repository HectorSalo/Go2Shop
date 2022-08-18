package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.CurrentShop
import com.skysam.hchirinos.go2shop.common.models.ListShared
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

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

    /*fun getCurrentShop(): Flow<CurrentShop> {
        return callbackFlow {
            val request = getInstance()
                .document(AuthAPI.getCurrenUser()!!.uid)
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
                                prod[Constants.IS]
                            )
                            listProducts.add(product)
                        }
                    }
                }
                .whereEqualTo(Constants.USERS, AuthAPI.getCurrenUser()!!.uid)
                .orderBy(Constants.DATE_CREATED, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val listsWish: MutableList<ListShared> = mutableListOf()
                    for (doc in value!!){
                        val listProducts: MutableList<ProductsToListModel> = mutableListOf()
                        if (doc.get(Constants.PRODUCTOS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            val products = doc.data.getValue(Constants.PRODUCTOS) as ArrayList<HashMap<String, Any>>
                            for (prod in products) {
                                val product = ProductsToListModel(
                                    Constants.LIST_ID,
                                    prod[Constants.NAME].toString(),
                                    prod[Constants.UNIT].toString(),
                                    AuthAPI.getCurrenUser()!!.uid,
                                    doc.id,
                                    prod[Constants.PRICE].toString().toDouble(),
                                    prod[Constants.QUANTITY].toString().toDouble()
                                )
                                listProducts.add(product)
                            }
                        }

                        var listUsers = mutableListOf<String>()
                        if (doc.get(Constants.USERS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            listUsers = doc.get(Constants.USERS) as MutableList<String>
                        }

                        val dateCreated = doc.getDate(Constants.DATE_CREATED)!!.time
                        val listWish = ListShared(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            listUsers,
                            listProducts,
                            doc.getDouble(Constants.TOTAL_LIST_WISH)!!,
                            dateCreated,
                            doc.getString(Constants.USER_OWNER)!!
                        )
                        listsWish.add(listWish)
                    }
                    trySend(listsWish)
                }
            awaitClose { request.remove() }
        }
    }*/
}