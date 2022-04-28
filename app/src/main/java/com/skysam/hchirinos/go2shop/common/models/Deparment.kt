package com.skysam.hchirinos.go2shop.common.models

/**
 * Created by Hector Chirinos on 27/04/2022.
 */

data class Deparment(
    val id: String,
    var name: String,
    var userId: String,
    var isExtended: Boolean = false
)
