package com.skysam.hchirinos.go2shop.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.Deparment
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 27/04/2022.
 */

object DeparmentsRepository {
 private fun getInstance(): CollectionReference {
  return FirebaseFirestore.getInstance().collection(Constants.DEPARMENTS)
 }

 fun getDeparments(): Flow<List<Deparment>> {
  return callbackFlow {
   val request = getInstance()
    .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
    .orderBy(Constants.NAME, Query.Direction.ASCENDING)
    .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
     if (error != null) {
      Log.w(ContentValues.TAG, "Listen failed.", error)
      return@addSnapshotListener
     }

     val deparments: MutableList<Deparment> = mutableListOf()
     for (doc in value!!) {
      val deparment = Deparment(
       doc.id,
       doc.getString(Constants.NAME)!!,
       doc.getString(Constants.USER_ID)!!
      )
      deparments.add(deparment)
     }
     trySend(deparments)
    }
   awaitClose { request.remove() }
  }
 }

 fun addDeparment(deparment: Deparment) {
  val data = hashMapOf(
   Constants.NAME to deparment.name,
   Constants.USER_ID to deparment.userId
  )
  getInstance().add(data)
 }

 fun editDeparment(deparment: Deparment) {
  val data = hashMapOf(
   Constants.NAME to deparment.name,
   Constants.USER_ID to deparment.userId
  )
  getInstance().document(deparment.id)
   .update(data as Map<String, Any>)
 }

 fun deleteDeparment(deparment: Deparment) {
  getInstance().document(deparment.id)
   .delete()
 }
}