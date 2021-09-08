package com.skysam.hchirinos.go2shop.comunicationAPI

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.skysam.hchirinos.go2shop.common.Constants

/**
 * Created by Hector Chirinos on 07/09/2021.
 */
object CloudMessaging {
    private fun getInstance(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    fun subscribeToMyTopic() {
        getInstance().subscribeToTopic(Constants.LISTS_SHARED)
            .addOnSuccessListener {
                Log.e("MSG OK", "subscribe")
            }
    }

    fun unsubscribeToMyTopic() {
        getInstance().unsubscribeFromTopic(Constants.LISTS_SHARED)
    }
}