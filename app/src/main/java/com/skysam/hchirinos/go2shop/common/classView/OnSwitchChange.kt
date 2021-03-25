package com.skysam.hchirinos.go2shop.common.classView

import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel

/**
 * Created by Hector Chirinos (Home) on 23/3/2021.
 */
interface OnSwitchChange {
    fun switchChange(isChecked: Boolean, product: ProductsToListModel?,
                     list: MutableList<ProductsToListModel>?, nameList: String?)
}