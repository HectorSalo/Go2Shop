package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

interface ListsWishPresenter {
    fun getLists()

    fun resultGetLists(lists: MutableList<ListWish>)
}
