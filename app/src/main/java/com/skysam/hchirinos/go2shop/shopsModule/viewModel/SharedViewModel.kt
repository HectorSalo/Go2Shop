package com.skysam.hchirinos.go2shop.shopsModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel

/**
 * Created by Hector Chirinos (Home) on 23/3/2021.
 */
class SharedViewModel: ViewModel() {
    private val _nameShop = MutableLiveData<String>()
    val nameShop: LiveData<String> get() = _nameShop

    private val _rateChange = MutableLiveData<Double>()
    val rateChange: LiveData<Double> get() = _rateChange

    private val _productsSelected = MutableLiveData<MutableList<ProductsToListModel>>().apply { value = mutableListOf() }
    val productsSelected: LiveData<MutableList<ProductsToListModel>> get() = _productsSelected

    fun sharedData(name: String, rate: Double, list: MutableList<ProductsToListModel>) {
        _nameShop.value = name
        _rateChange.value = rate
        _productsSelected.value = list
    }
}