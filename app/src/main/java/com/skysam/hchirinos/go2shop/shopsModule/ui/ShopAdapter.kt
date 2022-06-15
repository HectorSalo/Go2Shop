package com.skysam.hchirinos.go2shop.shopsModule.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.common.models.Shop
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 30/3/2021.
 */
class ShopAdapter(private var lists: MutableList<Shop>, private val onClickList: OnClickList): RecyclerView.Adapter<ShopAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopAdapter.ViewHolder, position: Int) {
        val item = lists[position]
        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item, NumberFormat.getInstance().format(item.total))
        holder.listDate.text = DateFormat.getDateInstance().format(Date(item.dateCreated))

        holder.card.setOnClickListener { onClickList.onClickEdit(position) }
    }

    override fun getItemCount(): Int = lists.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val listDate: TextView = view.findViewById(R.id.tv_items)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<Shop>) {
        lists = newList
        notifyDataSetChanged()
    }
}