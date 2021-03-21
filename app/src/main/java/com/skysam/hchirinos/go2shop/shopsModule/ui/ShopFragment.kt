package com.skysam.hchirinos.go2shop.shopsModule.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.databinding.FragmentHistoryBinding
import com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop.AddListShopViewModel

class ShopFragment : Fragment() {

    private lateinit var addListShopViewModel: AddListShopViewModel
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        addListShopViewModel =
                ViewModelProvider(this).get(AddListShopViewModel::class.java)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        addListShopViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textGallery.text = it
        })
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.action_cerrar_sesion)
        item.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}