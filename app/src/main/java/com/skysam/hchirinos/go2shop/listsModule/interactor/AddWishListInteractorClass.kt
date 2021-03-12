package com.skysam.hchirinos.go2shop.listsModule.interactor

import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.listsModule.presenter.AddListWishPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos on 10/03/2021.
 */
class AddWishListInteractorClass(private val addListWishPresenter: AddListWishPresenter): AddWishListInteractor, CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun saveListWishFirestore(list: ListWish) {
        val data = hashMapOf(
            Constants.NAME to list.name,
            Constants.USER_ID to list.userId,
            Constants.TOTAL_LIST_WISH to list.total
        )
        FirestoreAPI.getListWish().add(data)
            .addOnSuccessListener { doc ->
                saveProductsInList(list, doc.id)
            }
            .addOnFailureListener { e->
                addListWishPresenter.resultSaveListWishFirestore(false, e.toString(), null)
            }
    }

    private fun saveProductsInList(list: ListWish, id: String) {
        for (i in list.listProducts.indices) {
            val data = hashMapOf(
                Constants.NAME to list.listProducts[i].name,
                Constants.UNIT to list.listProducts[i].unit,
                Constants.USER_ID to list.listProducts[i].userId,
                Constants.PRICE to list.listProducts[i].price,
                Constants.QUANTITY to list.listProducts[i].quantity
            )
            FirestoreAPI.getProductsFromListWish(id).add(data)
                .addOnSuccessListener {
                    if (i == list.listProducts.indices.last) {
                        addListWishPresenter.resultSaveListWishFirestore(true, id, list)
                    }
                }
                .addOnFailureListener { e->
                    addListWishPresenter.resultSaveListWishFirestore(false, e.toString(), null)
                }
        }
    }

    override fun saveListWishRoom(id: String, list: ListWish) {
        val listWish = ListWish(
            id,
            list.name,
            list.userId,
            list.listProducts,
            list.total
        )
        launch {
            RoomDB.getInstance().listWish()
                .insert(listWish)
            addListWishPresenter.resultSaveListWishRoom()
        }
    }
}