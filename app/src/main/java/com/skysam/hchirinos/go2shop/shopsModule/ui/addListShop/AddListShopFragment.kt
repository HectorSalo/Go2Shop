package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.*
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.FragmentAddListShopBinding
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.productsModule.ui.EditProductDialog
import com.skysam.hchirinos.go2shop.shopsModule.viewModel.AddListShopViewModel
import com.skysam.hchirinos.go2shop.shopsModule.viewModel.SharedViewModel
import java.text.NumberFormat

class AddListShopFragment : Fragment(), OnClickList, ProductSaveFromList,
        OnClickExit, OnSwitchChange, UpdatedProduct {
    private val addListShopViewModel: AddListShopViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentAddListShopBinding? = null
    private val binding get() = _binding!!
    private var productsFromDB: MutableList<Product> = mutableListOf()
    private var productsToAdd: MutableList<ProductsToShopModel> = mutableListOf()
    private var productsToShop: MutableList<ProductsToListModel> = mutableListOf()
    private var productsName = mutableListOf<String>()
    private lateinit var addListShopAdapter: AddListShopAdapter
    private var rateChange: Double = 0.0
    private var total: Double = 0.0
    private var priceBeforeUpdated: Double = 0.0
    private var actived = true
    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentAddListShopBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getOut()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        loadViewModels()
        binding.rvList.setHasFixedSize(true)
        addListShopAdapter = AddListShopAdapter(productsToAdd, this, this)
        binding.rvList.adapter = addListShopAdapter
        binding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.tvTotal.text = getString(R.string.text_total_list, total.toString())
        binding.etSarchProduct.addTextChangedListener {
            if (binding.etSarchProduct.text.toString().isEmpty()) {
                binding.tfSearchProducts.startIconDrawable = null
                binding.tfSearchProducts.helperText = null
            } else {
                if (binding.etSarchProduct.adapter.isEmpty) {
                    binding.tfSearchProducts.startIconDrawable = ContextCompat
                        .getDrawable(requireContext(), R.drawable.ic_add_product_24)
                    binding.tfSearchProducts.helperText = getString(R.string.title_add_producto_dialog)
                    binding.tfSearchProducts.setStartIconOnClickListener {
                        val addProduct = AddProductDialog(binding.etSarchProduct.text.toString(), this)
                        addProduct.show(requireActivity().supportFragmentManager, tag)
                    }
                } else {
                    binding.tfSearchProducts.startIconDrawable = null
                    binding.tfSearchProducts.helperText = null
                }
            }
        }
        binding.etSarchProduct.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Keyboard.close(binding.root)
            var positionSelected = -1
            val nameSelected = parent.getItemAtPosition(position)
            for (i in productsName.indices) {
                positionSelected = productsName.indexOf(nameSelected)
            }
            addProductToList(positionSelected)
        }
    }

    private fun loadViewModels() {
        sharedViewModel.nameShop.observe(viewLifecycleOwner, {
            toolbar.title = it
            toolbar.setNavigationIcon(R.drawable.ic_close_24)
        })
        sharedViewModel.productsShared.observe(viewLifecycleOwner, { list ->
            addListShopViewModel.fillListFirst(list)
        })
        sharedViewModel.rateChange.observe(viewLifecycleOwner, {
            rateChange = it
        })
        addListShopViewModel.getProducts().observe(viewLifecycleOwner, {
            productsFromDB.addAll(it)
            fillListProductsDB(it)
        })
        addListShopViewModel.allProducts.observe(viewLifecycleOwner, {
            productsToAdd.clear()
            productsToAdd.addAll(it)
            addListShopAdapter.updateList(productsToAdd)
            addListShopViewModel.scrollToPosition()
        })
        addListShopViewModel.isProductInList.observe(viewLifecycleOwner, {
            if (it) {
                Snackbar.make(binding.root, getString(R.string.product_added), Snackbar.LENGTH_LONG).show()
            }
        })
        addListShopViewModel.positionProductInList.observe(viewLifecycleOwner, {
            binding.rvList.scrollToPosition(it)
        })
        addListShopViewModel.totalPrice.observe(viewLifecycleOwner, {
            total = it
            binding.tvTotal.text = getString(R.string.text_total_list, NumberFormat.getInstance().format(total))
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.new_shop, menu)
        val itemSave = menu.findItem(R.id.action_save)
        itemSave.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                getOut()
                true
            }
            R.id.action_save -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    private fun addProductToList(position: Int) {
        val productSelected = productsFromDB[position]
        val productModelSelected = ProductsToShopModel(
            productSelected.id,
            productSelected.name,
            productSelected.unit,
            productSelected.userId,
            "",
            productSelected.price,
            productSelected.quantity
        )
        addListShopViewModel.addProductToList(productModelSelected)
        binding.etSarchProduct.setText("")
    }

    private fun fillListProductsDB(list: MutableList<Product>){
        for (i in list.indices) {
            productsName.add(i, list[i].name)
        }
        val adapterSearchProduct = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, productsName)
        binding.etSarchProduct.setAdapter(adapterSearchProduct)
    }

    override fun onClickDelete(position: Int) {

    }

    override fun onClickEdit(position: Int) {
        if (actived) {
            val productSelected = productsToAdd[position]
            priceBeforeUpdated = productSelected.price
            val productToUpdated = Product(
                productSelected.id,
                productSelected.name,
                productSelected.unit,
                productSelected.userId,
                productSelected.price,
                productSelected.quantity
            )
            val editProductDialog = EditProductDialog(productToUpdated, position, true, this, rateChange)
            editProductDialog.show(requireActivity().supportFragmentManager, tag)
        }
    }

    override fun productSave(product: Product) {
        val productModelSelected = ProductsToShopModel(
            product.id,
            product.name,
            product.unit,
            product.userId,
            "",
            product.price,
            product.quantity
        )
        addListShopViewModel.addProductToList(productModelSelected)
        binding.etSarchProduct.setText("")
        productsFromDB.add(product)
        fillListProductsDB(productsFromDB)
    }

    override fun onClickExit() {
        requireActivity().finish()
    }

    override fun switchChange(
        isChecked: Boolean,
        product: ProductsToShopModel?,
        list: MutableList<ProductsToListModel>?,
        nameList: String?
    ) {
        if (actived) {
            if (isChecked) {
                if (product!!.price == 0.0) {
                    Toast.makeText(requireContext(), getString(R.string.error_field_zero), Toast.LENGTH_SHORT).show()
                    return
                }
                addListShopViewModel.checkedProduct(product)
            } else {
                addListShopViewModel.uncheckedProduct(product!!)
            }
        }
    }

    override fun updatedProduct(position: Int, product: Product) {
        val productSelected = productsToAdd[position]
        val productModel = ProductsToShopModel(
            product.id,
            product.name,
            product.unit,
            product.userId,
            "",
            product.price,
            product.quantity,
            productSelected.isChecked
        )
        addListShopViewModel.updateProductToList(productModel, position)
        if (productSelected.isChecked) {
            addListShopViewModel.updatedPrice(priceBeforeUpdated, product.price)
        }
    }
}