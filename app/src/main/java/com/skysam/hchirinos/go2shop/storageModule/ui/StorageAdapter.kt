package com.skysam.hchirinos.go2shop.storageModule.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import java.text.DateFormat

class StorageAdapter(private var products: MutableList<StorageModel>):
    RecyclerView.Adapter<StorageAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_storage, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StorageAdapter.ViewHolder, position: Int) {
       val item = products[position]
        holder.name.text = item.name
        holder.remaining.text = context.getString(R.string.text_products_remaining,
            item.quantityRemaining.toString(), item.unit)
        holder.quantityShop.text = context.getString(R.string.text_products_from_shop,
            item.quantityFromShop.toString(), item.unit)
        holder.date.text = context.getString(R.string.text_date_last_shop,
            DateFormat.getInstance().format(item.dateShop))
        if (item.isExpanded) {
            holder.btnDetails.setImageResource(R.drawable.ic_close_view_details_24)
            holder.expandable.visibility = View.VISIBLE
        } else {
            holder.btnDetails.setImageResource(R.drawable.ic_view_details_24)
            holder.expandable.visibility = View.GONE
        }

        holder.btnDetails.setOnClickListener {
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_product_name)
        val remaining: TextView = view.findViewById(R.id.tv_remaining)
        val btnDetails: ImageButton = view.findViewById(R.id.ib_details)
        val expandable: ConstraintLayout = view.findViewById(R.id.expandable)
        val quantityShop: TextView = view.findViewById(R.id.tv_quantity_shop)
        val date: TextView = view.findViewById(R.id.tv_date_shop)
        val btnRemove: MaterialButton = view.findViewById(R.id.btn_remove)
    }

    fun updateList(newList: MutableList<StorageModel>) {
        products = newList
        notifyDataSetChanged()
    }
}
