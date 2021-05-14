package com.skysam.hchirinos.go2shop.storageModule.ui

import com.skysam.hchirinos.go2shop.common.models.StorageModel

/**
 * Created by Hector Chirinos (Home) on 13/5/2021.
 */
interface OnClickRemoveQuantity {
    fun remove(product: StorageModel)
}