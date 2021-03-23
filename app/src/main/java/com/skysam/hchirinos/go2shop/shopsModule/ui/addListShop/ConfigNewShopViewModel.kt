package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
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
}