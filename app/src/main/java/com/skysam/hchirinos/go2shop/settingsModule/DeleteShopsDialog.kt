package com.skysam.hchirinos.go2shop.settingsModule

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.repositories.ShopRepository

/**
 * Created by Hector Chirinos on 28/05/2021.
 */
class DeleteShopsDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.title_delete_shops)
            .setMessage(R.string.message_dialog_delete_shops)
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                ShopRepository.deleteShops()
                Toast.makeText(requireContext(), R.string.delete_data_ok, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
        return dialog
    }
}