package com.skysam.hchirinos.go2shop.homeModule.ui

import androidx.lifecycle.*
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

class HomeViewModel : ViewModel() {

    val listFlow: LiveData<MutableList<Shop>> = RoomDB.getInstance().shop()
        .getAll().asLiveData()

    private val _lastDateShop = MutableLiveData<String>().apply {
        val list : MutableList<Shop> = mutableListOf()
        /*if (list.value != null) {
            if (!list.value.isNullOrEmpty()) {
                val listFinal = list.value!!.sortedWith(compareBy { it.dateCreated }).toMutableList()
                val date = DateFormat.getDateInstance().format(Date(listFinal[listFinal.size - 1].dateCreated))
                value = date
                return@apply
            }
        }
        value = ""*/
    }
    val lastDateShop: LiveData<String> = _lastDateShop

    private val _priceLastShop = MutableLiveData<String>().apply {
        val list: LiveData<MutableList<Shop>> = RoomDB.getInstance().shop()
            .getAll().asLiveData()
        if (list.value != null) {
            if (!list.value.isNullOrEmpty()) {
                val listFinal = list.value!!.sortedWith(compareBy { it.dateCreated }).toMutableList()
                value = NumberFormat.getInstance().format(listFinal[listFinal.size - 1].total).toString()
                return@apply
            }
        }
        value = ""
    }
    val priceLastShop: LiveData<String> get() = _priceLastShop
}