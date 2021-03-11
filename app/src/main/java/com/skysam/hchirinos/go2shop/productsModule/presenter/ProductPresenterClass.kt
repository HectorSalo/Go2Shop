package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.interactor.ProductInteractor
import com.skysam.hchirinos.go2shop.productsModule.interactor.ProductInteractorClass
import com.skysam.hchirinos.go2shop.productsModule.ui.ProductView

/**
 * Created by Hector Chirinos (Home) on 10/3/2021.
 */
class ProductPresenterClass(private val productView: ProductView): ProductPresenter {
    private val productInteractor: ProductInteractor = ProductInteractorClass(this)
    override fun getProducts() {
        productInteractor.getProducts()
    }

    override fun resultGetProducts(products: MutableList<Product>) {
        productView.resultGetProducts(products)
    }
}