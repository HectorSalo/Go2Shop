package com.skysam.hchirinos.go2shop.listsModule.ui.addListWish

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.room.entities.Product


class AddWishListAdapter(
    private val products: MutableList<Product>
) : RecyclerView.Adapter<AddWishListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = item.price.toString()
        holder.unit.text = "${item.quantity} ${item.unit}"
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_item)
        val unit: TextView = view.findViewById(R.id.tv_unit)
        val price: TextView = view.findViewById(R.id.tv_price)
    }
}