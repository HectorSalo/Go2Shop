package com.skysam.hchirinos.go2shop.productsModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.ClassesCommon
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.UpdatedProduct
import com.skysam.hchirinos.go2shop.common.models.Product
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD
import com.skysam.hchirinos.go2shop.databinding.DialogEditProductBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel
import java.util.Locale

/**
 * Created by Hector Chirinos (Home) on 9/3/2021.
 */
class EditProductDialog(
    var product: Product, private val position: Int, private val fromList: Boolean,
    private val updatedProduct: UpdatedProduct, private val rateChange: Double?, private val shop: Boolean):
    DialogFragment(), TextWatcher {
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
        if (shop) binding.spinnerDeparment.visibility = View.GONE

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
        binding.etPrice.addTextChangedListener(this)
        binding.etQuantity.addTextChangedListener(this)

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
        binding.etPrice.setText(ClassesCommon.convertDoubleToString(product.price))
        binding.etQuantity.setText(ClassesCommon.convertDoubleToString(product.quantity))
        binding.spinner.setSelection(listUnits.indexOf(product.unit))
        if (product.deparment.isNotEmpty()) {
            binding.spinnerDeparment.setSelection(listDepar.indexOf(product.deparment))
        }
    }

    private fun restQuantity() {
        if (quantityTotal > 1) {
            quantityTotal -= 1
            binding.etQuantity.setText(ClassesCommon.convertDoubleToString(quantityTotal))
        }
    }

    private fun addQuantity() {
        quantityTotal += 1
        binding.etQuantity.setText(ClassesCommon.convertDoubleToString(quantityTotal))
    }

    private fun validateEdit() {
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        var cadena = s.toString()
        cadena = cadena.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100
        cadena = String.format(Locale.GERMANY, "%,.2f", cantidad)

        if (s.toString() == binding.etPrice.text.toString()) {
            binding.etPrice.removeTextChangedListener(this)
            binding.etPrice.setText(cadena)
            binding.etPrice.setSelection(cadena.length)
            binding.etPrice.addTextChangedListener(this)
            priceTotal = ClassesCommon.convertStringToDouble(cadena)
        }
        if (s.toString() == binding.etQuantity.text.toString()) {
            binding.etQuantity.removeTextChangedListener(this)
            binding.etQuantity.setText(cadena)
            binding.etQuantity.setSelection(cadena.length)
            binding.etQuantity.addTextChangedListener(this)
            quantityTotal = ClassesCommon.convertStringToDouble(cadena)
        }
    }
}