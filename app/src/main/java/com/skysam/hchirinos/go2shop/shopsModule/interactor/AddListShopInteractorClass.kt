package com.skysam.hchirinos.go2shop.shopsModule.interactor

import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.Network
import com.skysam.hchirinos.go2shop.common.classView.ProductsSavedToList
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.shopsModule.presenter.AddListShopPresenter
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 29/3/2021.
 */
class AddListShopInteractorClass(private val addListShopPresenter: AddListShopPresenter): AddListShopInteractor, ProductsSavedToList {
    override fun saveListWish(list: Shop) {
        val date = Date(list.dateCreated)
        val data = hashMapOf(
                Constants.NAME to list.name,
                Constants.USER_ID to list.userId,
                Constants.TOTAL_LIST_WISH to list.total,
                Constants.DATE_CREATED to date,
                Constants.DATE_LAST_EDITED to date
        )
        FirestoreAPI.getListShop().add(data)
                .addOnSuccessListener { doc ->
                    saveProductsInList(list, doc.id)
                }
                .addOnFailureListener { e->
                    addListShopPresenter.resultSaveListWishFirestore(false, e.toString())
                }
        if (!Network.isAvailable()) {
            addListShopPresenter.resultSaveListWishFirestore(false, "e.toString()")
        }
    }

    private fun saveProductsInList(list: Shop, id: String) {
        for (i in list.listProducts.indices) {
            val data = hashMapOf(
                    Constants.NAME to list.listProducts[i].name,
                    Constants.UNIT to list.listProducts[i].unit,
                    Constants.USER_ID to list.listProducts[i].userId,
                    Constants.LIST_ID to id,
                    Constants.PRICE to list.listProducts[i].price,
                    Constants.QUANTITY to list.listProducts[i].quantity
            )
            FirestoreAPI.getProductsFromListShop()
                    .add(data)
                    .addOnSuccessListener {
                        if (i == list.listProducts.indices.last) {
                            FirestoreAPI.getProductsToListShopFromFirestore(id, list, this)
                        }
                    }
                    .addOnFailureListener { e->
                        addListShopPresenter.resultSaveListWishFirestore(false, e.toString())
                    }
        }
        if (!Network.isAvailable()) {
            addListShopPresenter.resultSaveListWishFirestore(false, "e.toString()")
        }
    }

    override fun savedListWish(listWish: ListWish) {

    }

    override fun savedListShop(listShop: Shop) {
        RoomDB.saveListShopToRoom(listShop)
        addListShopPresenter.resultSaveListWishFirestore(true, "")
    }
}