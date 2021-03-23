package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductsPresenter
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductsPresenterClass
import com.skysam.hchirinos.go2shop.productsModule.ui.ProductsView
import kotlinx.coroutines.launch

class AddListShopViewModel : ViewModel() {
    private val productsSelectd: MutableList<Product> = mutableListOf()

    private var productsToAdd = MutableLiveData<MutableList<Product>>().apply {
        value = productsSelectd
    }

    private val listProducts = MutableLiveData<MutableList<Product>>().apply {
        viewModelScope.launch {
            value = RoomDB.getInstance().product().getAll()
        }
    }

    fun getProducts(): LiveData<MutableList<Product>> {
        return listProducts
    }

    fun getProductsSelected(): LiveData<MutableList<Product>> {
        return productsToAdd
    }

    fun addProductToList(product: Product) {
        productsSelectd.add(product)
    }
}