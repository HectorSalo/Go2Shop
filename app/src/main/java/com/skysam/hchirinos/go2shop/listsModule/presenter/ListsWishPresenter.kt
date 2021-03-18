package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish

interface ListsWishPresenter {
    fun getLists()
    fun deleteLists(lists: MutableList<ListWish>)

    fun resultDeleteLists(statusOk: Boolean, msg: String)
    fun resultGetLists(lists: MutableList<ListWish>)
}
