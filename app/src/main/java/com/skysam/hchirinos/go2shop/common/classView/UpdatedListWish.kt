package com.skysam.hchirinos.go2shop.common.classView

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 18/03/2021.
 */
interface UpdatedListWish {
    fun updatedListWish(position: Int, listWishResult: ListWish)
}