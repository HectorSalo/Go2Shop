package com.skysam.hchirinos.go2shop.common.classView

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.databinding.DialogAddProductBinding

class AddProductDialog: DialogFragment() {
    private lateinit var dialogAddProductBinding: DialogAddProductBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogAddProductBinding = DialogAddProductBinding.inflate(layoutInflater)

        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        dialogAddProductBinding.spinner.adapter = adapterUnits

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_add_producto_dialog))
            .setView(dialogAddProductBinding.root)
            .setPositiveButton(R.string.btn_save, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        val buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateProduct() }
        return dialog
    }

    private fun validateProduct() {
        dialogAddProductBinding.tfName.error = null
        val product = dialogAddProductBinding.etName.text.toString()
        if (product.isEmpty()) {
            dialogAddProductBinding.tfName.error = getString(R.string.error_field_empty)
            return
        }
        Keyboard.close(dialogAddProductBinding.root)
        if (dialogAddProductBinding.spinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), getString(R.string.error_spinner_units_position), Toast.LENGTH_SHORT).show()
            return
        }
        dialog!!.dismiss()
    }
}