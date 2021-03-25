package com.skysam.hchirinos.go2shop.shopsModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import kotlinx.coroutines.launch

class AddListShopViewModel : ViewModel() {
    private val _isProductInList = MutableLiveData<Boolean>()
    val isProductInList: LiveData<Boolean> get() = _isProductInList

    private val _positionProducInList = MutableLiveData<Int>()
    val positionProductInList: LiveData<Int> get() = _positionProducInList

    private val _totalPrice = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val totalPrice: LiveData<Double> get() = _totalPrice

    private val _productsInList = MutableLiveData<MutableList<ProductsToListModel>>().apply { value = mutableListOf() }
    val productsInList: LiveData<MutableList<ProductsToListModel>> get() = _productsInList

    private val _productsToShop = MutableLiveData<MutableList<ProductsToListModel>>().apply { value = mutableListOf() }
    val productsToShop: LiveData<MutableList<ProductsToListModel>> get() = _productsToShop

    private val listProducts = MutableLiveData<MutableList<Product>>().apply {
        viewModelScope.launch {
            value = RoomDB.getInstance().product().getAll()
        }
    }

    fun getProducts(): LiveData<MutableList<Product>> {
        return listProducts
    }

    fun addProductToList(product: ProductsToListModel) {
        var exists = false
        var position = -1
        for (i in _productsInList.value!!.indices) {
            if (productsInList.value!![i].name == product.name) {
                exists = true
                position = i
            }
        }
        if (exists) {
            _isProductInList.value = exists
            _positionProducInList.value = position
        } else {
            _productsInList.value!!.add(product)
            val productsSorted = _productsInList.value!!.sortedWith(compareBy { it.name }).toMutableList()
            _productsInList.value = productsSorted
            _isProductInList.value = exists
        }
    }

    fun addProductToShop(product: ProductsToListModel) {
        _productsToShop.value!!.add(product)
        _productsToShop.value = _productsToShop.value
        _totalPrice.value = _totalPrice.value!! + product.quantity * product.price
    }

    fun removeProductToShop(product: ProductsToListModel) {
        _productsToShop.value!!.remove(product)
        _productsToShop.value = _productsToShop.value
        _totalPrice.value = _totalPrice.value!! - (product.quantity * product.price)
    }

    fun fillListFirst(products: MutableList<ProductsToListModel>) {
        val productsSorted = products.sortedWith(compareBy { it.name }).toMutableList()
        _productsInList.value!!.addAll(productsSorted)
        _productsInList.value = _productsInList.value
    }

    fun clear() {
        _isProductInList.value = false
        _positionProducInList.value = -1
        _totalPrice.value = 0.0
        _productsInList.value?.clear()
        _productsToShop.value?.clear()
    }
}