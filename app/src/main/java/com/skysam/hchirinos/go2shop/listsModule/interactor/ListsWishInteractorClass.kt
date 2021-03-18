package com.skysam.hchirinos.go2shop.listsModule.interactor

import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListsWishPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos on 16/03/2021.
 */
class ListsWishInteractorClass(private val listsWishPresenter: ListsWishPresenter): ListsWishInteractor, CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun getLists() {
        launch {
            val lists = RoomDB.getInstance().listWish()
                .getAll()
            listsWishPresenter.resultGetLists(lists)
        }
    }

    override fun deleteLists(lists: MutableList<ListWish>) {
        val tt = lists.size
        for (i in lists.indices) {
            val lastIndexFromList = lists.lastIndex
            for (j in lists[i].listProducts.indices) {
                val lastIndexFromProducts = lists[i].listProducts.lastIndex
                FirestoreAPI.getProductsFromListWish()
                    .document(lists[i].listProducts[j].id)
                    .delete()
                    .addOnSuccessListener {
                        if ((i == lastIndexFromList) && (j == lastIndexFromProducts)) {
                            val t = lists.size
                            deleteData(lists)
                        }
                    }
                    .addOnFailureListener { e -> listsWishPresenter.resultDeleteLists(false, e.toString()) }
            }
        }
    }

    private fun deleteData(lists: MutableList<ListWish>) {
        for (i in lists.indices) {
            FirestoreAPI.getListWish()
                .document(lists[i].id)
                .delete()
                .addOnSuccessListener {
                    if (i == lists.lastIndex) {
                        listsWishPresenter.resultDeleteLists(true, "")
                    }
                }
                .addOnFailureListener { e -> listsWishPresenter.resultDeleteLists(false, e.toString()) }
        }
    }
}