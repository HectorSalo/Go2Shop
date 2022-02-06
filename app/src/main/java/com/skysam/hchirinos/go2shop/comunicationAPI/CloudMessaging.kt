package com.skysam.hchirinos.go2shop.comunicationAPI

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.GoToShop

/**
 * Created by Hector Chirinos on 07/09/2021.
 */
object CloudMessaging {
    private fun getInstance(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    fun subscribeTopicMessagingForAll() {
        getInstance().subscribeToTopic(Constants.MESSAGING_FOR_ALL)
            .addOnSuccessListener {
                Log.e("MSG OK", "subscribe")
            }
    }

    fun unsubscribeTopicMessagingForAll() {
        getInstance().unsubscribeFromTopic(Constants.MESSAGING_FOR_ALL)
    }

    fun subscribeTopicMessagingForUser() {
        getInstance().subscribeToTopic(GoToShop.GoToShop.getContext()
            .getString(R.string.topic_notification_user_id, AuthAPI.getCurrenUser()?.uid))
            .addOnSuccessListener {
                Log.e("MSG OK", "subscribe")
            }
            .addOnFailureListener { Log.e("MSG FAILED", it.message!!) }
    }

    fun unsubscribeTopicMessagingForUser() {
        getInstance().unsubscribeFromTopic(GoToShop.GoToShop.getContext()
            .getString(R.string.topic_notification_user_id, AuthAPI.getCurrenUser()?.uid))
    }
}