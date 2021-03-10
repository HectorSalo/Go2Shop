package com.skysam.hchirinos.go2shop.database.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class ProductsConverter {
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

    @TypeConverter
    fun toList(data: String): ListProducts {
        return Gson().fromJson<ListProducts>(data)
    }

    @TypeConverter
    fun toString(products:ListProducts): String {
        return Gson().toJson(products)
    }
}