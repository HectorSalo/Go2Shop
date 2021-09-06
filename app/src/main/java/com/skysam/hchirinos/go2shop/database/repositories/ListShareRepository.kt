package com.skysam.hchirinos.go2shop.database.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.User
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 06/09/2021.
 */
object ListShareRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.LISTS_SHARED)
    }

    fun addListToShare(user: User, list: MutableList<ListWish>) {

    }
}