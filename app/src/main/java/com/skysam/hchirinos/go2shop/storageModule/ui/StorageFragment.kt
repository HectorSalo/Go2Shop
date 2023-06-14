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
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.WrapContentLinearLayoutManager
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.databinding.FragmentStorageBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel

class StorageFragment : Fragment(), SearchView.OnQueryTextListener, OnClick, OnClickResult {
    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StorageAdapter
    private lateinit var search: SearchView
    private var allProducts: MutableList<StorageModel> = mutableListOf()
    private var productsFiltered: MutableList<StorageModel> = mutableListOf()
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var wrapContentLinearLayoutManager: WrapContentLinearLayoutManager
    private var editing = false
    private var deleting = false

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
        adapter = StorageAdapter(allProducts, this)
        wrapContentLinearLayoutManager = WrapContentLinearLayoutManager(requireContext(),
            RecyclerView.VERTICAL, false)
        binding.rvProducts.setHasFixedSize(true)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.layoutManager  = wrapContentLinearLayoutManager
        binding.rvProducts.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        viewModel.productsFromStorage.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    allProducts.clear()
                    allProducts.addAll(it)
                    if (!editing && !deleting) {
                        adapter.notifyItemRangeInserted(0, allProducts.size)
                        binding.tvListEmpty.visibility = View.GONE
                        binding.rvProducts.visibility = View.VISIBLE
                    } else {
                        editing = false
                        deleting = false
                    }
                } else {
                    productsFiltered.clear()
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
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
        if (allProducts.isEmpty()) {
            Toast.makeText(context, getString(R.string.text_list_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.lowercase()
            productsFiltered.clear()

            for (product in allProducts) {
                if (product.name.lowercase().contains(userInput)) {
                    productsFiltered.add(product)
                }
            }
            if (productsFiltered.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }

            adapter.updateList(productsFiltered)
        }
        return false
    }

    override fun viewDetails(product: StorageModel) {
        val dialogViewDetails = ViewDetailsDialog(product)
        dialogViewDetails.show(requireActivity().supportFragmentManager, tag)
    }

    override fun remove(product: StorageModel) {
        val dialogUpdateQuantity = UpdateQuantityDialog(false, product, this)
        dialogUpdateQuantity.show(requireActivity().supportFragmentManager, tag)
    }

    override fun add(product: StorageModel) {
        val dialogUpdateQuantity = UpdateQuantityDialog(true, product, this)
        dialogUpdateQuantity.show(requireActivity().supportFragmentManager, tag)
    }

    override fun editProduct(product: StorageModel) {
        val editProductStorageDialog = EditProductStorageDialog(product, allProducts, this)
        editProductStorageDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteProduct(product: StorageModel) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_delete_dialog))
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                deleting = true
                if (productsFiltered.isNotEmpty()) {
                    adapter.notifyItemRemoved(productsFiltered.indexOf(product))
                    productsFiltered.remove(product)
                } else {
                    adapter.notifyItemRemoved(allProducts.indexOf(product))
                    allProducts.remove(product)
                }
                viewModel.deleteProductToStorage(product)
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun resultEdit(product: StorageModel) {
        editing = true
        if (productsFiltered.isNotEmpty()) {
            for (pro in productsFiltered) {
                if (pro.id == product.id) {
                    productsFiltered[productsFiltered.indexOf(pro)] = product
                    adapter.notifyItemChanged(productsFiltered.indexOf(product))
                    break
                }
            }
        } else {
            for (pro in allProducts) {
                if (pro.id == product.id) {
                    allProducts[allProducts.indexOf(pro)] = product
                    adapter.notifyItemChanged(allProducts.indexOf(product))
                    break
                }
            }
        }
        viewModel.editProductToStorage(product)
    }

}