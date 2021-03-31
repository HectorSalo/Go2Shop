package com.skysam.hchirinos.go2shop.shopsModule.ui.viewShop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import java.text.NumberFormat

/**
 * Created by Hector Chirinos (Home) on 30/3/2021.
 */
class ViewShopItemAdapter(private val products: MutableList<ProductsToListModel>): RecyclerView.Adapter<ViewShopItemAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewShopItemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_list, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewShopItemAdapter.ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item, NumberFormat.getInstance().format(item.price))
        holder.unit.text = context.getString(R.string.text_quantity_total, item.quantity, item.unit)
        holder.buttonDelete.visibility = View.GONE
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_item)
        val unit: TextView = view.findViewById(R.id.tv_unit)
        val price: TextView = view.findViewById(R.id.tv_price)
        val buttonDelete: ImageButton = view.findViewById(R.id.ib_delete_item)
    }
}