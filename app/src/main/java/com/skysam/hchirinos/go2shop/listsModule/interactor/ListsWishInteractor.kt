package com.skysam.hchirinos.go2shop.listsModule.interactor

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

/**
 * Created by Hector Chirinos on 16/03/2021.
 */
interface ListsWishInteractor {
    fun getLists()
    fun deleteLists(lists: MutableList<ListWish>)
}