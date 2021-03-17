package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 17/03/2021.
 */
interface EditListWishPresenter {
    fun editListWish(list: ListWish, productsToSave: MutableList<ProductsToListModel>,
                     productsToUpdate: MutableList<ProductsToListModel>, productsToDelete: MutableList<ProductsToListModel>)

    fun resultEditListWishFirestore(statusOk: Boolean, msg: String)
}