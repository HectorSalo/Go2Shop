package com.skysam.hchirinos.go2shop.productsModule.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.FragmentProductsBinding
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductPresenter
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductPresenterClass

class ProductsFragment : Fragment(), ProductView, OnClickList {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var productPresenter: ProductPresenter
    private lateinit var adapterProduct: ProductAdapter
    private var productsList: MutableList<Product> = mutableListOf()
    private var productsToDelete: MutableList<Product> = mutableListOf()
    private lateinit var itemDelete: MenuItem
    private lateinit var itemSearch: MenuItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        productPresenter = ProductPresenterClass(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productPresenter.getProducts()
        binding.rvProducts.setHasFixedSize(true)
        adapterProduct = ProductAdapter(productsList, this)
        binding.rvProducts.adapter = adapterProduct
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.action_cerrar_sesion)
        itemDelete = menu.findItem(R.id.action_delete)
        itemSearch = menu.findItem(R.id.action_search)
        item.isVisible = false
        itemDelete.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun resultGetProducts(products: MutableList<Product>) {
        if (products.isNullOrEmpty()) {
            binding.tvListEmpty.visibility = View.VISIBLE
        } else {
            binding.tvListEmpty.visibility = View.GONE
            productsList = products
            adapterProduct.updateList(products)
        }
    }

    override fun onClickDelete(position: Int) {
        val product = productsList[position]
        if (productsToDelete.contains(product)) {
            productsToDelete.remove(product)
        } else {
            productsToDelete.add(product)
        }
        itemDelete.isVisible = productsToDelete.isNotEmpty()
        itemSearch.isVisible = productsToDelete.isEmpty()
    }

    override fun onClickEdit(position: Int) {

    }
}