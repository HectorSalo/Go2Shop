package com.skysam.hchirinos.go2shop.productsModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.EditProduct
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import com.skysam.hchirinos.go2shop.databinding.DialogEditProductBinding
import com.skysam.hchirinos.go2shop.productsModule.presenter.EditProductPresenter
import com.skysam.hchirinos.go2shop.productsModule.presenter.EditProductPresenterClass
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 9/3/2021.
 */
class EditProductDialog(
    var product: Product, private val position: Int, private val fromList: Boolean,
    private val editProduct: EditProduct
): DialogFragment(), EditProductView {
    private var _binding: DialogEditProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var editProductPresenter: EditProductPresenter
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private lateinit var productResult: Product
    private var quantityTotal: Double = 1.0
    private var priceTotal: Double = 1.0
    private lateinit var unit: String
    private lateinit var name: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditProductBinding.inflate(layoutInflater)

        editProductPresenter = EditProductPresenterClass(this)

        if (!fromList) {
            binding.tfName.visibility = View.VISIBLE
            binding.tfQuantity.visibility = View.GONE
            binding.ibAddQuantity.visibility = View.GONE
            binding.ibRestQuantity.visibility = View.GONE
        }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_producto_dialog, product.name))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_edit, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        binding.etName.doAfterTextChanged { text->
            if (!text.isNullOrEmpty()) {
                name = text.toString()
            }
        }
        binding.etQuantity.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty()) {
                if (text.toString().toDouble() > 0) {
                    quantityTotal = text.toString().toDouble()
                }
            }
        }
        binding.etPrice.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty()) {
                if (text.toString().toDouble() >= 0) {
                    priceTotal = text.toString().toDouble()
                }
            }
        }

        /*binding.etPrice.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!binding.etPrice.text.isNullOrEmpty()) {
                    priceTotal = binding.etPrice.text.toString().toDouble()
                }
            }
        }*/

        binding.spinner.setOnTouchListener { v, _ ->
            v.performClick()
            binding.etPrice.clearFocus()
            false
        }
        binding.rgMoneda.setOnCheckedChangeListener { _, _ -> binding.etPrice.clearFocus() }
        binding.ibAddQuantity.setOnClickListener { addQuantity() }
        binding.ibRestQuantity.setOnClickListener { restQuantity() }

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateEdit() }

        loadData()

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadData() {
        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits
        name = product.name
        quantityTotal = product.quantity
        priceTotal = product.price
        unit = product.unit

        binding.etName.setText(product.name)
        binding.etPrice.setText(String.format("%.2f", product.price))
        binding.etQuantity.setText(product.quantity.toString())
        binding.spinner.setSelection(listUnits.indexOf(product.unit))
    }

    private fun restQuantity() {
        binding.etPrice.clearFocus()
        if (quantityTotal > 1) {
            quantityTotal -= 1
            binding.etQuantity.setText(quantityTotal.toString())
        }
    }

    private fun addQuantity() {
        binding.etPrice.clearFocus()
        quantityTotal += 1
        binding.etQuantity.setText(quantityTotal.toString())
    }

    private fun validateEdit() {
        binding.etPrice.clearFocus()
        if (binding.spinner.selectedItemPosition > 0) {
            unit = binding.spinner.selectedItem.toString()
        }
        if (binding.rbBolivar.isChecked) {
            val valueWeb = SharedPreferenceBD.getValue(AuthAPI.getCurrenUser()!!.uid)
            priceTotal /= valueWeb
        }
        productResult = Product(
            product.id,
            name,
            unit,
            product.userId,
            priceTotal,
            quantityTotal
        )
        Keyboard.close(binding.root)
        if (fromList) {
            editProduct.editProduct(position, productResult)
            dismiss()
        } else {
            dialog!!.setCanceledOnTouchOutside(false)
            binding.progressBar.visibility = View.VISIBLE
            binding.tfName.isEnabled = false
            binding.tfPrice.isEnabled = false
            buttonNegative.isEnabled = false
            buttonPositive.isEnabled = false
            editProductPresenter.editToFirestore(productResult)
        }
    }

    override fun resultEditToFirestore(statusOk: Boolean, msg: String) {
        if (_binding != null) {
            if (statusOk) {
                editProduct.editProduct(position, productResult)
                dismiss()
            } else {
                dialog!!.setCanceledOnTouchOutside(true)
                binding.progressBar.visibility = View.GONE
                binding.tfName.isEnabled = true
                binding.tfPrice.isEnabled = true
                buttonNegative.isEnabled = true
                buttonPositive.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    getString(R.string.save_data_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}