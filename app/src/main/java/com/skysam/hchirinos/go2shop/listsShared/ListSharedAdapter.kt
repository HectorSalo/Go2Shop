package com.skysam.hchirinos.go2shop.listsShared

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.ListShared
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import java.text.NumberFormat

/**
 * Created by Hector Chirinos (Home) on 3/11/2021.
 */
class ListSharedAdapter(private var lists: MutableList<ListShared>, private val onClick: OnClick):
    RecyclerView.Adapter<ListSharedAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSharedAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListSharedAdapter.ViewHolder, position: Int) {
        val item = lists[position]
        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item, NumberFormat.getInstance().format(item.total))
        holder.listItems.text = context.getString(R.string.text_item_in_list, item.listProducts.size.toString())

        holder.shareFrom.visibility = View.GONE

        holder.card.setOnLongClickListener {
            if (item.userOwner == AuthAPI.getCurrenUser()?.uid) {
                val popMenu = PopupMenu(context, holder.card)
                popMenu.inflate(R.menu.item_list_shared)
                popMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.action_add_user -> onClick.addUsers(item)
                        R.id.action_delete-> onClick.deleteList(item)
                    }
                    false
                }
                popMenu.show()
            }
            true
        }
        holder.card.setOnClickListener {
            onClick.viewList(item)
        }
    }

    override fun getItemCount(): Int = lists.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val listItems: TextView = view.findViewById(R.id.tv_items)
        val shareFrom: TextView = view.findViewById(R.id.tv_share)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<ListShared>) {
        lists = newList
        notifyDataSetChanged()
    }
}