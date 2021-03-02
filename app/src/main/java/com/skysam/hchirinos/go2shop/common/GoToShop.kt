package com.skysam.hchirinos.go2shop.common

import android.app.Application
import android.content.Context

class GoToShop: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    object GoToShop {
        fun getContext(): Context {
            return appContext
        }
    }
}