package com.skysam.hchirinos.go2shop.listsModule.interactor

import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.firebase.FirestoreAPI
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.listsModule.presenter.EditListWishPresenter

/**
 * Created by Hector Chirinos on 17/03/2021.
 */
class EditListWishInteractorClass(private val editListWishPresenter: EditListWishPresenter): EditListWishInteractor {

    override fun editListWish(
        list: ListWish,
        productsToSave: MutableList<ProductsToListModel>,
        productsToUpdate: MutableList<ProductsToListModel>,
        productsToDelete: MutableList<ProductsToListModel>
    ) {
        val data = hashMapOf<String, Any>(
            Constants.NAME to list.name,
            Constants.USER_ID to list.userId,
            Constants.TOTAL_LIST_WISH to list.total
        )

        FirestoreAPI.getListWish().document(list.id)
            .update(data)
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
                    deleteProducts(productsToDelete)
                }
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
                            deleteProducts(productsToDelete)
                            return@addOnSuccessListener
                        }
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
                            deleteProducts(productsToDelete)
                            return@addOnSuccessListener
                        }
                        editListWishPresenter.resultEditListWishFirestore(true, "")
                    }
                }
                .addOnFailureListener { e -> editListWishPresenter.resultEditListWishFirestore(false, e.toString()) }
        }
    }

    private fun deleteProducts(productsToDelete: MutableList<ProductsToListModel>) {
        for (i in productsToDelete.indices) {
            FirestoreAPI.getProductsFromListWish()
                .document(productsToDelete[i].id)
                .delete()
                .addOnSuccessListener {
                    if (i == productsToDelete.lastIndex) {
                        editListWishPresenter.resultEditListWishFirestore(true, "")
                    }
                }
                .addOnFailureListener { e -> editListWishPresenter.resultEditListWishFirestore(false, e.toString()) }
        }
    }
}