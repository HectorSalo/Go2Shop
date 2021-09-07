package com.skysam.hchirinos.go2shop.common

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD

class GoToShop: Application() {
    companion object {
        private lateinit var mRequestQueue: RequestQueue
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        when (SharedPreferenceBD.getTheme()) {
            Constants.PREFERENCE_THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            Constants.PREFERENCE_THEME_DARK -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES)
            Constants.PREFERENCE_THEME_LIGTH -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    object GoToShop {
        fun getContext(): Context {
            return appContext
        }

        private fun getmRequestQueue(): RequestQueue {
            mRequestQueue = Volley.newRequestQueue(appContext)
            return mRequestQueue
        }

        fun <T> addToReqQueue(request: Request<T>) {
            request.retryPolicy =
                DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            getmRequestQueue().add(request)
        }
    }
}