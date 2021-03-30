package com.skysam.hchirinos.go2shop.shopsModule.presenter

import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.shopsModule.interactor.AddListShopInteractor
import com.skysam.hchirinos.go2shop.shopsModule.interactor.AddListShopInteractorClass
import com.skysam.hchirinos.go2shop.shopsModule.ui.AddListShopView

/**
 * Created by Hector Chirinos (Home) on 29/3/2021.
 */
class AddListShopPresenterClass(private val addListShopView: AddListShopView): AddListShopPresenter {
    private val addListShopInteractor: AddListShopInteractor = AddListShopInteractorClass(this)
    override fun saveListWish(list: Shop) {
        addListShopInteractor.saveListWish(list)
    }

    override fun resultSaveListWishFirestore(statusOk: Boolean, msg: String) {
        addListShopView.resultSaveListWishFirestore(statusOk, msg)
    }
}