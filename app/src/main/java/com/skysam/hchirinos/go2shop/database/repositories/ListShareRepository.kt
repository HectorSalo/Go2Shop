package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.ListShared
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.comunicationAPI.EventErrorTypeListener
import com.skysam.hchirinos.go2shop.comunicationAPI.NotificationAPI
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos on 06/09/2021.
 */

object ListShareRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.LISTS_SHARED)
    }

    fun getListsShared(): Flow<List<ListShared>> {
        return callbackFlow {
            val request = getInstance()
                .whereArrayContains(Constants.USERS, AuthAPI.getCurrenUser()!!.uid)
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
    }

    fun addListShared(list: ListShared) {
        val date = Date(list.dateCreated)
        val data = hashMapOf(
            Constants.NAME to list.name,
            Constants.USERS to list.usersId,
            Constants.PRODUCTOS to list.listProducts,
            Constants.TOTAL_LIST_WISH to list.total,
            Constants.DATE_CREATED to date,
            Constants.USER_OWNER to list.userOwner
        )
        getInstance()
            .add(data)
            .addOnSuccessListener {
                for (user in list.usersId) {
                    NotificationAPI.sendNotification(
                        AuthAPI.getCurrenUser()?.displayName!!,
                        "Te he enviado una lista",
                        user,
                        AuthAPI.getCurrenUser()?.email!!,
                        object : EventErrorTypeListener {
                            override fun onError(typeEvent: Int, reaMsg: Int) {

                            }
                        }
                    )
                }
            }
    }

    fun updateListShared(list: ListShared) {
        val date = Date(list.dateCreated)
        val data = hashMapOf(
            Constants.NAME to list.name,
            Constants.USERS to list.usersId,
            Constants.PRODUCTOS to list.listProducts,
            Constants.TOTAL_LIST_WISH to list.total,
            Constants.DATE_CREATED to date,
            Constants.USER_OWNER to list.userOwner
        )

        getInstance().document(list.id)
            .update(data)
    }

    fun deleteList(list: ListShared) {
        getInstance().document(list.id)
            .delete()
    }
}