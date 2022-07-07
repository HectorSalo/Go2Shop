package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 02/09/2021.
 */

object UsersRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.USERS)
    }

    fun addUser(user: User) {
        val data = hashMapOf(
            Constants.NAME to user.name,
            Constants.EMAIL to user.email
        )
        getInstance()
            .document(user.id)
            .set(data)
    }

    fun getUsers(): Flow<MutableList<User>> {
        return callbackFlow {
            val request = getInstance()
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val users = mutableListOf<User>()
                    for (doc in value!!) {
                        val user = User(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.EMAIL)!!
                        )
                        users.add(user)
                    }
                    trySend(users)
                }
            awaitClose { request.remove() }
        }
    }
}