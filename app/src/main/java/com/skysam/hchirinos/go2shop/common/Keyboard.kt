package com.skysam.hchirinos.go2shop.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object Keyboard {
    fun close(view: View) {
        val imn = GoToShop.GoToShop.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imn.hideSoftInputFromWindow(view.windowToken, 0)
    }
}