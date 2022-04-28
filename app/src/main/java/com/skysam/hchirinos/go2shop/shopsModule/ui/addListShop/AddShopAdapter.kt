package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.ClassesCommon
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.common.classView.OnSwitchChange
import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.shopsModule.ui.OnClickToStorage
import java.text.NumberFormat

/**
 * Created by Hector Chirinos (Home) on 23/3/2021.
 */
class AddShopAdapter(private var products: MutableList<ProductsToShopModel>,
                     private val storaged: MutableList<StorageModel>,
                     private val listener: OnSwitchChange, private val listenerClickList: OnClickList,
                     private val onClickToStorage: OnClickToStorage):
    RecyclerView.Adapter<AddShopAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val listToStorage: MutableList<String> = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddShopAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_new_shop_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddShopAdapter.ViewHolder, position: Int) {
        val item = products[position]

        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item, NumberFormat.getInstance().format(item.price))
        holder.unit.text = context.getString(R.string.text_quantity_total, item.quantity, item.unit)

        holder.switchStorage.isChecked = item.isCheckedToStorage
        if (item.isCheckedToStorage) {
            holder.switchStorage.text = context.getString(R.string.text_switch_on_to_storage_product)
        } else {
            holder.switchStorage.text = context.getString(R.string.text_switch_off_to_storage_product)
        }

        holder.switchShop.isChecked = item.isCheckedToShop
        if (item.isCheckedToShop) {
            holder.switchShop.text = context.getString(R.string.text_switch_on_shop)
            holder.switchStorage.isEnabled = true
            holder.lottie.progress = 0.5f
            holder.subtotal.text = context.getString(R.string.text_total_price_item,
                NumberFormat.getInstance().format(item.price * item.quantity))
        } else {
            holder.switchShop.text = context.getString(R.string.text_switch_off_shop_product)
            holder.switchStorage.isEnabled = false
            holder.lottie.progress = 0.0f
            holder.lottie.cancelAnimation()
            holder.lottie.removeAllUpdateListeners()
            holder.subtotal.text = context.getString(R.string.text_empty)
        }

        holder.switchShop.setOnClickListener {
            val isChecked: Boolean = !item.isCheckedToShop
            if (isChecked) {
                if (item.price == 0.0) {
                    holder.switchShop.isChecked = false
                    item.isCheckedToShop = false
                } else {
                    holder.switchShop.text = context.getString(R.string.text_switch_on_shop)
                    item.isCheckedToShop = true
                    holder.switchStorage.isEnabled = true
                    holder.subtotal.text = context.getString(R.string.text_total_price_item,
                        NumberFormat.getInstance().format(item.price * item.quantity))
                    holder.lottie.addAnimatorUpdateListener { value->
                        if ((value.animatedValue as Float * 100).toInt() == 50) {
                            holder.lottie.cancelAnimation()
                        }
                    }
                    holder.lottie.playAnimation()
                }
            } else {
                holder.switchShop.text = context.getString(R.string.text_switch_off_shop_product)
                item.isCheckedToShop = false
                holder.switchStorage.isEnabled = false
                holder.switchStorage.isChecked = false
                holder.switchStorage.text = context.getString(R.string.text_switch_off_to_storage_product)
                item.isCheckedToStorage = false
                holder.subtotal.text = context.getString(R.string.text_empty)
                holder.lottie.resumeAnimation()
                holder.lottie.removeAllUpdateListeners()
                if (listToStorage.contains(item.name)){
                    listToStorage.remove(item.name)
                }
            }
            listener.switchChange(isChecked, item, null, null)
        }

        holder.switchStorage.setOnClickListener {
            val isChecked: Boolean = !item.isCheckedToStorage
            if (isChecked) {
                if (listToStorage.contains(item.name)) {
                    holder.switchStorage.isChecked = false
                    holder.switchStorage.text = context.getString(R.string.text_switch_off_to_storage_product)
                    Toast.makeText(context, context.getString(R.string.error_name_repeated_storage), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                listToStorage.add(item.name)
                item.isCheckedToStorage = true
                holder.switchStorage.text = context.getString(R.string.text_switch_on_to_storage_product)
                onClickToStorage.onClickAddToStorage(item)
            } else {
                listToStorage.remove(item.name)
                item.isCheckedToStorage = false
                holder.switchStorage.text = context.getString(R.string.text_switch_off_to_storage_product)
                onClickToStorage.onClickRemoveToStorage(item)
            }
        }

        holder.card.setOnClickListener { listenerClickList.onClickEdit(position) }

        var exists = false
        var quantitySto = 0.0
        var unitSto = ""
        for (sto in storaged) {
            if (sto.name == item.name && sto.unit == item.unit) {
                exists = true
                quantitySto = sto.quantityRemaining
                unitSto = sto.unit
            }
        }
        if (exists) {
            holder.remaining.text = context.getString(R.string.text_products_remaining,
                ClassesCommon.convertDoubleToString(quantitySto), unitSto)
            holder.remaining.visibility = View.VISIBLE
        } else {
            holder.remaining.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val unit: TextView = view.findViewById(R.id.tv_unit_items)
        val price: TextView = view.findViewById(R.id.tv_price)
        val subtotal: TextView = view.findViewById(R.id.tv_subtotal)
        val remaining: TextView = view.findViewById(R.id.tv_remaining)
        val switchShop: SwitchMaterial = view.findViewById(R.id.switch_shop)
        val switchStorage: SwitchMaterial = view.findViewById(R.id.switch_storage)
        val card: MaterialCardView = view.findViewById(R.id.card)
        val lottie: LottieAnimationView = view.findViewById(R.id.lottieAnimationView)
    }

    fun updateList(newList: MutableList<ProductsToShopModel>) {
        products = newList
        notifyDataSetChanged()
    }
}