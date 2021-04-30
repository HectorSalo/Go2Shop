package com.skysam.hchirinos.go2shop.homeModule.interactor

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.classView.ProductsSavedToList
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenter
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class InicioInteractorClass(private val inicioPresenter: InicioPresenter): InicioInteractor, CoroutineScope, ProductsSavedToList {
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

    private fun getListsWishFromFirestore() {
        //launch { RoomDB.getInstance().listWish().deleteAll() }
        FirestoreAPI.getListWish()
            .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
            .orderBy(Constants.DATE_CREATED, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents->
                for (doc in documents) {
                    val productsFromList: MutableList<ProductsToListModel> = mutableListOf()
                    val dateCreated = doc.getDate(Constants.DATE_CREATED)!!.time
                    val dateEdited = doc.getDate(Constants.DATE_LAST_EDITED)!!.time
                    val listToAdd = ListWish(
                        doc.id,
                        doc.getString(Constants.NAME)!!,
                        doc.getString(Constants.USER_ID)!!,
                        productsFromList,
                        doc.getDouble(Constants.TOTAL_LIST_WISH)!!,
                        dateCreated,
                        dateEdited
                    )
                    FirestoreAPI.getProductsToListWishFromFirestore(doc.id, listToAdd, this@InicioInteractorClass)
                }
                if (AuthAPI.getCurrenUser() != null) {
                    getListsShopFromFirestore()
                }
            }
            .addOnFailureListener { inicioPresenter.resultSync(false) }
    }

    private fun getListsShopFromFirestore() {
        //launch { RoomDB.getInstance().shop().deleteAll() }
        FirestoreAPI.getListShop()
            .whereEqualTo(Constants.USER_ID, AuthAPI.getCurrenUser()!!.uid)
            .orderBy(Constants.DATE_CREATED, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents->
                for (doc in documents) {
                    val productsFromList: MutableList<ProductsToListModel> = mutableListOf()
                    val dateCreated = doc.getDate(Constants.DATE_CREATED)!!.time
                    val listToAdd = Shop(
                        doc.id,
                        doc.getString(Constants.NAME)!!,
                        doc.getString(Constants.USER_ID)!!,
                        productsFromList,
                        doc.getDouble(Constants.TOTAL_LIST_WISH)!!,
                        dateCreated,
                        doc.getDouble(Constants.RATE_CHANGE)!!
                    )
                    FirestoreAPI.getProductsToListShopFromFirestore(doc.id, listToAdd, this@InicioInteractorClass)
                }
                inicioPresenter.resultSync(true)
            }
            .addOnFailureListener { inicioPresenter.resultSync(false) }
    }

    override fun savedListWish(listWish: ListWish) {
        launch {
            val list = RoomDB.getInstance().listWish()
                .getById(listWish.id)
            if (list == null) {
                RoomDB.saveListWishToRoom(listWish)
            } else {
                RoomDB.updateListWishToRoom(listWish)
            }
        }
    }

    override fun savedListShop(listShop: Shop) {
        launch {
            val list = RoomDB.getInstance().shop()
                .getById(listShop.id)
            if (list == null) {
                RoomDB.saveListShopToRoom(listShop)
            } else {
                RoomDB.updateListShopToRoom(listShop)
            }
        }
    }

}