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
    private val _producInList = MutableLiveData<Boolean>()
    val productInList: LiveData<Boolean> get() = _producInList

    private val _positionProducInList = MutableLiveData<Int>()
    val positionProductInList: LiveData<Int> get() = _positionProducInList

    private val _totalPrice = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val totalPrice: LiveData<Double> get() = _totalPrice

    private val _productsSelected = MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
    val productsSelected: LiveData<MutableList<Product>> get() = _productsSelected

    private val listProducts = MutableLiveData<MutableList<Product>>().apply {
        viewModelScope.launch {
            value = RoomDB.getInstance().product().getAll()
        }
    }

    fun getProducts(): LiveData<MutableList<Product>> {
        return listProducts
    }

    fun addProductToList(product: Product) {
        if (!_productsSelected.value!!.contains(product)) {
            _productsSelected.value!!.add(product)
            _productsSelected.value = _productsSelected.value
            _totalPrice.value = _totalPrice.value!! + product.quantity * product.price
            _producInList.value = false
            return
        }
        _producInList.value = true
        _positionProducInList.value = _productsSelected.value!!.indexOf(product)
    }

    fun removeProductFromList(position: Int) {
        val productSelected = _productsSelected.value!![position]
        _productsSelected.value!!.removeAt(position)
        _productsSelected.value = _productsSelected.value
        _totalPrice.value = _totalPrice.value!! - (productSelected.quantity * productSelected.price)
    }
}