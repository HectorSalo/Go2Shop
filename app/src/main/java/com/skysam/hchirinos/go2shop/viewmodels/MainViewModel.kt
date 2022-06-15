package com.skysam.hchirinos.go2shop.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.auth.FirebaseUser
import com.skysam.hchirinos.go2shop.common.models.Deparment
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.common.models.User
import com.skysam.hchirinos.go2shop.database.repositories.*
import com.skysam.hchirinos.go2shop.common.models.ListWish
import com.skysam.hchirinos.go2shop.common.models.Product
import com.skysam.hchirinos.go2shop.common.models.Shop

/**
 * Created by Hector Chirinos (Home) on 29/4/2021.
 */
class MainViewModel: ViewModel() {
    val products: LiveData<List<Product>> = ProductsRepository.getProducts().asLiveData()
    val listsWish: LiveData<List<ListWish>> = ListWishRepository.getListsWish().asLiveData()
    val shops: LiveData<List<Shop>> = ShopRepository.getShops().asLiveData()
    val productsFromStorage: LiveData<List<StorageModel>> = StorageRepository.getProductsFromStorage().asLiveData()
    val users: LiveData<List<User>> = UsersRepository.getUsers().asLiveData()
    val deparments: LiveData<List<Deparment>> = DeparmentsRepository.getDeparments().asLiveData()

    private val _syncReady = MutableLiveData<Boolean>().apply { value = false }
    val syncReady: LiveData<Boolean> get() = _syncReady

    fun syncReadyTrue() {
        _syncReady.value = true
    }

    private val _deparmentToEdit = MutableLiveData<Deparment>()
    val deparmentToEdit: LiveData<Deparment> get() = _deparmentToEdit

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

    fun addProductToStorage(product: StorageModel) {
        val list = mutableListOf<StorageModel>()
        list.add(product)
        StorageRepository.saveProductsToStorge(list)
    }

    fun updateUnitFromProductToStorage(product: StorageModel) {
        StorageRepository.updateUnitFromProductToStorage(product)
    }

    fun editProductToStorage(product: StorageModel) {
        StorageRepository.editProductToStorage(product)
    }

    fun deleteProductToStorage(product: StorageModel) {
        StorageRepository.deleteProductToStorage(product)
    }

    fun addUser(currentUser: FirebaseUser) {
        val user = User(
            currentUser.uid,
            currentUser.displayName!!,
            currentUser.email!!
        )
        UsersRepository.addUser(user)
    }

    fun shareLists(user: User, lists: MutableList<ListWish>) {
        ListWishRepository.addListWishShare(user, lists)
    }

    fun addDeparment(deparment: Deparment) {
        DeparmentsRepository.addDeparment(deparment)
    }

    fun editDeparment(deparment: Deparment) {
        DeparmentsRepository.editDeparment(deparment)
    }

    fun departmentToEdit(deparment: Deparment) {
        _deparmentToEdit.value = deparment
    }

    fun deleteDeparment(deparment: Deparment) {
        DeparmentsRepository.deleteDeparment(deparment)
    }
}