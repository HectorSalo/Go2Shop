package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.interactor.AddProductInteractor
import com.skysam.hchirinos.go2shop.productsModule.interactor.AddProductInteractorClass
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductView

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
class AddProductPresenterClass(private val addProductView: AddProductView): AddProductPresenter {
    private val addProductInteractor: AddProductInteractor = AddProductInteractorClass(this)
    override fun saveProductToFirestore(product: Product) {
        addProductInteractor.saveProductToFirestore(product)
    }

    override fun resultSaveProductFirestore(statusOk: Boolean, msg: String) {
        addProductView.resultSaveProduct(statusOk, msg)
    }
}