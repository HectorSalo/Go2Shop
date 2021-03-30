package com.skysam.hchirinos.go2shop.common.classView

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R

/**
 * Created by Hector Chirinos (Home) on 29/3/2021.
 */
class ConfirmationDialog(private val onClickShop: OnClickShop): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
                .setMessage(getString(R.string.msg_shop_dialog))
                .setPositiveButton(R.string.btn_shop) { _, _ ->
                    onClickShop.onClick()
                }
                .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        return dialog
    }
}