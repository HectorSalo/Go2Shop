package com.skysam.hchirinos.go2shop.productsModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.ProductSaveFromList
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.common.models.Product
import com.skysam.hchirinos.go2shop.databinding.DialogAddProductBinding

class AddProductDialog(private val name: String?, private val productSaveFromList: ProductSaveFromList,
    private val products: MutableList<Product>):
    DialogFragment() {
    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var product: Product
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddProductBinding.inflate(layoutInflater)

        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits
        binding.etName.doAfterTextChanged { binding.tfName.error = null }

        if (!name.isNullOrEmpty()) binding.etName.setText(name)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_add_producto_dialog))
            .setView(binding.root)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateProduct() {
        binding.tfName.error = null
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            return
        }
        for (i in products.indices) {
            if (products[i].name.equals(name, true)) {
                binding.tfName.error = getString(R.string.error_name_exists)
                return
            }
        }
        Keyboard.close(binding.root)
        if (binding.spinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), getString(R.string.error_spinner_units_position), Toast.LENGTH_SHORT).show()
            return
        }
        product = Product(Constants.USER_ID,
            name,
            binding.spinner.selectedItem.toString(),
            AuthAPI.getCurrenUser()!!.uid)
        dialog?.dismiss()
        productSaveFromList.productSave(product)
    }
}