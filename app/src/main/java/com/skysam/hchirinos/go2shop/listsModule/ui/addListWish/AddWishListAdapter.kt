package com.skysam.hchirinos.go2shop.listsModule.ui.addListWish

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import java.text.NumberFormat


class AddWishListAdapter(
    private var products: MutableList<Product>, private val listener: OnClickList
) : RecyclerView.Adapter<AddWishListAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_list, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item, NumberFormat.getInstance().format(item.price))
        holder.unit.text = context.getString(R.string.text_quantity_total, item.quantity, item.unit)
        holder.buttonDelete.setOnClickListener { listener.onClickDelete(position) }
        holder.buttonEdit.setOnClickListener { listener.onClickEdit(position) }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_item)
        val unit: TextView = view.findViewById(R.id.tv_unit)
        val price: TextView = view.findViewById(R.id.tv_price)
        val buttonDelete: ImageButton = view.findViewById(R.id.ib_delete_item)
        val buttonEdit: ConstraintLayout = view.findViewById(R.id.constraint)
    }

    fun updateList(newList: MutableList<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}