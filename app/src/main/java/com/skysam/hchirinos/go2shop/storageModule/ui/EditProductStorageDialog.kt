package com.skysam.hchirinos.go2shop.storageModule.ui

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
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.databinding.DialogAddProductStorageBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel

/**
 * Created by Hector Chirinos (Home) on 31/10/2021.
 */
class EditProductStorageDialog(private val product: StorageModel,
                               private val productsStoraged: MutableList<StorageModel>): DialogFragment() {
    private var _binding: DialogAddProductStorageBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddProductStorageBinding.inflate(layoutInflater)

        viewModel.products.observe(this.requireActivity(), {
            if (_binding != null) {
                val productsName = mutableListOf<String>()
                productsName.clear()
                for (i in it.indices) {
                    productsName.add(i, it[i].name)
                }
                val adapterSearchProduct = ArrayAdapter(requireContext(),
                    R.layout.list_autocomplete_text, productsName.sorted())
                binding.etName.setAdapter(adapterSearchProduct)
            }
        })

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_producto_dialog, product.name))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_edit, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits
        binding.etName.doAfterTextChanged { binding.tfName.error = null }

        binding.etName.setText(product.name)
        binding.spinner.setSelection(listUnits.indexOf(product.unit))

        binding.tfQuantity.visibility = View.GONE
        binding.tfPrice.visibility = View.GONE
        binding.ibAddQuantity.visibility = View.GONE
        binding.ibRestQuantity.visibility = View.GONE

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

    private fun validateEdit() {
        binding.tfName.error = null
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            binding.etName.requestFocus()
            return
        }
        if (binding.spinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), getString(R.string.error_spinner_units_position), Toast.LENGTH_SHORT).show()
            return
        }
        val unit = binding.spinner.selectedItem.toString()
        var exitis = false
        for (prod in productsStoraged) {
            if (name == prod.name && unit == prod.unit && prod.id != product.id) {
                binding.tfName.error = getString(R.string.error_name_repeated_storage)
                exitis = true
                break
            }
        }
        if (exitis) return

        Keyboard.close(binding.root)
        product.name = name
        product.unit = binding.spinner.selectedItem.toString()
        viewModel.editProductToStorage(product)
        dismiss()
    }
}