package com.skysam.hchirinos.go2shop.shopsModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import kotlinx.coroutines.launch

/**
 * Created by Hector Chirinos on 23/03/2021.
 */
class ConfigNewShopViewModel: ViewModel() {
    private val _lists = MutableLiveData<MutableList<ListWish>>().apply {
        viewModelScope.launch {
            value = RoomDB.getInstance().listWish().getAll()
        }
    }
    val lists: LiveData<MutableList<ListWish>> get() = _lists

    private val _rateChange = MutableLiveData<Double>().apply {
        viewModelScope.launch {
            value = SharedPreferenceBD.getValue(AuthAPI.getCurrenUser()!!.uid).toDouble()
        }
    }
    val rateChange: LiveData<Double> get() = _rateChange
}