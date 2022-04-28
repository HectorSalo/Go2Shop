package com.skysam.hchirinos.go2shop.deparments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.Deparment

/**
 * Created by Hector Chirinos on 27/04/2022.
 */

class DeparmentAdapter(private var deparments: MutableList<Deparment>, private val onClick: OnClick):
 RecyclerView.Adapter<DeparmentAdapter.ViewHolder>() {
 private lateinit var context: Context

 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeparmentAdapter.ViewHolder {
  val view = LayoutInflater.from(parent.context)
   .inflate(R.layout.layout_deparment_item, parent, false)
  context = parent.context
  return ViewHolder(view)
 }

 override fun onBindViewHolder(holder: DeparmentAdapter.ViewHolder, position: Int) {
  holder.arrow.visibility = View.GONE
  holder.list.visibility = View.GONE

  val item = deparments[position]
  holder.name.text = item.name

  holder.card.setOnClickListener {
   val popMenu = PopupMenu(context, holder.card)
   popMenu.inflate(R.menu.menu_deparment_item)
   popMenu.setOnMenuItemClickListener {
    when(it.itemId) {
     R.id.menu_edit-> onClick.edit(item)
     R.id.menu_delete-> onClick.delete(item)
    }
    false
   }
   popMenu.show()
  }
 }

 override fun getItemCount(): Int = deparments.size

 inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val name: TextView = view.findViewById(R.id.tv_name)
  val arrow: ImageButton = view.findViewById(R.id.ib_arrow)
  val list: RecyclerView = view.findViewById(R.id.rv_list)
  val card: MaterialCardView = view.findViewById(R.id.card)
 }

 fun updateList(newList: MutableList<Deparment>) {
  deparments = newList
  notifyDataSetChanged()
 }
}