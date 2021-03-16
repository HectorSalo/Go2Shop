package com.skysam.hchirinos.go2shop.listsModule.interactor

import com.skysam.hchirinos.go2shop.database.room.RoomDB
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
}