package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.*
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos on 30/04/2021.
 */
object ListWishRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.LIST_WISH)
    }

    private fun getInstanceProductsListsWish(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.PRODUCTS_TO_LIST_WISH)
    }


    fun getListsWish(): Flow<List<ListWish>> {
        return callbackFlow {
            val request = getInstance()
                .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
                .orderBy(Constants.DATE_CREATED, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val allProductsListsWish: MutableList<ProductsToListModel> = mutableListOf()
                    getInstanceProductsListsWish()
                        .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
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
                                allProductsListsWish.add(product)
                            }


                            val listsWish: MutableList<ListWish> = mutableListOf()
                            for (doc in value!!){
                                val productsThisList: MutableList<ProductsToListModel> = mutableListOf()
                                for (product in allProductsListsWish) {
                                    if (product.listId == doc.id) {
                                        productsThisList.add(product)
                                    }
                                }

                                val dateCreated = doc.getDate(Constants.DATE_CREATED)!!.time
                                val dateEdited = doc.getDate(Constants.DATE_LAST_EDITED)!!.time
                                val listWish = ListWish(
                                    doc.id,
                                    doc.getString(Constants.NAME)!!,
                                    doc.getString(Constants.USER_ID)!!,
                                    productsThisList,
                                    doc.getDouble(Constants.TOTAL_LIST_WISH)!!,
                                    dateCreated,
                                    dateEdited
                                )
                                listsWish.add(listWish)
                            }
                            offer(listsWish)
                        }
                }
            awaitClose { request.remove() }
        }
    }

    fun editListWish(list: ListWish, productsToSave: MutableList<ProductsToListModel>,
                     productsToUpdate: MutableList<ProductsToListModel>, productsToDelete: MutableList<ProductsToListModel>) {
        saveProducts(list, productsToSave, productsToUpdate, productsToDelete)
    }

    private fun saveProducts(list: ListWish, productsToSave: MutableList<ProductsToListModel>,
                             productsToUpdate: MutableList<ProductsToListModel>,
                             productsToDelete: MutableList<ProductsToListModel>) {
        if (productsToSave.isNotEmpty()) {
            for (i in productsToSave.indices) {
                val data = hashMapOf(
                    Constants.NAME to productsToSave[i].name,
                    Constants.UNIT to productsToSave[i].unit,
                    Constants.USER_ID to productsToSave[i].userId,
                    Constants.LIST_ID to list.id,
                    Constants.PRICE to productsToSave[i].price,
                    Constants.QUANTITY to productsToSave[i].quantity
                )

                getInstanceProductsListsWish()
                    .add(data)
                    .addOnSuccessListener {
                        if (i == productsToSave.lastIndex) {
                            updateProducts(list, productsToUpdate, productsToDelete)
                        }
                    }
            }
        } else {
            updateProducts(list, productsToUpdate, productsToDelete)
        }
    }

    private fun updateProducts(list: ListWish,
                               productsToUpdate: MutableList<ProductsToListModel>,
                               productsToDelete: MutableList<ProductsToListModel>
    ) {
        if (productsToUpdate.isNotEmpty()) {
            for (i in productsToUpdate.indices) {
                val data = hashMapOf(
                    Constants.NAME to productsToUpdate[i].name,
                    Constants.UNIT to productsToUpdate[i].unit,
                    Constants.USER_ID to productsToUpdate[i].userId,
                    Constants.LIST_ID to list.id,
                    Constants.PRICE to productsToUpdate[i].price,
                    Constants.QUANTITY to productsToUpdate[i].quantity
                )

                getInstanceProductsListsWish()
                    .document(productsToUpdate[i].id)
                    .set(data)
                    .addOnSuccessListener {
                        if (i == productsToUpdate.lastIndex) {
                            deleteProducts(list, productsToDelete)
                        }
                    }
            }
        } else {
            deleteProducts(list, productsToDelete)
        }
    }

    private fun deleteProducts(list: ListWish, productsToDelete: MutableList<ProductsToListModel>) {
        if (productsToDelete.isNotEmpty()) {
            for (i in productsToDelete.indices) {
                getInstanceProductsListsWish()
                    .document(productsToDelete[i].id)
                    .delete()
                    .addOnSuccessListener {
                        if (i == productsToDelete.lastIndex) {
                            updateListWish(list)
                        }
                    }
            }
        } else {
            updateListWish(list)
        }
    }

    fun addListWish(list: ListWish) {
        val date = Date(list.dateCreated)
        val data = hashMapOf(
            Constants.NAME to list.name,
            Constants.USER_ID to list.userId,
            Constants.TOTAL_LIST_WISH to list.total,
            Constants.DATE_CREATED to date,
            Constants.DATE_LAST_EDITED to date
        )
        getInstance()
            .add(data)
            .addOnSuccessListener { doc ->
                saveProductsInList(list, doc.id)
            }
    }

    private fun saveProductsInList(list: ListWish, id: String) {
        for (i in list.listProducts.indices) {
            val data = hashMapOf(
                Constants.NAME to list.listProducts[i].name,
                Constants.UNIT to list.listProducts[i].unit,
                Constants.USER_ID to list.listProducts[i].userId,
                Constants.LIST_ID to id,
                Constants.PRICE to list.listProducts[i].price,
                Constants.QUANTITY to list.listProducts[i].quantity
            )
            getInstanceProductsListsWish()
                .add(data)
                .addOnSuccessListener {
                    if (i == list.listProducts.indices.last) {
                        updateDateEdited(id)
                    }
                }
        }
    }

    private fun updateDateEdited(id: String) {
        val calendar = Calendar.getInstance()
        getInstance()
            .document(id)
            .update(Constants.DATE_LAST_EDITED, calendar.time)
    }

    private fun updateListWish(list: ListWish) {
        val dateCreated = Date(list.dateCreated)
        val dateEdited = Date(list.lastEdited)
        val data = hashMapOf(
            Constants.NAME to list.name,
            Constants.USER_ID to list.userId,
            Constants.TOTAL_LIST_WISH to list.total,
            Constants.DATE_CREATED to dateCreated,
            Constants.DATE_LAST_EDITED to dateEdited
        )

        getInstance().document(list.id)
            .set(data)
    }

    fun deleteLists(lists: MutableList<ListWish>) {
        for (list in lists) {
            for (product in list.listProducts) {
                getInstanceProductsListsWish()
                    .document(product.id)
                    .delete()
            }
        }
        deleteData(lists)
    }

    private fun deleteData(lists: MutableList<ListWish>) {
        for (list in lists) {
            getInstance()
                .document(list.id)
                .delete()
        }
    }
}