package com.skysam.hchirinos.go2shop.homeModule.ui

import androidx.lifecycle.*
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
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
}