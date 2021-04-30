package com.skysam.hchirinos.go2shop.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.go2shop.database.repositories.ProductsRepository
import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos (Home) on 29/4/2021.
 */
class MainViewModel: ViewModel() {
    val products: LiveData<List<Product>> = ProductsRepository.getProducts().asLiveData()

    fun addProduct(product: Product) {
        ProductsRepository.addProduct(product)
    }

    fun editProduct(product: Product) {
        ProductsRepository.editProduct(product)
    }

    fun deleteProducts(products: MutableList<Product>) {
        ProductsRepository.deleteProducts(products)
    }
}