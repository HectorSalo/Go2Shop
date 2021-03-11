package com.skysam.hchirinos.go2shop.productsModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.EditProduct
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import com.skysam.hchirinos.go2shop.databinding.DialogEditProductBinding
import com.skysam.hchirinos.go2shop.productsModule.presenter.EditProductPresenter
import com.skysam.hchirinos.go2shop.productsModule.presenter.EditProductPresenterClass

/**
 * Created by Hector Chirinos (Home) on 9/3/2021.
 */
class EditProductDialog(var product: Product, private val position: Int, private val fromList: Boolean,
                        private val editProduct: EditProduct): DialogFragment(), EditProductView {
    private lateinit var dialogEditProductBinding: DialogEditProductBinding
    private lateinit var editProductPresenter: EditProductPresenter
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private lateinit var productResult: Product
    private var quantityTotal: Double = 1.0
    private var priceTotal: Double = 1.0
    private lateinit var unit: String
    private lateinit var name: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogEditProductBinding = DialogEditProductBinding.inflate(layoutInflater)

        editProductPresenter = EditProductPresenterClass(this)

        if (!fromList) {
            dialogEditProductBinding.tfName.visibility = View.VISIBLE
            dialogEditProductBinding.tfQuantity.visibility = View.GONE
            dialogEditProductBinding.ibAddQuantity.visibility = View.GONE
            dialogEditProductBinding.ibRestQuantity.visibility = View.GONE
        }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_producto_dialog, product.name))
            .setView(dialogEditProductBinding.root)
            .setPositiveButton(R.string.btn_edit, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        dialogEditProductBinding.etName.doAfterTextChanged { text->
            if (!text.isNullOrEmpty()) {
                name = text.toString()
            }
        }
        dialogEditProductBinding.etQuantity.doAfterTextChanged {text ->
            if (!text.isNullOrEmpty()) {
                if (text.toString().toDouble() > 0) {
                    quantityTotal = text.toString().toDouble()
                }
            }
        }
        dialogEditProductBinding.etPrice.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty()) {
                if (text.toString().toDouble() > 0) {
                    priceTotal = text.toString().toDouble()
                }
            }
        }

        dialogEditProductBinding.ibAddQuantity.setOnClickListener { addQuantity() }
        dialogEditProductBinding.ibRestQuantity.setOnClickListener { restQuantity() }

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateEdit() }

        loadData()

        return dialog
    }

    private fun loadData() {
        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        dialogEditProductBinding.spinner.adapter = adapterUnits
        name = product.name
        quantityTotal = product.quantity
        priceTotal = product.price
        unit = product.unit

        dialogEditProductBinding.etName.setText(product.name)
        dialogEditProductBinding.etPrice.setText(product.price.toString())
        dialogEditProductBinding.etQuantity.setText(product.quantity.toString())
        dialogEditProductBinding.spinner.setSelection(listUnits.indexOf(product.unit))
    }

    private fun restQuantity() {
        if (quantityTotal > 1) {
            quantityTotal -= 1
            dialogEditProductBinding.etQuantity.setText(quantityTotal.toString())
        }
    }

    private fun addQuantity() {
        quantityTotal += 1
        dialogEditProductBinding.etQuantity.setText(quantityTotal.toString())
    }

    private fun validateEdit() {
        if (dialogEditProductBinding.spinner.selectedItemPosition > 0) {
            unit = dialogEditProductBinding.spinner.selectedItem.toString()
        }
        if (dialogEditProductBinding.rbBolivar.isChecked) {
            val valueWeb = SharedPreferenceBD.getValue(AuthAPI.getCurrenUser()!!.uid)
            priceTotal /= valueWeb
        }
        productResult = Product(
        product.id,
        name,
        unit,
        product.userId,
        priceTotal,
        quantityTotal)
        Keyboard.close(dialogEditProductBinding.root)
        if (fromList) {
            editProduct.editProduct(position, productResult)
            dismiss()
        } else {
            dialog!!.setCanceledOnTouchOutside(false)
            dialogEditProductBinding.progressBar.visibility = View.VISIBLE
            dialogEditProductBinding.tfName.isEnabled = false
            dialogEditProductBinding.tfPrice.isEnabled = false
            buttonNegative.isEnabled = false
            buttonPositive.isEnabled = false
            editProductPresenter.editToFirestore(productResult)
        }
    }

    override fun resultEditToFirestore(statusOk: Boolean, msg: String) {
        if (statusOk) {
            editProduct.editProduct(position, productResult)
            dismiss()
        } else {
            dialog!!.setCanceledOnTouchOutside(true)
            dialogEditProductBinding.progressBar.visibility = View.GONE
            dialogEditProductBinding.tfName.isEnabled = true
            dialogEditProductBinding.tfPrice.isEnabled = true
            buttonNegative.isEnabled = true
            buttonPositive.isEnabled = true
            Toast.makeText(requireContext(), getString(R.string.save_data_error), Toast.LENGTH_SHORT).show()
        }
    }
}