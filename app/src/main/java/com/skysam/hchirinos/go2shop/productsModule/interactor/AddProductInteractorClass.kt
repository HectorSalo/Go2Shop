package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.common.model.ProductModel
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.productsModule.presenter.AddProductPresenter

/**
 * Created by Hector Chirinos on 04/03/2021.
 */
class AddProductInteractorClass(val addProductPresenter: AddProductPresenter): AddProductInteractor {
    override fun addProduct(product: ProductModel) {

    }
}