package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.listsModule.interactor.ListsWishInteractor
import com.skysam.hchirinos.go2shop.listsModule.interactor.ListsWishInteractorClass
import com.skysam.hchirinos.go2shop.listsModule.ui.ListsWishView

/**
 * Created by Hector Chirinos on 16/03/2021.
 */
class ListsWishPresenterClass(private val listsWishView: ListsWishView): ListsWishPresenter {
    private val listsWishInteractor: ListsWishInteractor = ListsWishInteractorClass(this)
    override fun getLists() {
        listsWishInteractor.getLists()
    }

    override fun deleteLists(lists: MutableList<ListWish>) {
        listsWishInteractor.deleteLists(lists)
    }

    override fun resultDeleteLists(statusOk: Boolean, msg: String) {
        listsWishView.resultDeleteLists(statusOk, msg)
    }

    override fun resultGetLists(lists: MutableList<ListWish>) {
        listsWishView.resultGetLists(lists)
    }
}