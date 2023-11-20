package com.skysam.hchirinos.go2shop.listsModule.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.AvailabilityDetails

class AvailabilityDetailsAdapter: RecyclerView.Adapter<AvailabilityDetailsAdapter.ViewHolder> () {
    private lateinit var context: Context
    private var availabilityProduct = listOf<AvailabilityDetails>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvailabilityDetailsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_availability_details, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailabilityDetailsAdapter.ViewHolder, position: Int) {
        val item = availabilityProduct[position]

        holder.quantity.text = item.quantity.toString()
        holder.unit.text = item.unit

    }

    override fun getItemCount(): Int = availabilityProduct.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quantity: TextView = view.findViewById(R.id.tv_quantity)
        val unit: TextView = view.findViewById(R.id.tv_unit)

    }

    fun updateList(newList: List<AvailabilityDetails>) {
        availabilityProduct = newList
        notifyDataSetChanged()
    }
}