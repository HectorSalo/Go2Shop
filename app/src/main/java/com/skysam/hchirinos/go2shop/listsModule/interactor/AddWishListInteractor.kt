package com.skysam.hchirinos.go2shop.listsModule.interactor

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
interface AddWishListInteractor {
    fun saveListWish(list: ListWish)
}