package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.listsModule.interactor.EditListWishInteractor
import com.skysam.hchirinos.go2shop.listsModule.interactor.EditListWishInteractorClass
import com.skysam.hchirinos.go2shop.listsModule.ui.editListWish.EditListWishView

/**
 * Created by Hector Chirinos on 17/03/2021.
 */
class EditListWishPresenterClass(private val editListWishView: EditListWishView): EditListWishPresenter {
    private val editListWishInteractor: EditListWishInteractor = EditListWishInteractorClass(this)
    override fun editListWish(
        list: ListWish,
        productsToSave: MutableList<ProductsToListModel>,
        productsToUpdate: MutableList<ProductsToListModel>,
        productsToDelete: MutableList<ProductsToListModel>
    ) {
        editListWishInteractor.editListWish(list, productsToSave, productsToUpdate, productsToDelete)
    }


    override fun resultEditListWishFirestore(statusOk: Boolean, msg: String) {
        editListWishView.resultEditListWishFirestore(statusOk, msg)
    }
}