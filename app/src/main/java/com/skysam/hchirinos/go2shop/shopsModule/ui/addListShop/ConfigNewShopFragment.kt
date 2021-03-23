package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.databinding.FragmentConfigNewShopBinding


class ConfigNewShopFragment : Fragment() {

    private var _binding: FragmentConfigNewShopBinding? = null
    private val binding get() = _binding!!
    private lateinit var configNewShopViewModel: ConfigNewShopViewModel
    private lateinit var adapter: ConfigNewShopAdapter
    private val lists: MutableList<ListWish> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigNewShopBinding.inflate(inflater, container, false)
        configNewShopViewModel = ViewModelProvider(this).get(ConfigNewShopViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViewModels()
        adapter = ConfigNewShopAdapter(lists)
        binding.rvLists.setHasFixedSize(true)
        binding.rvLists.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModels() {
        configNewShopViewModel.lists.observe(viewLifecycleOwner, {
            adapter.updateList(it)
        })
    }
}