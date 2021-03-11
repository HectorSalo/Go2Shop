package com.skysam.hchirinos.go2shop.productsModule.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.database.room.entities.Product

/**
 * Created by Hector Chirinos (Home) on 10/3/2021.
 */
class ProductAdapter(private var products: MutableList<Product>, private val onClickList: OnClickList): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var listToDeleted: MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item,item.price)
        holder.unit.text = item.unit

        holder.card.setOnLongClickListener {
            holder.card.isChecked = !holder.card.isChecked
            onClickList.onClickDelete(position)
            fillListToDelete(position)
            true
        }
        holder.card.setOnClickListener {
            if (listToDeleted.isNotEmpty()) {
                holder.card.isChecked = !holder.card.isChecked
                onClickList.onClickDelete(position)
                fillListToDelete(position)
            } else {
                onClickList.onClickEdit(position)
            }
        }
    }

    private fun fillListToDelete(position: Int) {
        if (listToDeleted.contains(position)) {
            listToDeleted.remove(position)
        } else {
            listToDeleted.add(position)
        }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val unit: TextView = view.findViewById(R.id.tv_unit)
        val price: TextView = view.findViewById(R.id.tv_price)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}