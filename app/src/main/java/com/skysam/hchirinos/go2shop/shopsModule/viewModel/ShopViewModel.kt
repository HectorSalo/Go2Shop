package com.skysam.hchirinos.go2shop.shopsModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.Shop

/**
 * Created by Hector Chirinos (Home) on 30/3/2021.
 */
class ShopViewModel: ViewModel() {
    val listFlow: LiveData<MutableList<Shop>> = RoomDB.getInstance().shop()
        .getAll().asLiveData()
}