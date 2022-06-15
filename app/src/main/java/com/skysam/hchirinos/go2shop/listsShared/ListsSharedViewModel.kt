package com.skysam.hchirinos.go2shop.listsShared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.go2shop.common.models.ListShared
import com.skysam.hchirinos.go2shop.common.models.User
import com.skysam.hchirinos.go2shop.database.repositories.ListShareRepository
import com.skysam.hchirinos.go2shop.database.repositories.ProductsRepository
import com.skysam.hchirinos.go2shop.database.repositories.UsersRepository
import com.skysam.hchirinos.go2shop.common.models.Product

class ListsSharedViewModel : ViewModel() {
    val listsShared: LiveData<List<ListShared>> = ListShareRepository.getListsShared().asLiveData()
    val users: LiveData<List<User>> = UsersRepository.getUsers().asLiveData()
    val products: LiveData<List<Product>> = ProductsRepository.getProducts().asLiveData()

    fun addProduct(product: Product) {
        ProductsRepository.addProduct(product)
    }

    fun addListShared(listShared: ListShared) {
        ListShareRepository.addListShared(listShared)
    }

    fun updateListShared(listShared: ListShared) {
        ListShareRepository.updateListShared(listShared)
    }

    fun deleteList(listShared: ListShared) {
        ListShareRepository.deleteList(listShared)
    }
}