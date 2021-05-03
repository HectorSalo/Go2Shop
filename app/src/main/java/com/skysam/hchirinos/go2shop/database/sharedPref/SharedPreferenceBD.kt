package com.skysam.hchirinos.go2shop.database.sharedPref

import android.content.Context
import android.content.SharedPreferences
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.GoToShop

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
object SharedPreferenceBD {
    private fun getInstance(uid: String): SharedPreferences {
        return GoToShop.GoToShop.getContext().getSharedPreferences(uid, Context.MODE_PRIVATE)
    }

    fun saveValue(uid: String, valorFloat: Float) {
        val editor = getInstance(uid).edit()
        editor.putFloat(Constants.SHARED_VALUE_WEB, valorFloat)
        editor.apply()
    }

    fun getValue(uid: String,): Float {
        return getInstance(uid).getFloat(Constants.SHARED_VALUE_WEB, 1f)
    }
}