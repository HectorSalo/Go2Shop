package com.skysam.hchirinos.go2shop.storageModule.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.ClassesCommon
import com.skysam.hchirinos.go2shop.common.models.StorageModel

class StorageAdapter(private var products: MutableList<StorageModel>,
                     private val onClick: OnClick):
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
            ClassesCommon.convertDoubleToString(item.quantityRemaining), item.unit)

        holder.btnDetails.setOnClickListener { onClick.viewDetails(item) }
        holder.btnRemove.setOnClickListener { onClick.remove(item) }
        holder.btnAdd.setOnClickListener { onClick.add(item) }
        holder.card.setOnClickListener {
            val popMenu = PopupMenu(context, holder.card)
            popMenu.inflate(R.menu.item_storage)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_edit -> onClick.editProduct(item)
                    R.id.action_delete -> onClick.deleteProduct(item)
                }
                false
            }
            popMenu.show()
        }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_product_name)
        val remaining: TextView = view.findViewById(R.id.tv_remaining)
        val btnDetails: ImageButton = view.findViewById(R.id.ib_details)
        val btnRemove: ImageButton = view.findViewById(R.id.ib_rest_quantity)
        val btnAdd: ImageButton = view.findViewById(R.id.ib_add_quantity)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<StorageModel>) {
        products = newList
        notifyDataSetChanged()
    }
}
