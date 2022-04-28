package com.skysam.hchirinos.go2shop.deparments

import com.skysam.hchirinos.go2shop.common.models.Deparment

/**
 * Created by Hector Chirinos on 27/04/2022.
 */

interface OnClick {
 fun edit(deparment: Deparment)
 fun delete(deparment: Deparment)
}