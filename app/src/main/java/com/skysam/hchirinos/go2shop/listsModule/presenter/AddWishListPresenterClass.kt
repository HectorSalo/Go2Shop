package com.skysam.hchirinos.go2shop.listsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.listsModule.interactor.AddWishListInteractor
import com.skysam.hchirinos.go2shop.listsModule.interactor.AddWishListInteractorClass
import com.skysam.hchirinos.go2shop.listsModule.ui.addListWish.AddWishListView

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class AddWishListPresenterClass(private val addWishListView: AddWishListView): AddListWishPresenter {
    private val addWishListInteractor: AddWishListInteractor = AddWishListInteractorClass(this)
    override fun saveListWish(list: ListWish) {
        addWishListInteractor.saveListWishFirestore(list)
    }

    override fun resultSaveListWishFirestore(statusOk: Boolean, msg: String, list: ListWish?) {
        if (statusOk){
            addWishListInteractor.saveListWishRoom(msg, list!!)
        } else {
            addWishListView.resultSaveListWish(false, msg)
        }
    }

    override fun resultSaveListWishRoom() {
        addWishListView.resultSaveListWish(true, "")
    }
}