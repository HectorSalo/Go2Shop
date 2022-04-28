package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.Deparment

/**
 * Created by Hector Chirinos on 28/04/2022.
 */

class DeparmentShopAdapter(private val deparments: MutableList<Deparment>): RecyclerView.Adapter<DeparmentShopAdapter.ViewHolder>() {
 private lateinit var context: Context

 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeparmentShopAdapter.ViewHolder {
  val view = LayoutInflater.from(parent.context)
   .inflate(R.layout.layout_deparment_item, parent, false)
  context = parent.context
  return ViewHolder(view)
 }

 override fun onBindViewHolder(holder: DeparmentShopAdapter.ViewHolder, position: Int) {
  val item = deparments[position]
  holder.name.text = item.name

  if (item.isExtended) {
   holder.arrow.setImageResource(R.drawable.ic_arrow_up_24)
   holder.list.visibility = View.VISIBLE
  } else {
   holder.arrow.setImageResource(R.drawable.ic_arrow_down_24)
   holder.list.visibility = View.GONE
  }

  holder.arrow.setOnClickListener {
   if (item.isExtended) {
    holder.arrow.setImageResource(R.drawable.ic_arrow_up_24)
    holder.list.visibility = View.VISIBLE
    item.isExtended = false
   } else {
    holder.arrow.setImageResource(R.drawable.ic_arrow_down_24)
    holder.list.visibility = View.GONE
    item.isExtended = true
   }
   notifyItemChanged(position)
  }
 }

 override fun getItemCount(): Int = deparments.size

 inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val name: TextView = view.findViewById(R.id.tv_name)
  val arrow: ImageButton = view.findViewById(R.id.ib_arrow)
  val list: RecyclerView = view.findViewById(R.id.rv_list)
 }
}