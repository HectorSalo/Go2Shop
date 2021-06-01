package com.skysam.hchirinos.go2shop.common

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD

class GoToShop: Application() {
    companion object {
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
    }
}