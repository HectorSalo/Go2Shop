package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.listsModule.interactor.ListWishInteractor
import com.skysam.hchirinos.go2shop.listsModule.interactor.ListWishInteractorClass
import com.skysam.hchirinos.go2shop.listsModule.ui.ListWishView

class ListWishPresenterClass(private val listWishView: ListWishView): ListWishPresenter {
    private val listWishInteractor: ListWishInteractor = ListWishInteractorClass(this)
    override fun getProducts() {
        listWishInteractor.getProducts()
    }

    override fun resultGetProducts(products: List<Product>) {
        val productsName = mutableListOf<String>()
        for (i in products.indices) {
            productsName.add(i, products[i].name)
        }
        listWishView.resultGetProducts(productsName)
    }
}