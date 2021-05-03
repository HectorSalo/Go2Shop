package com.skysam.hchirinos.go2shop.shopsModule.ui.viewShop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.databinding.DialogViewShopBinding
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 30/3/2021.
 */
class ViewShopDetailsDialog(private val shop: Shop): DialogFragment() {
    private var _binding: DialogViewShopBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ViewShopDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogViewShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        loadData()
    }

    private fun loadData() {
        binding.tvNameList.text = shop.name
        binding.tvDate.text = DateFormat.getDateInstance().format(Date(shop.dateCreated))
        binding.tvRate.text = getString(R.string.text_cotizacion_from_shop, NumberFormat.getInstance().format(shop.rateChange))
        binding.tvTotal.text = getString(R.string.text_total_list, NumberFormat.getInstance().format(shop.total))
        adapter = ViewShopDetailsAdapter(shop.listProducts.sortedWith(compareBy { it.name }).toMutableList())
        binding.rvList.adapter = adapter
        binding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}