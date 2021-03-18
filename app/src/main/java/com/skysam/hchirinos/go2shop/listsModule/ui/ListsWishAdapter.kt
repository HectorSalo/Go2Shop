package com.skysam.hchirinos.go2shop.listsModule.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import java.text.NumberFormat

/**
 * Created by Hector Chirinos on 16/03/2021.
 */
class ListsWishAdapter(private var lists: MutableList<ListWish>, private val onClickList: OnClickList): RecyclerView.Adapter<ListsWishAdapter.ViewHolder>() {
    private lateinit var context: Context
    private var listToDeleted: MutableList<Int> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListsWishAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListsWishAdapter.ViewHolder, position: Int) {
        val item = lists[position]
        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_total_price_item, NumberFormat.getInstance().format(item.total))
        holder.listItems.text = context.getString(R.string.text_item_in_list, item.listProducts.size.toString())

        holder.card.isChecked = listToDeleted.contains(position)

        holder.card.setOnLongClickListener {
            holder.card.isChecked = !holder.card.isChecked
            onClickList.onClickDelete(position)
            fillListToDelete(position)
            true
        }
        holder.card.setOnClickListener {
            if (listToDeleted.isNotEmpty()) {
                fillListToDelete(position)
                holder.card.isChecked = !holder.card.isChecked
                onClickList.onClickDelete(position)
            } else {
                onClickList.onClickEdit(position)
            }
        }
    }

    override fun getItemCount(): Int = lists.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val listItems: TextView = view.findViewById(R.id.tv_items)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    private fun fillListToDelete(position: Int) {
        if (listToDeleted.contains(position)) {
            listToDeleted.remove(position)
        } else {
            listToDeleted.add(position)
        }
    }

    fun updateList(newList: MutableList<ListWish>) {
        lists = newList
        notifyDataSetChanged()
    }

    fun clearListToDelete() {
        listToDeleted.clear()
    }
}