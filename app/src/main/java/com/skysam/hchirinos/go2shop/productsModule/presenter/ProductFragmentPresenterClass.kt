package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.interactor.ProductFragmentInteractor
import com.skysam.hchirinos.go2shop.productsModule.interactor.ProductFragmentInteractorClass
import com.skysam.hchirinos.go2shop.productsModule.ui.ProductFragmentView

/**
 * Created by Hector Chirinos (Home) on 10/3/2021.
 */
class ProductFragmentPresenterClass(private val productFragmentView: ProductFragmentView): ProductFragmentPresenter {
    private val productFragmentInteractor: ProductFragmentInteractor = ProductFragmentInteractorClass(this)

    override fun deleteProducts(products: MutableList<Product>) {
        productFragmentInteractor.deleteProducts(products)
    }

    override fun resultDeleteProducts(statusOk: Boolean, msg: String) {
        productFragmentView.resultDeleteProducts(statusOk, msg)
    }
}