package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 2/5/2021.
 */
object ShopRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.SHOP)
    }

    private fun getInstanceProductsShop(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.PRODUCTS_TO_LIST_SHOP)
    }

    fun getShops(): Flow<List<Shop>> {
       return callbackFlow {
           val request = getInstance()
               .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
               .orderBy(Constants.DATE_CREATED, Query.Direction.DESCENDING)
               .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                   if (error != null) {
                       Log.w(ContentValues.TAG, "Listen failed.", error)
                       return@addSnapshotListener
                   }

                   val allProductsShop: MutableList<ProductsToListModel> = mutableListOf()
                   getInstanceProductsShop()
                       .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()?.uid)
                       .get()
                       .addOnSuccessListener { result ->
                           for (document in result) {
                               val product = ProductsToListModel(
                                   document.id,
                                   document.getString(Constants.NAME)!!,
                                   document.getString(Constants.UNIT)!!,
                                   document.getString(Constants.USER_ID)!!,
                                   document.getString(Constants.LIST_ID)!!,
                                   document.getDouble(Constants.PRICE)!!,
                                   document.getDouble(Constants.QUANTITY)!!
                               )
                               allProductsShop.add(product)
                           }

                           val shops: MutableList<Shop> = mutableListOf()
                           for (doc in value!!) {
                               val productsThisList: MutableList<ProductsToListModel> = mutableListOf()
                               for (product in allProductsShop) {
                                   if (product.listId == doc.id) {
                                       productsThisList.add(product)
                                   }
                               }

                               val dateCreated = doc.getDate(Constants.DATE_CREATED)!!.time
                               val shop = Shop(
                                   doc.id,
                                   doc.getString(Constants.NAME)!!,
                                   doc.getString(Constants.USER_ID)!!,
                                   productsThisList,
                                   doc.getDouble(Constants.TOTAL_LIST_WISH)!!,
                                   dateCreated,
                                   doc.getDouble(Constants.RATE_CHANGE)!!
                               )
                               shops.add(shop)
                           }
                           offer(shops)
                       }
               }
           awaitClose { request.remove() }
       }
    }

    fun saveShop(shop: Shop) {
        val date = Date(shop.dateCreated)
        val data = hashMapOf(
            Constants.NAME to shop.name,
            Constants.USER_ID to shop.userId,
            Constants.TOTAL_LIST_WISH to shop.total,
            Constants.DATE_CREATED to date,
            Constants.RATE_CHANGE to shop.rateChange
        )
        getInstance()
            .add(data)
            .addOnSuccessListener { doc ->
                saveProductsInList(shop, doc.id)
            }
    }

    private fun saveProductsInList(shop: Shop, id: String) {
        for (i in shop.listProducts.indices) {
            val data = hashMapOf(
                Constants.NAME to shop.listProducts[i].name,
                Constants.UNIT to shop.listProducts[i].unit,
                Constants.USER_ID to shop.listProducts[i].userId,
                Constants.LIST_ID to id,
                Constants.PRICE to shop.listProducts[i].price,
                Constants.QUANTITY to shop.listProducts[i].quantity
            )
            getInstanceProductsShop()
                .add(data)
                .addOnSuccessListener {
                    if (i == shop.listProducts.indices.last) {
                        updateDateEdited(id)
                    }
                }
        }
    }

    private fun updateDateEdited(id: String) {
        val calendar = Calendar.getInstance()
        getInstance()
            .document(id)
            .update(Constants.DATE_CREATED, calendar.time)
    }

    fun deleteShops() {
        getInstanceProductsShop()
            .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()?.uid)
            .get()
            .addOnSuccessListener { result->
                for (document in result) {
                    getInstanceProductsShop().document(document.id)
                        .delete()
                }

                getInstance()
                    .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
                    .get()
                    .addOnSuccessListener { result2->
                        for (document in result2) {
                            getInstance().document(document.id)
                                .delete()
                        }
                    }
            }
    }
}