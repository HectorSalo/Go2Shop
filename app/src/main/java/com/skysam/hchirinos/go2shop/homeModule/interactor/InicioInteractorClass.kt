package com.skysam.hchirinos.go2shop.homeModule.interactor

import android.util.Log
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenter
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.security.KeyManagementException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
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

            withContext(Dispatchers.Default) {
                try {
                    val doc = Jsoup.connect(url).sslSocketFactory(socketFactory()).get()
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

    private fun socketFactory(): SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            return sslContext.socketFactory
        } catch (e: Exception) {
            when (e) {
                is RuntimeException, is KeyManagementException -> {
                    throw RuntimeException("Failed to create a SSL socket factory", e)
                }
                else -> throw e
            }
        }
    }

    private fun saveValueWeb(valorFloat: Float) {
        SharedPreferenceBD.saveValue(AuthAPI.getCurrenUser()!!.uid, valorFloat)
    }
}