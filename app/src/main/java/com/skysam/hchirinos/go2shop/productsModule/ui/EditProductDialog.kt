package com.skysam.hchirinos.go2shop.productsModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.EditProductFromList
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import com.skysam.hchirinos.go2shop.databinding.DialogEditProductBinding

/**
 * Created by Hector Chirinos (Home) on 9/3/2021.
 */
class EditProductDialog(var product: Product, private val position: Int, val fromList: Boolean,
                        private val editProductFromList: EditProductFromList): DialogFragment() {
    private lateinit var dialogEditProductBinding: DialogEditProductBinding
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private var quantityTotal: Double = 0.0
    private var priceTotal: Double = 0.0
    private lateinit var unit: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogEditProductBinding = DialogEditProductBinding.inflate(layoutInflater)

        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        dialogEditProductBinding.spinner.adapter = adapterUnits
        quantityTotal = product.quantity
        priceTotal = product.price
        unit = product.unit

        dialogEditProductBinding.etPrice.setText(product.price.toString())
        dialogEditProductBinding.etQuantity.setText(product.quantity.toString())
        dialogEditProductBinding.spinner.setSelection(listUnits.indexOf(product.unit))

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_producto_dialog))
            .setView(dialogEditProductBinding.root)
            .setPositiveButton(R.string.btn_edit, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

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

        return dialog
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
        val productResult = Product(
        product.id,
        product.name,
        unit,
        product.userId,
        priceTotal,
        quantityTotal)
        Keyboard.close(dialogEditProductBinding.root)
        editProductFromList.editProduct(position, productResult)
        dismiss()
    }
}