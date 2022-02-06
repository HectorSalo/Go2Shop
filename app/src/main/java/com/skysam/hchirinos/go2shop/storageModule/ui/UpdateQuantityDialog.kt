package com.skysam.hchirinos.go2shop.storageModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.databinding.DialogUpdateUnitStorageBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel

/**
 * Created by Hector Chirinos (Home) on 21/10/2021.
 */
class UpdateQuantityDialog(private val adding: Boolean, private val product: StorageModel):
    DialogFragment() {
    private var _binding: DialogUpdateUnitStorageBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogUpdateUnitStorageBinding.inflate(layoutInflater)

        val title = if (adding) getString(R.string.title_add_quantity_storage_dialog)
        else getString(R.string.title_remove_quantity_storage_dialog)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
            .setView(binding.root)
            .setPositiveButton(R.string.btn_update, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateData() }
        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateData() {
        val quantity: Double
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
        if (!adding && (product.quantityRemaining - quantity) < 0) {
            binding.tfQuantity.error = getString(R.string.error_quantity_negative)
            return
        }
        if (adding) {
            product.quantityRemaining = product.quantityRemaining + quantity
        } else {
            product.quantityRemaining = product.quantityRemaining - quantity
        }
        viewModel.updateUnitFromProductToStorage(product)
        dismiss()
    }
}