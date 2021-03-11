package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.interactor.EditProductInteractor
import com.skysam.hchirinos.go2shop.productsModule.interactor.EditProductInteractorClass
import com.skysam.hchirinos.go2shop.productsModule.ui.EditProductView

/**
 * Created by Hector Chirinos on 11/03/2021.
 */
class EditProductPresenterClass(private val editProductView: EditProductView): EditProductPresenter {
    private val editProductInteractor: EditProductInteractor = EditProductInteractorClass(this)
    override fun editToFirestore(product: Product) {
        editProductInteractor.editToFirestore(product)
    }

    override fun resultEditToFirestore(statusOk: Boolean, msg: String) {
        editProductView.resultEditToFirestore(statusOk, msg)
    }
}