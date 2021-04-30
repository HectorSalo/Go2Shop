package com.skysam.hchirinos.go2shop.productsModule.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.common.classView.UpdatedProduct
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.FragmentProductsBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel
import java.util.*

class ProductsFragment : Fragment(), OnClickList, UpdatedProduct, SearchView.OnQueryTextListener {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapterProduct: ProductAdapter
    private lateinit var search: SearchView
    private var productsList: MutableList<Product> = mutableListOf()
    private var productsToDelete: MutableList<Product> = mutableListOf()
    private var productsToRestored: MutableList<Product> = mutableListOf()
    private var listSearch: MutableList<Product> = mutableListOf()
    private var listPositionsToDelete: MutableList<Int> = mutableListOf()
    private var actionModeActived = false
    private var deleting = false
    private var actionMode: androidx.appcompat.view.ActionMode? = null
    private var positionEdit = 0
    private var firstPositionToDelete = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvProducts.setHasFixedSize(true)
        adapterProduct = ProductAdapter(productsList, this)
        binding.rvProducts.adapter = adapterProduct
        loadViewModels()
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

    private fun loadViewModels() {
        viewModel.products.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    if (!deleting) {
                        productsList.clear()
                        productsList.addAll(it)
                        adapterProduct.updateList(productsList)
                        binding.rvProducts.visibility = View.VISIBLE
                        binding.tvListEmpty.visibility = View.GONE
                        if (listSearch.isNotEmpty()) {
                            binding.rvProducts.scrollToPosition(positionEdit)
                            listSearch.clear()
                        }
                    }
                    deleting = false
                } else {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onClickDelete(position: Int) {
        var positionOriginal = -1
        val product: Product
        if (listSearch.isEmpty() || (listSearch.size == productsList.size)) {
            product = productsList[position]
            positionOriginal = position
        } else {
            product = listSearch[position]
            for (i in productsList.indices) {
                if (productsList[i].id == product.id) {
                    positionOriginal = i
                }
            }
        }
        if (productsToDelete.contains(product)) {
            productsToDelete.remove(product)
            listPositionsToDelete.remove(positionOriginal)
        } else {
            productsToDelete.add(product)
            listPositionsToDelete.add(positionOriginal)
        }
        if (productsToDelete.size == 1 && !actionModeActived) {
            actionMode = (activity as AppCompatActivity).startSupportActionMode(callback)
            actionModeActived = true
            firstPositionToDelete = positionOriginal
        }
        if (productsToDelete.isEmpty()) {
            adapterProduct.clearListToDelete()
            actionModeActived = false
            (activity as AppCompatActivity).startSupportActionMode(callback)!!.finish()
        }
        actionMode?.title = "Seleccionado ${productsToDelete.size}"
    }

    override fun onClickEdit(position: Int) {
        val productSelected: Product = if (listSearch.isEmpty() || (listSearch.size == productsList.size)) {
            productsList[position]
        } else {
            listSearch[position]
        }
        positionEdit = productsList.indexOf(productSelected)
        val editProductDialog = EditProductDialog(productSelected, position, false, this, null)
        editProductDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun updatedProduct(position: Int, product: Product) {
        viewModel.editProduct(product)
        search.onActionViewCollapsed()
    }

    private val callback = object : androidx.appcompat.view.ActionMode.Callback {

        override fun onCreateActionMode(
            mode: androidx.appcompat.view.ActionMode?,
            menu: Menu?
        ): Boolean {
            requireActivity().menuInflater.inflate(R.menu.contextual_action_bar_products, menu)
            return true
        }

        override fun onPrepareActionMode(
            mode: androidx.appcompat.view.ActionMode?,
            menu: Menu?
        ): Boolean {
            return false
        }

        override fun onActionItemClicked(
            mode: androidx.appcompat.view.ActionMode?,
            item: MenuItem?
        ): Boolean {
            return when (item?.itemId) {
                R.id.action_delete -> {
                    productsToRestored.addAll(productsToDelete)
                    productsList.removeAll(productsToDelete)
                    Snackbar.make(
                        binding.rvProducts,
                        getString(R.string.text_deleting),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setDuration(3500)
                        .setAction(getString(R.string.btn_undo)) {
                            for (i in productsToRestored.indices) {
                                productsList.add(listPositionsToDelete[i], productsToRestored[i])
                            }
                            listSearch.clear()
                            productsToRestored.clear()
                            listPositionsToDelete.clear()
                            adapterProduct.updateList(productsList)
                            binding.rvProducts.scrollToPosition(firstPositionToDelete)
                        }
                        .show()
                    actionMode?.finish()

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (productsToRestored.isNotEmpty()) {
                            deleting = true
                            viewModel.deleteProducts(productsToRestored)
                            productsToRestored.clear()
                            listPositionsToDelete.clear()
                        }
                    }, 4500)
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
            actionModeActived = false
            listSearch.clear()
            search.onActionViewCollapsed()
            productsToDelete.clear()
            adapterProduct.clearListToDelete()
            adapterProduct.updateList(productsList)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (productsList.isEmpty()) {
            Toast.makeText(context, getString(R.string.text_list_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.toLowerCase(Locale.ROOT)
            listSearch.clear()

            for (product in productsList) {
                if (product.name.toLowerCase(Locale.ROOT).contains(userInput)) {
                    listSearch.add(product)
                }
            }
            adapterProduct.updateList(listSearch)
        }
        return false
    }
}