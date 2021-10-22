package com.skysam.hchirinos.go2shop.storageModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.databinding.DialogAddProductStorageBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 22/10/2021.
 */
class DialogAddProductStorage: DialogFragment() {
    private var _binding: DialogAddProductStorageBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private var quantityTotal: Double = 1.0
    private var price: Double = 1.0
    private lateinit var unit: String
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddProductStorageBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_add_producto_dialog))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_add_product, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits

        binding.etName.doAfterTextChanged { binding.tfName.error = null }
        binding.etQuantity.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty()) {
                if (text.toString().toDouble() > 0) {
                    quantityTotal = text.toString().toDouble()
                }
            }
        }

        binding.etPrice.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!binding.etPrice.text.isNullOrEmpty()) {
                    var priceString = binding.etPrice.text.toString()
                    try {
                        priceString = priceString.replace(",", ".")
                        price = priceString.toDouble()
                        binding.etPrice.setText(NumberFormat.getInstance().format(price))
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error en el número ingresado", Toast.LENGTH_SHORT).show()
                        binding.etPrice.setText(NumberFormat.getInstance().format(price))
                    }
                }
            }
        }
        binding.spinner.setOnTouchListener { v, _ ->
            v.performClick()
            binding.etPrice.clearFocus()
            false
        }
        binding.ibAddQuantity.setOnClickListener { addQuantity() }
        binding.ibRestQuantity.setOnClickListener { restQuantity() }

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateEdit() }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val quantity: Double
        binding.etPrice.clearFocus()
        binding.tfName.error = null
        binding.tfPrice.error = null
        binding.tfQuantity.error = null
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            binding.etName.requestFocus()
            return
        }
        val priceS = binding.etPrice.text.toString().trim()
        if (priceS.isEmpty()) {
            binding.tfPrice.error = getString(R.string.error_field_empty)
            return
        }
        var quantityS = binding.etQuantity.text.toString().trim()
        if (quantityS.isEmpty()) {
            binding.tfQuantity.error = getString(R.string.error_field_empty)
            return
        }
        try {
            quantityS = quantityS.replace(",",".")
            quantity = quantityS.toDouble()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error en el número ingresado", Toast.LENGTH_SHORT).show()
            return
        }
        if (quantity <= 0) {
            binding.tfQuantity.error = getString(R.string.error_field_zero)
            return
        }
        if (binding.spinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), getString(R.string.error_spinner_units_position), Toast.LENGTH_SHORT).show()
            return
        }
        unit = binding.spinner.selectedItem.toString()
        val productResult = StorageModel(
            Constants.USER_ID,
            name,
            unit,
            AuthAPI.getCurrenUser()!!.uid,
            quantity,
            Date(),
            quantity,
            price
        )
        viewModel.addProductToStorage(productResult)
        Keyboard.close(binding.root)
        dismiss()
    }
}