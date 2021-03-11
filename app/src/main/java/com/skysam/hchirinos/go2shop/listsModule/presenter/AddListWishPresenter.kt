package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
interface AddListWishPresenter {
    fun saveListWish(list: ListWish)

    fun resultSaveListWishFirestore(statusOk: Boolean, msg: String, list: ListWish?)
    fun resultSaveListWishRoom()
}