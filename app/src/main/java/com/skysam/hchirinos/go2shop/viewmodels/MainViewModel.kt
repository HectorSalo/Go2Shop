package com.skysam.hchirinos.go2shop.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.database.repositories.ListWishRepository
import com.skysam.hchirinos.go2shop.database.repositories.ProductsRepository
import com.skysam.hchirinos.go2shop.database.repositories.ShopRepository
import com.skysam.hchirinos.go2shop.database.repositories.StorageRepository
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.room.entities.Shop

/**
 * Created by Hector Chirinos (Home) on 29/4/2021.
 */
class MainViewModel: ViewModel() {
    val products: LiveData<List<Product>> = ProductsRepository.getProducts().asLiveData()
    val listsWish: LiveData<List<ListWish>> = ListWishRepository.getListsWish().asLiveData()
    val shops: LiveData<List<Shop>> = ShopRepository.getShops().asLiveData()
    val productsFromStorage: LiveData<List<StorageModel>> = StorageRepository.getProductsFromStorage().asLiveData()

    fun addProduct(product: Product) {
        ProductsRepository.addProduct(product)
    }

    fun editProduct(product: Product) {
        ProductsRepository.editProduct(product)
    }

    fun deleteProducts(products: MutableList<Product>) {
        ProductsRepository.deleteProducts(products)
    }

    fun addListWish(list: ListWish) {
        ListWishRepository.addListWish(list)
    }

    fun editListWish(list: ListWish, productsToSave: MutableList<ProductsToListModel>,
                     productsToUpdate: MutableList<ProductsToListModel>, productsToDelete: MutableList<ProductsToListModel>) {
        ListWishRepository.editListWish(list, productsToSave, productsToUpdate, productsToDelete)
    }

    fun deleteListsWish(lists: MutableList<ListWish>) {
        ListWishRepository.deleteLists(lists)
    }

    fun removeUnitFromProductToStorage(product: StorageModel) {
        StorageRepository.removeUnitFromProductToStorage(product)
    }

    fun deleteProductToStorage(product: StorageModel) {
        StorageRepository.deleteProductToStorage(product)
    }
}