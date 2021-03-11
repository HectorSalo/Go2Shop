package com.skysam.hchirinos.go2shop.productsModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.ProductSaveFromList
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.DialogAddProductBinding
import com.skysam.hchirinos.go2shop.productsModule.presenter.AddProductPresenter
import com.skysam.hchirinos.go2shop.productsModule.presenter.AddProductPresenterClass

class AddProductDialog(private val name: String?, private val productSaveFromList: ProductSaveFromList?): DialogFragment(), AddProductView {
    private lateinit var dialogAddProductBinding: DialogAddProductBinding
    private lateinit var addProductPresenter: AddProductPresenter
    private lateinit var product: Product
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogAddProductBinding = DialogAddProductBinding.inflate(layoutInflater)
        addProductPresenter = AddProductPresenterClass(this)

        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        dialogAddProductBinding.spinner.adapter = adapterUnits

        if (!name.isNullOrEmpty()) dialogAddProductBinding.etName.setText(name)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_add_producto_dialog))
            .setView(dialogAddProductBinding.root)
            .setPositiveButton(R.string.btn_save, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateProduct() }
        return dialog
    }

    private fun validateProduct() {
        dialogAddProductBinding.tfName.error = null
        val name = dialogAddProductBinding.etName.text.toString()
        if (name.isEmpty()) {
            dialogAddProductBinding.tfName.error = getString(R.string.error_field_empty)
            return
        }
        Keyboard.close(dialogAddProductBinding.root)
        if (dialogAddProductBinding.spinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), getString(R.string.error_spinner_units_position), Toast.LENGTH_SHORT).show()
            return
        }
        product = Product(Constants.USER_ID, name, dialogAddProductBinding.spinner.selectedItem.toString(),
        AuthAPI.getCurrenUser()!!.uid)
        dialog!!.setCanceledOnTouchOutside(false)
        dialogAddProductBinding.tfName.isEnabled = false
        dialogAddProductBinding.spinner.isEnabled = false
        buttonPositive.isEnabled = false
        buttonNegative.isEnabled = false
        dialogAddProductBinding.progressBar.visibility = View.VISIBLE
        addProductPresenter.saveProductToFirestore(product)
    }

    override fun resultSaveProduct(statusOk: Boolean, msg: String) {
        dialogAddProductBinding.progressBar.visibility = View.GONE
        if (statusOk) {
            Toast.makeText(requireContext(), getString(R.string.save_data_ok), Toast.LENGTH_SHORT).show()
            productSaveFromList?.productSave(product)
            dialog!!.dismiss()
        } else {
            dialog!!.setCanceledOnTouchOutside(true)
            dialogAddProductBinding.tfName.isEnabled = true
            dialogAddProductBinding.spinner.isEnabled = true
            buttonPositive.isEnabled = true
            buttonNegative.isEnabled = true
            dialogAddProductBinding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), getString(R.string.save_data_error), Toast.LENGTH_SHORT).show()
        }
    }
}