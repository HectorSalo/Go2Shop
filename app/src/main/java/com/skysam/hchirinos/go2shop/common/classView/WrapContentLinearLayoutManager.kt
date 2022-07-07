package com.skysam.hchirinos.go2shop.common.classView

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Hector Chirinos on 27/04/2022.
 */

class WrapContentLinearLayoutManager(context: Context?, orientation: Int, reverseLayout: Boolean) :
 LinearLayoutManager(context, orientation, reverseLayout) {

 override fun supportsPredictiveItemAnimations(): Boolean {
  return false
 }

 override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
  try {
   super.onLayoutChildren(recycler, state)
  } catch (ex: Exception) {
   ex.printStackTrace()
  }
 }
}