package com.skysam.hchirinos.go2shop.storageModule.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.databinding.DialogViewDetailsStorageBinding
import java.text.DateFormat

/**
 * Created by Hector Chirinos (Home) on 21/10/2021.
 */
class DialogViewDetails(private val product: StorageModel): DialogFragment() {
    private var _binding: DialogViewDetailsStorageBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogViewDetailsStorageBinding.inflate(layoutInflater)

        binding.tvDateShop.text = getString(R.string.text_date_last_shop,
            DateFormat.getInstance().format(product.dateShop))
        binding.tvQuantityShop.text = getString(R.string.text_products_from_shop,
            product.quantityFromShop.toString(), product.unit)
        if (product.price > 0) {
            binding.tvPrice.text = getString(R.string.text_price_by_unit, product.price.toString())
        }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_view_details_storage_dialog))
            .setView(binding.root)
            .setNegativeButton(R.string.btn_exit, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}