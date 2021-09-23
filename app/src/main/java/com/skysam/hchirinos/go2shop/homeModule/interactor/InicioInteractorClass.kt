package com.skysam.hchirinos.go2shop.homeModule.interactor

import android.util.Log
import com.skysam.hchirinos.go2shop.common.ClassesCommon
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenter
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class InicioInteractorClass(private val inicioPresenter: InicioPresenter): InicioInteractor, CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun getValueWeb() {
        launch {
            var valor: String? = null
            var valorCotizacion: Float? = null
            val url = "http://www.bcv.org.ve/"

            withContext(Dispatchers.IO) {
                try {
                    val doc = Jsoup.connect(url).get()
                    val data = doc.select("div#dolar")
                    valor = data.select("strong").last()?.text()
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                }
            }
            if (valor != null) {
                val valorNeto = valor?.replace(",", ".")
                valorCotizacion = valorNeto?.toFloat()
                val valorRounded = String.format(Locale.US, "%.2f", valorCotizacion)
                valorCotizacion = valorRounded.toFloat()
            }

            if (valorCotizacion != null) {
                saveValueWeb(valorCotizacion)
                inicioPresenter.resultSync(true)
            } else {
                inicioPresenter.resultSync(false)
            }
        }
    }

    private fun saveValueWeb(valorFloat: Float) {
        SharedPreferenceBD.saveValue(AuthAPI.getCurrenUser()!!.uid, valorFloat)
    }
}