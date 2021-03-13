package com.skysam.hchirinos.go2shop.productsModule.interactor

import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductsPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProductsInteractorClass(private val productsPresenter: ProductsPresenter) : ProductsInteractor, CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun getProducts() {
        launch {
            val products = RoomDB.getInstance().product()
                .getAll()
            productsPresenter.resultGetProducts(products)
        }
    }

}
