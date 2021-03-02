package com.skysam.hchirinos.go2shop.homeModule.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.AddProductDialog
import com.skysam.hchirinos.go2shop.databinding.FragmentInicioBinding

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNewProduct.setOnClickListener {
            val addProductDialog = AddProductDialog()
            addProductDialog.show(requireActivity().supportFragmentManager, tag)
        }
        binding.btnInventory.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_home_to_storageFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.action_search)
        item.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}