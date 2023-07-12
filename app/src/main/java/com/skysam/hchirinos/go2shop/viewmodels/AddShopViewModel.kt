package com.skysam.hchirinos.go2shop.viewmodels

import androidx.lifecycle.*
import com.skysam.hchirinos.go2shop.common.models.*
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.database.repositories.*
import com.skysam.hchirinos.go2shop.common.models.ListWish
import com.skysam.hchirinos.go2shop.common.models.Product
import com.skysam.hchirinos.go2shop.common.models.Shop
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Hector Chirinos on 03/05/2021.
 */
class AddShopViewModel: ViewModel() {
    //ConfigShop
    val lists: LiveData<List<ListWish>> = ListWishRepository.getListsWish().asLiveData()
    val listsShared: LiveData<List<ListShared>> = ListSharedRepository.getListsShared().asLiveData()
    val deparments: LiveData<List<Deparment>> = DeparmentsRepository.getDeparments().asLiveData()
    val currentShop: LiveData<CurrentShop?> = CurrentShopRepository.getCurrentShop().asLiveData()

    private val _rateChange = MutableLiveData<Double>().apply {
        viewModelScope.launch {
            value = SharedPreferenceBD.getValue(AuthAPI.getCurrenUser()!!.uid).toDouble()
        }
    }
    val rateChange: LiveData<Double> get() = _rateChange

    private val _nameShop = MutableLiveData<String>()
    val nameShop: LiveData<String> get() = _nameShop

    private val _productsShared = MutableLiveData<MutableList<ProductsToListModel>>().apply { value = mutableListOf() }
    val productsShared: LiveData<MutableList<ProductsToListModel>> get() = _productsShared

    fun sharedData(name: String, rate: Double, list: MutableList<ProductsToListModel>) {
        _nameShop.value = name
        _rateChange.value = rate
        _productsShared.value = list
    }


    //AddShop
    val allProductsCreated: LiveData<List<Product>> = ProductsRepository.getProducts().asLiveData()
    val productsStoraged: LiveData<List<StorageModel>> = StorageRepository.getProductsFromStorage().asLiveData()

    private val _totalPrice = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val totalPrice: LiveData<Double> get() = _totalPrice

    private val _allProducts = MutableLiveData<MutableList<ProductsToShopModel>>().apply { value = mutableListOf() }
    val allProducts: LiveData<MutableList<ProductsToShopModel>> get() = _allProducts

    fun addProduct(product: Product) {
        ProductsRepository.addProduct(product)
    }

    fun addProductToList(product: ProductsToShopModel) {
        _allProducts.value!!.add(product)
        _allProducts.value = _allProducts.value!!.sortedWith(compareBy { it.name }).toMutableList()
        _allProducts.value = _allProducts.value
    }

    fun updateProductToList(product: ProductsToShopModel, position: Int) {
        _allProducts.value!![position] = product
        _allProducts.value = _allProducts.value
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
                products[i].quantity,
                deparment = products[i].deparment
            )
            list.add(productToModel)
        }
        _allProducts.value = list.sortedWith(compareBy { it.name }).toMutableList()
        _allProducts.value = _allProducts.value
    }

    fun updatedPrice(priceOld: Double, priceNew: Double) {
        _totalPrice.value = _totalPrice.value!! + priceNew - priceOld
    }

    fun saveShop(shop: Shop) {
        ShopRepository.saveShop(shop)
    }

    fun saveProductsToStorage(products: MutableList<StorageModel>) {
        val productsToSave: MutableList<StorageModel> = mutableListOf()
        val productsToUpdate: MutableList<StorageModel> = mutableListOf()
        for (productToStorage in products) {
            var exists = false
            for (product in productsStoraged.value!!) {
                if (productToStorage.name == product.name && productToStorage.unit == product.unit) {
                    exists = true
                    productToStorage.id = product.id
                    val quantity = productToStorage.quantityFromShop + product.quantityRemaining
                    productToStorage.quantityRemaining = quantity
                }
            }
            if (exists) {
                productsToUpdate.add(productToStorage)
            } else {
                productsToSave.add(productToStorage)
            }
        }
        StorageRepository.saveProductsToStorge(productsToSave)
        StorageRepository.updateProductsToStorage(productsToUpdate)
    }

    //SaveCurrentShop
    fun firstSaveCurrentShop(name: String, rate: Double, products: MutableList<ProductsToListModel>) {
        val list: MutableList<ProductsToShopModel> = mutableListOf()
        for (i in products.indices) {
            val productToModel = ProductsToShopModel(
                products[i].id,
                products[i].name,
                products[i].unit,
                products[i].userId,
                products[i].listId,
                products[i].price,
                products[i].quantity,
                deparment = products[i].deparment
            )
            list.add(productToModel)
        }

        val newCurrentShop = CurrentShop(
            "",
            name,
            "",
            list,
            Date(),
            rate
        )
        CurrentShopRepository.saveCurrentShop(newCurrentShop)
    }

    fun updateCurrentShop(products: MutableList<ProductsToShopModel>) {
        CurrentShopRepository.updateCurrentShop(products)
    }

    fun deleteCurrentShop() {
        CurrentShopRepository.deleteCurrentShop()
    }

    fun restoreShop(currentShop: CurrentShop) {
        _nameShop.value = currentShop.name
        _rateChange.value = currentShop.rateChange
        _allProducts.value = currentShop.listProducts.sortedWith(compareBy { it.name }).toMutableList()
        _allProducts.value = _allProducts.value
    }
}