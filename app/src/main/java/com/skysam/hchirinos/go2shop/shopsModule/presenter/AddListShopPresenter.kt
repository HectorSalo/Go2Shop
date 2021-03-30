package com.skysam.hchirinos.go2shop.shopsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Shop

/**
 * Created by Hector Chirinos (Home) on 29/3/2021.
 */
interface AddListShopPresenter {
    fun saveListWish(list: Shop)

    fun resultSaveListWishFirestore(statusOk: Boolean, msg: String)
}