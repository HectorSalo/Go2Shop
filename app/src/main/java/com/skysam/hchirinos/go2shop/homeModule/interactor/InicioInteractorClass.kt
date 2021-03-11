package com.skysam.hchirinos.go2shop.homeModule.interactor

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
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

    override fun getDataFromFirestore() {
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
                                    saveToRoom(product)
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
                                updateToRoom(product)
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
                                deleteToRoom(product)
                            }
                        }
                    }
                }
        }
    }

    private fun deleteToRoom(product: Product) {
        launch {
            RoomDB.getInstance().product()
                .delete(product)
        }
    }

    private fun updateToRoom(product: Product) {
        launch {
            RoomDB.getInstance().product()
                .update(product)
        }
    }

    private fun saveToRoom(product: Product) {
        launch {
            RoomDB.getInstance().product()
                .insert(product)
        }
    }
}