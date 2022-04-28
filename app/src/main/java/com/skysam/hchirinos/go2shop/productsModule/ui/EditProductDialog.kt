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
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.UpdatedProduct
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import com.skysam.hchirinos.go2shop.databinding.DialogEditProductBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

/**
 * Created by Hector Chirinos (Home) on 9/3/2021.
 */
class EditProductDialog(
    var product: Product, private val position: Int, private val fromList: Boolean,
    private val updatedProduct: UpdatedProduct, private val rateChange: Double?):
    DialogFragment() {
    private var _binding: DialogEditProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private lateinit var productResult: Product
    private var quantityTotal: Double = 0.0
    private var priceTotal: Double = 1.0
    private lateinit var unit: String
    private lateinit var name: String
    private lateinit var deparment: String
    private val listDepar = mutableListOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditProductBinding.inflate(layoutInflater)

        val decimalSeparator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
        NumberFormat.getInstance().isGroupingUsed = true

        viewModel.deparments.observe(this.requireActivity()) {
            if (_binding != null) {
                listDepar.clear()
                listDepar.add("Sin departamento")
                for (dep in it) {
                    listDepar.add(dep.name)
                }
                val depNames = ArrayAdapter(requireContext(), R.layout.layout_spinner, listDepar)
                binding.spinnerDeparment.adapter = depNames
            }
        }

        if (!fromList) {
            binding.tfName.visibility = View.VISIBLE
            binding.tfQuantity.visibility = View.GONE
            binding.ibAddQuantity.visibility = View.GONE
            binding.ibRestQuantity.visibility = View.GONE
            binding.spinnerDeparment.visibility = View.GONE
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
                var quantity = text.toString()
                if (quantity.contains(".")) {
                    quantity = quantity.replace(".", ",")
                    binding.etQuantity.setText(quantity)
                    return@doAfterTextChanged
                }
                quantity = quantity.replace(",", ".")
                try {
                    if (quantity.toDouble() > 0) {
                        quantityTotal = quantity.toDouble()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error en el número ingresado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.etPrice.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!binding.etPrice.text.isNullOrEmpty()) {
                    var priceString = binding.etPrice.text.toString()
                    if (decimalSeparator == ",") {
                        try {
                            priceString = priceString.replace(".", "").replace(",", ".")
                            priceTotal = priceString.toDouble()
                            binding.etPrice.setText(NumberFormat.getInstance().format(priceTotal))
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error en el número ingresado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        try {
                            priceString = priceString.replace(",", "")
                            priceTotal = priceString.toDouble()
                            binding.etPrice.setText(NumberFormat.getInstance().format(priceTotal))
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error en el número ingresado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.spinner.setOnTouchListener { v, _ ->
            v.performClick()
            binding.etPrice.clearFocus()
            false
        }
        binding.spinnerDeparment.setOnTouchListener { v, _ ->
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
        binding.etPrice.setText(NumberFormat.getInstance().format(product.price))
        binding.etQuantity.setText(product.quantity.toString())
        binding.spinner.setSelection(listUnits.indexOf(product.unit))
        if (product.deparment.isNotEmpty()) {
            binding.spinnerDeparment.setSelection(listDepar.indexOf(product.deparment))
        }
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
        deparment = if (binding.spinnerDeparment.selectedItemPosition > 0) {
            binding.spinnerDeparment.selectedItem.toString()
        } else ""
        if (binding.rbBolivar.isChecked) {
            val value: Double =
                rateChange ?: SharedPreferenceBD.getValue(AuthAPI.getCurrenUser()!!.uid).toDouble()
            priceTotal /= value
        }
        productResult = Product(
            product.id,
            name,
            unit,
            product.userId,
            priceTotal,
            quantityTotal,
            deparment
        )
        Keyboard.close(binding.root)
        updatedProduct.updatedProduct(position, productResult)
        dismiss()
    }
}