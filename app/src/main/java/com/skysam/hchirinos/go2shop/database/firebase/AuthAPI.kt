package com.skysam.hchirinos.go2shop.database.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
object AuthAPI {
    fun getCurrenUser(): FirebaseUser? {
        val mAuth = FirebaseAuth.getInstance()
        return mAuth.currentUser
    }
}