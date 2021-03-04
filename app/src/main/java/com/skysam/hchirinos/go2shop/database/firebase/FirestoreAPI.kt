package com.skysam.hchirinos.go2shop.database.firebase

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

    fun getDataFromUserReference(uid: String): DocumentReference {
        return getInstance().collection(Constants.USERS).document(uid)
    }

    fun getProductReference(id: String): DocumentReference {
        return getInstance().collection(Constants.PRODUCTOS).document(id)
    }

    fun getListWishReference(id: String): DocumentReference {
        return getInstance().collection(Constants.LIST_WISH).document(id)
    }

    fun getShopReference(id: String): DocumentReference {
        return getInstance().collection(Constants.SHOP).document(id)
    }
}