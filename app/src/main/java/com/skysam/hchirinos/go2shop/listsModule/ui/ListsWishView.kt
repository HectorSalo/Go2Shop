package com.skysam.hchirinos.go2shop.listsModule.ui

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 16/03/2021.
 */
interface ListsWishView {
    fun resultGetLists(lists: MutableList<ListWish>)
}