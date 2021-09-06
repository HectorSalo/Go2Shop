package com.skysam.hchirinos.go2shop.storageModule.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.databinding.FragmentStorageBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel

class StorageFragment : Fragment(), SearchView.OnQueryTextListener, OnClickRemoveQuantity {
    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StorageAdapter
    private lateinit var search: SearchView
    private var products: MutableList<StorageModel> = mutableListOf()
    private var listSearch: MutableList<StorageModel> = mutableListOf()
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        setToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StorageAdapter(products, this)
        binding.rvProducts.setHasFixedSize(true)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        viewModel.productsFromStorage.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    products.clear()
                    products.addAll(it)
                    adapter.updateList(products)
                    binding.tvListEmpty.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                } else {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun setToolbar() {
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.btn_storage)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (products.isEmpty()) {
            Toast.makeText(context, getString(R.string.text_list_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.lowercase()
            listSearch.clear()

            for (product in products) {
                if (product.name.lowercase().contains(userInput)) {
                    listSearch.add(product)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adapter.updateList(listSearch)
        }
        return false
    }

    override fun remove(product: StorageModel) {
        if (product.quantityRemaining > 0) {
            viewModel.removeUnitFromProductToStorage(product)
        } else {
            viewModel.deleteProductToStorage(product)
        }
    }

}