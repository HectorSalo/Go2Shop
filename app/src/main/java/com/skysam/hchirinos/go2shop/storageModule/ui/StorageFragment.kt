package com.skysam.hchirinos.go2shop.storageModule.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

class StorageFragment : Fragment(), SearchView.OnQueryTextListener, OnClick {
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
        requireActivity().menuInflater.inflate(R.menu.storage, menu)
        val item = menu.findItem(R.id.action_new_storage)
        item.setOnMenuItemClickListener {
            val dialogAddProductStorage = AddProductStorageDialog()
            dialogAddProductStorage.show(requireActivity().supportFragmentManager, tag)
            true
        }
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

    override fun viewDetails(product: StorageModel) {
        val dialogViewDetails = ViewDetailsDialog(product)
        dialogViewDetails.show(requireActivity().supportFragmentManager, tag)
    }

    override fun remove(product: StorageModel) {
        val dialogUpdateQuantity = UpdateQuantityDialog(false, product)
        dialogUpdateQuantity.show(requireActivity().supportFragmentManager, tag)
    }

    override fun add(product: StorageModel) {
        val dialogUpdateQuantity = UpdateQuantityDialog(true, product)
        dialogUpdateQuantity.show(requireActivity().supportFragmentManager, tag)
    }

    override fun editProduct(product: StorageModel) {
        val editProductStorageDialog = EditProductStorageDialog(product, products)
        editProductStorageDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteProduct(product: StorageModel) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_delete_dialog))
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteProductToStorage(product)
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

}