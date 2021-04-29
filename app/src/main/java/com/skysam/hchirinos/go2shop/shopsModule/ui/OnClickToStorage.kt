package com.skysam.hchirinos.go2shop.shopsModule.ui

import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel

/**
 * Created by Hector Chirinos on 29/04/2021.
 */
interface OnClickToStorage {
    fun onClickAddToStorage(product: ProductsToShopModel)
    fun onClickRemoveToStorage(product: ProductsToShopModel)
}