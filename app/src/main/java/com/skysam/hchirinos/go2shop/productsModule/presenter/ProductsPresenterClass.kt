package com.skysam.hchirinos.go2shop.productsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.interactor.ProductsInteractor
import com.skysam.hchirinos.go2shop.productsModule.interactor.ProductsInteractorClass
import com.skysam.hchirinos.go2shop.productsModule.ui.ProductsView

class ProductsPresenterClass(private val productsView: ProductsView): ProductsPresenter {
    private val productsInteractor: ProductsInteractor = ProductsInteractorClass(this)
    override fun getProducts() {
        productsInteractor.getProducts()
    }

    override fun resultGetProducts(products: MutableList<Product>) {
        productsView.resultGetProducts(products)
    }
}