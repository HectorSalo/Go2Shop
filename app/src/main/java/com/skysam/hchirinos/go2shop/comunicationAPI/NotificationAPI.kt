package com.skysam.hchirinos.go2shop.comunicationAPI

import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.GoToShop
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Hector Chirinos on 07/09/2021.
 */
object NotificationAPI {
    const val NOTIFICATION_API = "https://skysam.000webhostapp.com/go_to_shop/goToShopRS.php"
    private const val SEND_NOTIFICATION = "sendNotification"

    fun sendNotification (title: String, message: String, uid: String,
                          myEmail: String, listener: EventErrorTypeListener) {
        val params = JSONObject()
        params.put(Constants.METHOD, SEND_NOTIFICATION)
        params.put(Constants.TITLE, title)
        params.put(Constants.MESSAGE, message)
        params.put(Constants.TOPIC, Constants.LISTS_SHARED)
        params.put(Constants.USER_ID, uid)
        params.put(Constants.EMAIL, myEmail)
        params.put(Constants.NAME_USER_SENDING, title)

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, NOTIFICATION_API, params,
            {response ->
                try {
                    when (response.getInt(Constants.SUCCESS)) {
                        Constants.SEND_NOTIFICATION_SUCCESS -> {
                            Log.e("MSG OK", response.getString(Constants.MESSAGE))
                        }
                        Constants.ERROR_METHOD_NOT_EXIST -> listener.onError(Constants.ERROR_METHOD_NOT_EXIST, R.string.method_not_exist)
                        else -> listener.onError(Constants.ERROR_SERVER, R.string.common_error_server)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    listener.onError(Constants.ERROR_PROCESS_DATA, R.string.common_error_server)
                }
            }, {error ->
                Log.e("Volley error", error.localizedMessage!!)
                listener.onError(Constants.ERROR_VOLLEY, R.string.common_error_server)
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }
        GoToShop.GoToShop.addToReqQueue(jsonObjectRequest)
    }
}