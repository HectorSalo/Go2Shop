package com.skysam.hchirinos.go2shop.storageModule.ui

import com.skysam.hchirinos.go2shop.common.models.StorageModel

/**
 * Created by Hector Chirinos (Home) on 13/5/2021.
 */
interface OnClick {
    fun viewDetails(product: StorageModel)
    fun remove(product: StorageModel)
    fun add(product: StorageModel)
    fun editProduct(product: StorageModel)
    fun deleteProduct(product: StorageModel)
}