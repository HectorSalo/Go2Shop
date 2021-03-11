package com.skysam.hchirinos.go2shop.database.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.go2shop.common.Constants

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

    fun getProductsFromListWish(idList: String): CollectionReference {
        return getInstance().collection(Constants.LIST_WISH).document(idList).collection(Constants.PRODUCTS_TO_LIST_WISH)
    }

    fun getShop(id: String): DocumentReference {
        return getInstance().collection(Constants.SHOP).document(id)
    }
}