package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import java.text.NumberFormat

/**
 * Created by Hector Chirinos on 23/03/2021.
 */
class ConfigNewShopAdapter(private var lists: MutableList<ListWish>):
    RecyclerView.Adapter<ConfigNewShopAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_new_shop_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lists[position]

        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item, NumberFormat.getInstance().format(item.total))
        holder.unit.text = context.getString(R.string.text_item_in_list, item.listProducts.size.toString())
    }

    override fun getItemCount(): Int = lists.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val unit: TextView = view.findViewById(R.id.tv_unit_items)
        val price: TextView = view.findViewById(R.id.tv_price)
        val switch: SwitchMaterial = view.findViewById(R.id.switch_shop)
    }

    fun updateList(newList: MutableList<ListWish>) {
        lists = newList
        notifyDataSetChanged()
    }
}