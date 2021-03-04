package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.common.model.ProductModel
import com.skysam.hchirinos.go2shop.productsModule.interactor.AddProductInteractor
import com.skysam.hchirinos.go2shop.productsModule.interactor.AddProductInteractorClass
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductView

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
class AddProductPresenterClass(val addProductView: AddProductView): AddProductPresenter {
    private val addProductInteractor: AddProductInteractor = AddProductInteractorClass(this)
    override fun addProduct(product: ProductModel) {
        addProductInteractor.addProduct(product)
    }
}