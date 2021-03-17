package com.skysam.hchirinos.go2shop.homeModule.interactor

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class InicioInteractorClass: InicioInteractor, CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun getValueWeb() {
        launch {
            var valor: String? = null
            var valorCotizacion: Float? = null
            val url = "https://monitordolarvenezuela.com/"

            withContext(Dispatchers.IO) {
                try {
                    val doc = Jsoup.connect(url).get()
                    val data = doc.select("div.back-white-tabla")
                    valor = data.select("h6.text-center").text()
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                }
            }
            if (valor != null) {
                val valor1: String = valor!!.replace("Bs.S ", "")
                val valor2 = valor1.replace(".", "")
                val values: List<String> = valor2.split(" ")
                val valor3 = values[0]
                val valorNeto = valor3.replace(",", ".")
                valorCotizacion = valorNeto.toFloat()

                val values2: List<String> = valor1.split(" ")
                valor = values2[0]
            }

            if (valorCotizacion != null) {
                saveValueWeb(valorCotizacion)
            }
        }
    }

    private fun saveValueWeb(valorFloat: Float) {
        SharedPreferenceBD.saveValue(AuthAPI.getCurrenUser()!!.uid, valorFloat)
    }

    override fun getProductsFromFirestore() {
        launch {
            val productsInRoom = RoomDB.getInstance().product().getAll()

            FirestoreAPI.getProducts()
                .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
                .addSnapshotListener (MetadataChanges.INCLUDE) { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                        return@addSnapshotListener
                    }

                    for (dc in snapshots!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                Log.d(TAG, "New city: ${dc.document.data}")
                                val product = Product(
                                    dc.document.id,
                                    dc.document.getString(Constants.NAME)!!,
                                    dc.document.getString(Constants.UNIT)!!,
                                    dc.document.getString(Constants.USER_ID)!!,
                                    dc.document.getDouble(Constants.PRICE)!!,
                                    dc.document.getDouble(Constants.QUANTITY)!!
                                )
                                if (!productsInRoom.contains(product)) {
                                    saveProductToRoom(product)
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Log.d(TAG,"Modified city: ${dc.document.data}")
                                val product = Product(
                                    dc.document.id,
                                    dc.document.getString(Constants.NAME)!!,
                                    dc.document.getString(Constants.UNIT)!!,
                                    dc.document.getString(Constants.USER_ID)!!,
                                    dc.document.getDouble(Constants.PRICE)!!,
                                    dc.document.getDouble(Constants.QUANTITY)!!
                                )
                                updateProductToRoom(product)
                            }
                            DocumentChange.Type.REMOVED -> {
                                Log.d(TAG, "Removed city: ${dc.document.data}")
                                val product = Product(
                                    dc.document.id,
                                    dc.document.getString(Constants.NAME)!!,
                                    dc.document.getString(Constants.UNIT)!!,
                                    dc.document.getString(Constants.USER_ID)!!,
                                    dc.document.getDouble(Constants.PRICE)!!,
                                    dc.document.getDouble(Constants.QUANTITY)!!
                                )
                                deleteProductToRoom(product)
                            }
                        }
                    }
                }
        }
    }

    override fun getListsWishFromFirestore() {
        //launch { RoomDB.getInstance().listWish().deleteAll() }
        FirestoreAPI.getListWish()
            .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            launch {
                                val list = RoomDB.getInstance().listWish()
                                    .getById(dc.document.id)
                                if (list == null) {
                                    val productsFromList: MutableList<ProductsToListModel> = mutableListOf()
                                    val listFinal = ListWish(
                                        dc.document.id,
                                        dc.document.getString(Constants.NAME)!!,
                                        dc.document.getString(Constants.USER_ID)!!,
                                        productsFromList,
                                        dc.document.getDouble(Constants.TOTAL_LIST_WISH)!!
                                    )
                                    saveListToRoom(listFinal)
                                    //getProductsToListWishFromFirestore(dc.document.id)
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {
                            /*FirestoreAPI.getProductsFromListWish(dc.document.id).get()
                                .addOnSuccessListener { result ->
                                    val productsFromList: MutableList<Product> = mutableListOf()
                                    for (doc in result) {
                                        val product = Product(
                                            doc.id,
                                            doc.getString(Constants.NAME)!!,
                                            doc.getString(Constants.UNIT)!!,
                                            doc.getString(Constants.USER_ID)!!,
                                            doc.getDouble(Constants.PRICE)!!,
                                            doc.getDouble(Constants.QUANTITY)!!
                                        )
                                        productsFromList.add(product)
                                    }
                                    val list = ListWish(
                                        dc.document.id,
                                        dc.document.getString(Constants.NAME)!!,
                                        dc.document.getString(Constants.USER_ID)!!,
                                        productsFromList,
                                        dc.document.getDouble(Constants.TOTAL_LIST_WISH)!!
                                    )
                                   updateListWishToRoom(list)
                                }*/
                        }
                        DocumentChange.Type.REMOVED -> {
                            deleteListWishToRoom(dc.document.id)
                        }
                    }
                }
                getProductsToListWishFromFirestoreTest()
            }
    }

    private fun getProductsToListWishFromFirestore(id: String) {
        FirestoreAPI.getProductsFromListWish()
            .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
            .whereEqualTo(Constants.LIST_ID, id)
            .get()
            .addOnSuccessListener { result ->
                val listProducts: MutableList<ProductsToListModel> = mutableListOf()
                for (document in result) {
                    val product = ProductsToListModel(
                        document.id,
                        document.getString(Constants.NAME)!!,
                        document.getString(Constants.UNIT)!!,
                        document.getString(Constants.USER_ID)!!,
                        document.getString(Constants.LIST_ID)!!,
                        document.getDouble(Constants.PRICE)!!,
                        document.getDouble(Constants.QUANTITY)!!
                    )
                    listProducts.add(product)
                }
                launch {
                    RoomDB.getInstance().listWish()
                        .updateListProducts(id,
                            listProducts)
                }
            }
    }

    private fun getProductsToListWishFromFirestoreTest() {
        FirestoreAPI.getProductsFromListWish()
            .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
            .addSnapshotListener(MetadataChanges.INCLUDE) { value, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when(dc.type) {
                        DocumentChange.Type.ADDED -> {
                            launch {
                                val idList = dc.document.getString(Constants.LIST_ID)
                                val listOld = RoomDB.getInstance().listWish()
                                    .getById(idList!!)
                                if (listOld != null) {
                                    val products = listOld!!.listProducts
                                    var add = true
                                    for (i in products.indices) {
                                        if (products[i].id == dc.document.id) {
                                            add = false
                                        }
                                    }
                                    if (add) {
                                        val product = ProductsToListModel(
                                            dc.document.id,
                                            dc.document.getString(Constants.NAME)!!,
                                            dc.document.getString(Constants.UNIT)!!,
                                            dc.document.getString(Constants.USER_ID)!!,
                                            dc.document.getString(Constants.LIST_ID)!!,
                                            dc.document.getDouble(Constants.PRICE)!!,
                                            dc.document.getDouble(Constants.QUANTITY)!!
                                        )
                                        products.add(product)
                                    }
                                    RoomDB.getInstance().listWish()
                                        .updateListProducts(idList,
                                            products)
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {}
                        DocumentChange.Type.REMOVED -> {}
                    }
                }
            }
    }

    override fun getListsShopFromFirestore() {

    }

    private fun deleteListWishToRoom(id: String) {
        launch {
            RoomDB.getInstance().listWish()
                .delete(id)
        }
    }

    private fun saveListToRoom(list: ListWish) {
        launch {
            RoomDB.getInstance().listWish()
                .insert(list)
        }
    }

    private fun deleteProductToRoom(product: Product) {
        launch {
            RoomDB.getInstance().product()
                .delete(product)
        }
    }

    private fun updateProductToRoom(product: Product) {
        launch {
            RoomDB.getInstance().product()
                .update(product)
        }
    }

    private fun saveProductToRoom(product: Product) {
        launch {
            RoomDB.getInstance().product()
                .insert(product)
        }
    }

}