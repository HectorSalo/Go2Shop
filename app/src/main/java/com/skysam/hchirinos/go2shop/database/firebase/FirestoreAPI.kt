package com.skysam.hchirinos.go2shop.database.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.classView.ProductSaveFromList
import com.skysam.hchirinos.go2shop.common.classView.ProductsSavedToList
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
object FirestoreAPI {
    private fun getInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun getDataFromUser(uid: String): DocumentReference {
        return getInstance().collection(Constants.USERS).document(uid)
    }

    fun getProducts(): CollectionReference {
        return getInstance().collection(Constants.PRODUCTOS)
    }

    fun getProductById(id: String): DocumentReference {
        return getInstance().collection(Constants.PRODUCTOS).document(id)
    }

    fun getListWish(): CollectionReference {
        return getInstance().collection(Constants.LIST_WISH)
    }

    fun getProductsFromListWish(): CollectionReference {
        return getInstance().collection(Constants.PRODUCTS_TO_LIST_WISH)
    }

    fun getShop(id: String): DocumentReference {
        return getInstance().collection(Constants.SHOP).document(id)
    }

    fun getProductsToListWishFromFirestore(id: String, list: ListWish, productsSavedToList: ProductsSavedToList) {
        getProductsFromListWish()
            .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
            .whereEqualTo(Constants.LIST_ID, id)
            .get()
            .addOnSuccessListener { result ->
                val listProducts: MutableList<ProductsToListModel> = mutableListOf()
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
                    listProducts.add(product)
                }
                val listWishToAdd = ListWish(
                    id,
                    list.name,
                    list.userId,
                    listProducts,
                    list.total,
                    list.dateCreated,
                    list.lastEdited
                )
                productsSavedToList.saved(listWishToAdd)
            }
    }
}