package com.skysam.hchirinos.go2shop.shopsModule.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.database.room.entities.Shop
import com.skysam.hchirinos.go2shop.databinding.FragmentHistoryBinding
import com.skysam.hchirinos.go2shop.shopsModule.ui.viewShop.ViewShopItem
import com.skysam.hchirinos.go2shop.shopsModule.viewModel.ShopViewModel
import java.util.*

class ShopFragment : Fragment(), SearchView.OnQueryTextListener, OnClickList {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ShopAdapter
    private lateinit var search: SearchView
    private var listsShop: MutableList<Shop> = mutableListOf()
    private var listSearch: MutableList<Shop> = mutableListOf()
    private val shopViewModel: ShopViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ShopAdapter(listsShop, this)
        binding.rvList.setHasFixedSize(true)
        binding.rvList.adapter = adapter

        shopViewModel.listFlow.observe(viewLifecycleOwner, {shop->
            if (_binding != null) {
                listsShop.clear()
                listsShop.addAll(shop)
                adapter.updateList(listsShop)
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main, menu)
        val item2 = menu.findItem(R.id.action_sync)
        item2.isVisible = false
        val item = menu.findItem(R.id.action_cerrar_sesion)
        item.isVisible = false
        val itemSearch = menu.findItem(R.id.action_search)
        search = itemSearch.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (listsShop.isEmpty()) {
            Toast.makeText(context, getString(R.string.text_list_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.toLowerCase(Locale.ROOT)
            listSearch.clear()

            for (list in listsShop) {
                if (list.name.toLowerCase(Locale.ROOT).contains(userInput)) {
                    listSearch.add(list)
                }
            }
            adapter.updateList(listSearch)
        }
        return false
    }

    override fun onClickDelete(position: Int) {

    }

    override fun onClickEdit(position: Int) {
        val listSelected: Shop = if (listSearch.isEmpty() || (listSearch.size == listsShop.size)) {
            listsShop[position]
        } else {
            listSearch[position]
        }
        val action = ViewShopItem(listSelected)
        action.show(requireActivity().supportFragmentManager, tag)
    }
}