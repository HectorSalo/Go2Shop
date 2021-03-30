package com.skysam.hchirinos.go2shop.common.classView

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Shop

/**
 * Created by Hector Chirinos on 18/03/2021.
 */
interface ProductsSavedToList {
    fun savedListWish(listWish: ListWish)
    fun savedListShop(listShop: Shop)
}