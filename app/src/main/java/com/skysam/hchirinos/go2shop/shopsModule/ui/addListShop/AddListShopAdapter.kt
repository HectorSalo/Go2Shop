package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnSwitchChange
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import java.text.NumberFormat

/**
 * Created by Hector Chirinos (Home) on 23/3/2021.
 */
class AddListShopAdapter(private var products: MutableList<ProductsToListModel>, private val listener: OnSwitchChange):
    RecyclerView.Adapter<AddListShopAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddListShopAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_new_shop_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddListShopAdapter.ViewHolder, position: Int) {
        val item = products[position]

        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item, NumberFormat.getInstance().format(item.price))
        holder.unit.text = context.getString(R.string.text_quantity_total, item.quantity, item.unit)

        if (holder.switch.isChecked) {
            holder.switch.text = context.getString(R.string.text_switch_on_shop)
        } else {
            holder.switch.text = context.getString(R.string.text_switch_off_shop_product)
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.switch.text = context.getString(R.string.text_switch_on_shop)
            } else {
                holder.switch.text = context.getString(R.string.text_switch_off_shop_product)
            }
            listener.switchChange(isChecked, item, null)
        }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val unit: TextView = view.findViewById(R.id.tv_unit_items)
        val price: TextView = view.findViewById(R.id.tv_price)
        val switch: SwitchMaterial = view.findViewById(R.id.switch_shop)
    }

    fun updateList(newList: MutableList<ProductsToListModel>) {
        products = newList
        notifyDataSetChanged()
    }
}