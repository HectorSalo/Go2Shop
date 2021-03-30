package com.skysam.hchirinos.go2shop.listsModule.interactor

import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.classView.ProductsSavedToList
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.listsModule.presenter.AddListWishPresenter
import java.util.*

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class AddWishListInteractorClass(private val addListWishPresenter: AddListWishPresenter): AddWishListInteractor, ProductsSavedToList {

    override fun saveListWishFirestore(list: ListWish) {
        val date = Date(list.dateCreated)
        val data = hashMapOf(
            Constants.NAME to list.name,
            Constants.USER_ID to list.userId,
            Constants.TOTAL_LIST_WISH to list.total,
            Constants.DATE_CREATED to date,
            Constants.DATE_LAST_EDITED to date
        )
        FirestoreAPI.getListWish().add(data)
            .addOnSuccessListener { doc ->
                saveProductsInList(list, doc.id)
            }
            .addOnFailureListener { e->
                addListWishPresenter.resultSaveListWishFirestore(false, e.toString())
            }
    }

    private fun saveProductsInList(list: ListWish, id: String) {
        for (i in list.listProducts.indices) {
            val data = hashMapOf(
                Constants.NAME to list.listProducts[i].name,
                Constants.UNIT to list.listProducts[i].unit,
                Constants.USER_ID to list.listProducts[i].userId,
                Constants.LIST_ID to id,
                Constants.PRICE to list.listProducts[i].price,
                Constants.QUANTITY to list.listProducts[i].quantity
            )
            FirestoreAPI.getProductsFromListWish()
                .add(data)
                .addOnSuccessListener {
                    if (i == list.listProducts.indices.last) {
                        FirestoreAPI.getProductsToListWishFromFirestore(id, list, this)
                    }
                }
                .addOnFailureListener { e->
                    addListWishPresenter.resultSaveListWishFirestore(false, e.toString())
                }
        }
    }

    override fun saved(listWish: ListWish?, listShop: Shop?) {
        RoomDB.saveListWishToRoom(listWish!!)
        addListWishPresenter.resultSaveListWishFirestore(true, "")
    }
}