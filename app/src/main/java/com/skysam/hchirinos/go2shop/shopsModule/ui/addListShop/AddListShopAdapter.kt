package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.common.classView.OnSwitchChange
import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel
import java.text.NumberFormat

/**
 * Created by Hector Chirinos (Home) on 23/3/2021.
 */
class AddListShopAdapter(private var products: MutableList<ProductsToShopModel>,
                         private val listener: OnSwitchChange, private val listenerClickList: OnClickList):
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

        if (item.listId.isEmpty()) {
            holder.nameList.text = ""
        } else {
            holder.nameList.text = context.getString(R.string.text_list_belong, item.listId)
        }

        holder.switch.isChecked = item.isChecked

        if (holder.switch.isChecked) {
            holder.switch.text = context.getString(R.string.text_switch_on_shop)
        } else {
            holder.switch.text = context.getString(R.string.text_switch_off_shop_product)
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (item.price == 0.0) {
                    holder.switch.isChecked = false
                } else {
                    holder.switch.text = context.getString(R.string.text_switch_on_shop)
                }
            } else {
                holder.switch.text = context.getString(R.string.text_switch_off_shop_product)
            }
            listener.switchChange(isChecked, item, null, null)
        }

        holder.card.setOnClickListener { listenerClickList.onClickEdit(position) }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val unit: TextView = view.findViewById(R.id.tv_unit_items)
        val price: TextView = view.findViewById(R.id.tv_price)
        val nameList: TextView = view.findViewById(R.id.tv_name_list)
        val switch: SwitchMaterial = view.findViewById(R.id.switch_shop)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<ProductsToShopModel>) {
        products = newList
        notifyDataSetChanged()
    }
}