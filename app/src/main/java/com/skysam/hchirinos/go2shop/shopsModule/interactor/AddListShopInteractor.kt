package com.skysam.hchirinos.go2shop.shopsModule.interactor

import com.skysam.hchirinos.go2shop.database.room.entities.Shop

interface AddListShopInteractor {
    fun saveListWish(list: Shop)
}
