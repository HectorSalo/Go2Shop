package com.skysam.hchirinos.go2shop.common.classView

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.go2shop.R

/**
 * Created by Hector Chirinos (Home) on 9/3/2021.
 */
class ExitDialog(private val onClickExit: OnClickExit): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_exit_dialog))
            .setMessage(getString(R.string.msg_exit_dialog))
            .setPositiveButton(R.string.btn_exit) { _, _ ->
                onClickExit.onClickExit()
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        return dialog
    }
}