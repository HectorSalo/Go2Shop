package com.skysam.hchirinos.go2shop.listsModule.interactor

import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.classView.ProductsSavedToList
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.RoomDB
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.listsModule.presenter.EditListWishPresenter
import java.util.*

/**
 * Created by Hector Chirinos on 17/03/2021.
 */
class EditListWishInteractorClass(private val editListWishPresenter: EditListWishPresenter): EditListWishInteractor, ProductsSavedToList {

    private lateinit var listToUpdate: ListWish

    override fun editListWish(
        list: ListWish,
        productsToSave: MutableList<ProductsToListModel>,
        productsToUpdate: MutableList<ProductsToListModel>,
        productsToDelete: MutableList<ProductsToListModel>
    ) {
        listToUpdate = list
        val dateCreated = Date(list.dateCreated)
        val dateEdited = Date(list.lastEdited)
        val data = hashMapOf(
            Constants.NAME to list.name,
            Constants.USER_ID to list.userId,
            Constants.TOTAL_LIST_WISH to list.total,
            Constants.DATE_CREATED to dateCreated,
            Constants.DATE_LAST_EDITED to dateEdited
        )

        FirestoreAPI.getListWish().document(list.id)
            .set(data)
            .addOnSuccessListener {
                if (productsToSave.isNotEmpty()) {
                    saveProducts(list.id, productsToSave, productsToUpdate, productsToDelete)
                    return@addOnSuccessListener
                }
                if (productsToUpdate.isNotEmpty()) {
                    updateProducts(list.id, productsToUpdate, productsToDelete)
                    return@addOnSuccessListener
                }
                if (productsToDelete.isNotEmpty()) {
                    deleteProducts(list.id, productsToDelete)
                }
                FirestoreAPI.getProductsToListWishFromFirestore(list.id, list, this)
                editListWishPresenter.resultEditListWishFirestore(true, "")
            }
            .addOnFailureListener { e -> editListWishPresenter.resultEditListWishFirestore(false, e.toString()) }
    }

    private fun saveProducts(id: String, productsToSave: MutableList<ProductsToListModel>,
                             productsToUpdate: MutableList<ProductsToListModel>,
                             productsToDelete: MutableList<ProductsToListModel>) {
        for (i in productsToSave.indices) {
            val data = hashMapOf(
                Constants.NAME to productsToSave[i].name,
                Constants.UNIT to productsToSave[i].unit,
                Constants.USER_ID to productsToSave[i].userId,
                Constants.LIST_ID to id,
                Constants.PRICE to productsToSave[i].price,
                Constants.QUANTITY to productsToSave[i].quantity
            )

            FirestoreAPI.getProductsFromListWish()
                .add(data)
                .addOnSuccessListener {
                    if (i == productsToSave.lastIndex) {
                        if (productsToUpdate.isNotEmpty()) {
                            updateProducts(id, productsToUpdate, productsToDelete)
                            return@addOnSuccessListener
                        }
                        if (productsToDelete.isNotEmpty()) {
                            deleteProducts(id, productsToDelete)
                            return@addOnSuccessListener
                        }
                        FirestoreAPI.getProductsToListWishFromFirestore(id, listToUpdate, this)
                        editListWishPresenter.resultEditListWishFirestore(true, "")
                    }
                }
                .addOnFailureListener { e -> editListWishPresenter.resultEditListWishFirestore(false, e.toString()) }
        }
    }

    private fun updateProducts(id: String,
        productsToUpdate: MutableList<ProductsToListModel>,
        productsToDelete: MutableList<ProductsToListModel>
    ) {
        for (i in productsToUpdate.indices) {
            val data = hashMapOf(
                Constants.NAME to productsToUpdate[i].name,
                Constants.UNIT to productsToUpdate[i].unit,
                Constants.USER_ID to productsToUpdate[i].userId,
                Constants.LIST_ID to id,
                Constants.PRICE to productsToUpdate[i].price,
                Constants.QUANTITY to productsToUpdate[i].quantity
            )

            FirestoreAPI.getProductsFromListWish()
                .document(productsToUpdate[i].id)
                .set(data)
                .addOnSuccessListener {
                    if (i == productsToUpdate.lastIndex) {
                        if (productsToDelete.isNotEmpty()) {
                            deleteProducts(id, productsToDelete)
                            return@addOnSuccessListener
                        }
                        FirestoreAPI.getProductsToListWishFromFirestore(id, listToUpdate, this)
                        editListWishPresenter.resultEditListWishFirestore(true, "")
                    }
                }
                .addOnFailureListener { e -> editListWishPresenter.resultEditListWishFirestore(false, e.toString()) }
        }
    }

    private fun deleteProducts(id:String, productsToDelete: MutableList<ProductsToListModel>) {
        for (i in productsToDelete.indices) {
            FirestoreAPI.getProductsFromListWish()
                .document(productsToDelete[i].id)
                .delete()
                .addOnSuccessListener {
                    if (i == productsToDelete.lastIndex) {
                        FirestoreAPI.getProductsToListWishFromFirestore(id, listToUpdate, this)
                    }
                }
                .addOnFailureListener { e -> editListWishPresenter.resultEditListWishFirestore(false, e.toString()) }
        }
    }

    override fun savedListWish(listWish: ListWish) {
        RoomDB.updateListWishToRoom(listWish)
        editListWishPresenter.resultEditListWishFirestore(true, "")
    }

    override fun savedListShop(listShop: Shop) {

    }
}