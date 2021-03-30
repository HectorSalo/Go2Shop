package com.skysam.hchirinos.go2shop.homeModule.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _lastDateShop = MutableLiveData<String>().apply {
        viewModelScope.launch {
            var list = RoomDB.getInstance().shop()
                    .getAll()
            list = list.sortedWith(compareBy { it.dateCreated }).toMutableList()
            val date = DateFormat.getDateInstance().format(Date(list[list.size - 1].dateCreated))
            value = date
        }
    }
    val lastDateShop: LiveData<String> = _lastDateShop

    private val _priceLastShop = MutableLiveData<String>().apply {
        viewModelScope.launch {
            var list = RoomDB.getInstance().shop()
                    .getAll()
            list = list.sortedWith(compareBy { it.dateCreated }).toMutableList()
            value = NumberFormat.getInstance().format(list[list.size - 1].total).toString()
        }
    }
    val priceLastShop: LiveData<String> get() = _priceLastShop
}