package com.skysam.hchirinos.go2shop.database.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class ProductsConverter {
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

    @TypeConverter
    fun toList(data: String): MutableList<ProductsToListModel> {
        return Gson().fromJson<MutableList<ProductsToListModel>>(data)
    }

    @TypeConverter
    fun toString(products: MutableList<ProductsToListModel>): String {
        return Gson().toJson(products)
    }
}