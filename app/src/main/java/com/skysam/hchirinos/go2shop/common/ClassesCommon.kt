package com.skysam.hchirinos.go2shop.common

import java.util.*

/**
 * Created by Hector Chirinos (Home) on 23/9/2021.
 */
object ClassesCommon {
    fun convertFloatToString(value: Float): String {
        return String.format(Locale.GERMANY, "%,.2f", value)
    }

    fun convertDoubleToString(value: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", value)
    }

    fun convertStringToDouble(value: String): Double {
        val valueF = value.replace(".", "").replace(",", ".")
        return valueF.toDouble()
    }
}