package com.skysam.hchirinos.go2shop.deparments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.models.Deparment
import com.skysam.hchirinos.go2shop.databinding.DialogAddDeparmentBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel

/**
 * Created by Hector Chirinos on 27/04/2022.
 */

class EditDeparmentDialog: DialogFragment() {
 private var _binding: DialogAddDeparmentBinding? = null
 private val binding get() = _binding!!
 private val viewModel: MainViewModel by activityViewModels()
 private val deparments = mutableListOf<Deparment>()
 private lateinit var buttonPositive: Button
 private lateinit var buttonNegative: Button
 private lateinit var deparment: Deparment

 override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
  _binding = DialogAddDeparmentBinding.inflate(layoutInflater)

  viewModel.deparments.observe(this.requireActivity()) {
   if (_binding != null) {
    if (it.isNotEmpty()) {
     deparments.clear()
     deparments.addAll(it)
    }
   }
  }
  viewModel.deparmentToEdit.observe(this.requireActivity()) {
   if (_binding != null) {
    deparment = it
    binding.etDeparment.setText(deparment.name)
   }
  }
  binding.etDeparment.doAfterTextChanged { binding.tfDeparment.error = null }

  val builder = AlertDialog.Builder(requireActivity())
  builder.setTitle(getString(R.string.title_edit_deparment_dialog))
   .setView(binding.root)
   .setPositiveButton(R.string.btn_edit, null)
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
  binding.tfDeparment.error = null
  val name = binding.etDeparment.text.toString().trim()
  if (name.isEmpty()) {
   binding.tfDeparment.error = getString(R.string.error_field_empty)
   return
  }
  for (dep in deparments) {
   if (dep.name.equals(name, true) && !deparment.name.equals(name, true)) {
    binding.tfDeparment.error = getString(R.string.error_name_exists)
    return
   }
  }
  Keyboard.close(binding.root)

  val departmentUpdate = Deparment(
   deparment.id,
   name,
   deparment.userId
  )

  viewModel.editDeparment(departmentUpdate)
  dialog?.dismiss()
 }
}