package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.common.model.ProductModel
import com.skysam.hchirinos.go2shop.productsModule.interactor.AddProductInteractor
import com.skysam.hchirinos.go2shop.productsModule.interactor.AddProductInteractorClass
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductView

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
class AddProductPresenterClass(private val addProductView: AddProductView): AddProductPresenter {
    private val addProductInteractor: AddProductInteractor = AddProductInteractorClass(this)
    override fun saveProductToFirestore(product: ProductModel) {
        addProductInteractor.saveProductToFirestore(product)
    }

    override fun resultSaveProductFirestore(statusOk: Boolean, msg: String, product: ProductModel?) {
        if (statusOk) {
            addProductInteractor.saveProductToRoom(msg, product!!)
        } else {
            addProductView.resultSaveProduct(false, msg)
        }
    }

    override fun resultSaveProductFirestore() {
        addProductView.resultSaveProduct(true, "")
    }
}