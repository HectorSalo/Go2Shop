package com.skysam.hchirinos.go2shop.shopsModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import kotlinx.coroutines.launch

class AddListShopViewModel : ViewModel() {
    private var productToScroll: ProductsToShopModel? = null
    private val _isProductInList = MutableLiveData<Boolean>()
    val isProductInList: LiveData<Boolean> get() = _isProductInList

    private val _positionProductInList = MutableLiveData<Int>()
    val positionProductInList: LiveData<Int> get() = _positionProductInList

    private val _totalPrice = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val totalPrice: LiveData<Double> get() = _totalPrice

    private val _allProducts = MutableLiveData<MutableList<ProductsToShopModel>>().apply { value = mutableListOf() }
    val allProducts: LiveData<MutableList<ProductsToShopModel>> get() = _allProducts

    private val listProducts = MutableLiveData<MutableList<Product>>().apply {
        viewModelScope.launch {
            value = RoomDB.getInstance().product().getAll()
        }
    }

    fun getProducts(): LiveData<MutableList<Product>> {
        return listProducts
    }

    fun addProductToList(product: ProductsToShopModel) {
        var exists = false
        var position = -1
        for (i in _allProducts.value!!.indices) {
            if (_allProducts.value!![i].name == product.name) {
                exists = true
                position = i
            }
        }
        if (exists) {
            _isProductInList.value = exists
            _positionProductInList.value = position
        } else {
            productToScroll = product
            _isProductInList.value = exists
            _allProducts.value!!.add(product)
            _allProducts.value = _allProducts.value!!.sortedWith(compareBy { it.name }).toMutableList()
            _allProducts.value = _allProducts.value
        }
    }

    fun updateProductToList(product: ProductsToShopModel, position: Int) {
        _allProducts.value!![position] = product
        _allProducts.value = _allProducts.value
        productToScroll = product
    }

    fun checkedProduct(product: ProductsToShopModel) {
        _totalPrice.value = _totalPrice.value!! + product.quantity * product.price
    }

    fun uncheckedProduct(product: ProductsToShopModel) {
        _totalPrice.value = _totalPrice.value!! - (product.quantity * product.price)
    }

    fun fillListFirst(products: MutableList<ProductsToListModel>) {
        val list: MutableList<ProductsToShopModel> = mutableListOf()
        for (i in products.indices) {
            val productToModel = ProductsToShopModel(
                products[i].id,
                products[i].name,
                products[i].unit,
                products[i].userId,
                products[i].listId,
                products[i].price,
                products[i].quantity
            )
            list.add(productToModel)
        }
        _allProducts.value = list.sortedWith(compareBy { it.name }).toMutableList()
        _allProducts.value = _allProducts.value
    }

    fun scrollToPosition() {
        _positionProductInList.value = _allProducts.value!!.indexOf(productToScroll)
    }

    fun updatedPrice(priceOld: Double, priceNew: Double) {
        _totalPrice.value = _totalPrice.value!! + priceNew - priceOld
    }

}