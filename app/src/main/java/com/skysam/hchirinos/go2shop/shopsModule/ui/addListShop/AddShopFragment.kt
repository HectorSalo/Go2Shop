package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.*
import com.skysam.hchirinos.go2shop.common.models.Deparment
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel
import com.skysam.hchirinos.go2shop.common.models.StorageModel
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.common.models.Product
import com.skysam.hchirinos.go2shop.common.models.Shop
import com.skysam.hchirinos.go2shop.databinding.FragmentAddListShopBinding
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.productsModule.ui.EditProductDialog
import com.skysam.hchirinos.go2shop.shopsModule.ui.OnClickToStorage
import com.skysam.hchirinos.go2shop.viewmodels.AddShopViewModel
import java.text.NumberFormat
import java.util.*

class AddShopFragment : Fragment(),
    OnClickList, ProductSaveFromList, OnClickExit, OnSwitchChange, UpdatedProduct, OnClickShop, OnClickToStorage {
    private val viewModel: AddShopViewModel by activityViewModels()
    private var _binding: FragmentAddListShopBinding? = null
    private val binding get() = _binding!!
    private var productsFromDB: MutableList<Product> = mutableListOf()
    private var productsToAdd: MutableList<ProductsToShopModel> = mutableListOf()
    private val productsFiltered = mutableListOf<ProductsToShopModel>()
    private var productsToShop: MutableList<ProductsToListModel> = mutableListOf()
    private var productsShared: MutableList<ProductsToListModel> = mutableListOf()
    private var productsToStorage: MutableList<StorageModel> = mutableListOf()
    private var productsStoraged: MutableList<StorageModel> = mutableListOf()
    private var productsName = mutableListOf<String>()
    private var deparments = mutableListOf<Deparment>()
    private var deparmentsToShow = mutableListOf<Deparment>()
    private var deparmentsFilter = mutableListOf<Deparment>()
    private lateinit var addShopAdapter: AddShopAdapter
    private lateinit var nameList: String
    private lateinit var wrapContentLinearLayoutManager: WrapContentLinearLayoutManager
    private var rateChange: Double = 0.0
    private var total: Double = 0.0
    private var priceBeforeUpdated: Double = 0.0
    private var productAdded: ProductsToShopModel? = null
    private var actived = true
    private var editing = false
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
        binding.rvList.setHasFixedSize(true)
        wrapContentLinearLayoutManager = WrapContentLinearLayoutManager(requireContext(),
            RecyclerView.VERTICAL, false)
        addShopAdapter = AddShopAdapter(productsToAdd, productsStoraged,this, this, this)
        binding.rvList.adapter = addShopAdapter
        binding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.tvTotal.text = getString(R.string.text_total_list, total.toString())
        binding.tfSearchProducts.setStartIconOnClickListener {
            val addProduct = AddProductDialog(binding.etSarchProduct.text.toString().trim(), this, productsFromDB)
            addProduct.show(requireActivity().supportFragmentManager, tag)
        }
        binding.etSarchProduct.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Keyboard.close(binding.root)
            var product: Product? = null
            val nameSelected = parent.getItemAtPosition(position)
            for (i in productsName.indices) {
                val positionSelected = productsName.indexOf(nameSelected)
                product = productsFromDB[positionSelected]
            }
            addProductToList(product!!)
        }
        loadViewModels()
    }

    private fun loadViewModels() {
        viewModel.deparments.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    deparments.clear()
                    deparments.addAll(it)
                    loadDeparmentsToShow()
                }
            }
        }
        viewModel.nameShop.observe(viewLifecycleOwner) {
            nameList = it
            toolbar.title = it
            toolbar.setNavigationIcon(R.drawable.ic_close_24)
        }
        viewModel.productsShared.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                productsShared.clear()
                productsShared.addAll(list)
                loadDeparmentsToShow()
                viewModel.fillListFirst(list)
            }
        }
        viewModel.rateChange.observe(viewLifecycleOwner) {
            rateChange = it
        }
        viewModel.allProductsCreated.observe(viewLifecycleOwner) {
            if (_binding != null) {
                productsFromDB.clear()
                productsFromDB.addAll(it)
                fillListProductsDB(productsFromDB)
            }
        }
        viewModel.productsStoraged.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                productsStoraged.clear()
                productsStoraged.addAll(it)
                addShopAdapter.updateList(productsToAdd)
            }
        }
        viewModel.allProducts.observe(viewLifecycleOwner) {
            if (!editing) {
                productsFiltered.clear()
                deparmentsFilter.clear()
                binding.chipGroup.clearCheck()
                productsToAdd.clear()
                productsToAdd.addAll(it)
                addShopAdapter.updateList(productsToAdd)
                if (productAdded != null) {
                    binding.rvList.scrollToPosition(productsToAdd.indexOf(productAdded))
                    productAdded = null
                }
            } else {
                editing = false
                productsToAdd.clear()
                productsToAdd.addAll(it)
            }
            viewModel.updateCurrentShop(productsToAdd, total)
        }
        viewModel.totalPrice.observe(viewLifecycleOwner) {
            total = it
            binding.tvTotal.text =
                getString(R.string.text_total_list, NumberFormat.getInstance().format(total))
            viewModel.updateCurrentShop(productsToAdd, total)
        }
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
                val confirmationDialog = ConfirmationDialog(this)
                confirmationDialog.show(requireActivity().supportFragmentManager, tag)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateCurrentShop(productsToAdd, total)
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    private fun addProductToList(product: Product) {
        var exist = false
        var position = 0
        for (i in productsToAdd.indices) {
            if (productsToAdd[i].name == product.name) {
                exist = true
                position = i
            }
        }
        if (exist) {
            Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
            if(productsFiltered.isEmpty()) binding.rvList.scrollToPosition(position)
            return
        }
        val productModelSelected = ProductsToShopModel(
            product.id,
            product.name,
            product.unit,
            product.userId,
            "",
            product.price,
            product.quantity
        )
        productAdded = productModelSelected
        viewModel.addProductToList(productModelSelected)
        binding.etSarchProduct.setText("")
    }

    private fun fillListProductsDB(list: MutableList<Product>){
        productsName.clear()
        for (i in list.indices) {
            productsName.add(i, list[i].name)
        }
        val adapterSearchProduct = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, productsName.sorted())
        binding.etSarchProduct.setAdapter(adapterSearchProduct)
    }

    override fun onClickDelete(position: Int) {

    }

    override fun onClickEdit(position: Int) {
        if (actived) {
            val productSelected = if (productsFiltered.isEmpty()) {
                productsToAdd[position]
            } else {
                productsFiltered[position]
            }
            priceBeforeUpdated = productSelected.price * productSelected.quantity
            val productToUpdated = Product(
                productSelected.id,
                productSelected.name,
                productSelected.unit,
                productSelected.userId,
                productSelected.price,
                productSelected.quantity
            )
            val editProductDialog = EditProductDialog(productToUpdated, position, true, this, rateChange, true)
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
        productAdded = productModelSelected
        viewModel.addProduct(product)
        viewModel.addProductToList(productModelSelected)
        binding.etSarchProduct.setText("")
    }

    override fun onClickExit() {
        viewModel.deleteCurrentShop()
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
                val productModelSelected = ProductsToListModel(
                        product.id,
                        product.name,
                        product.unit,
                        product.userId,
                        Constants.LIST_ID,
                        product.price,
                        product.quantity
                )
                productsToShop.add(productModelSelected)
                viewModel.checkedProduct(product)
            } else {
                var productModelSelected: ProductsToListModel? = null
                for (i in productsToShop.indices) {
                    if (productsToShop[i].id == product!!.id) {
                        productModelSelected = ProductsToListModel(
                                product.id,
                                product.name,
                                product.unit,
                                product.userId,
                                Constants.LIST_ID,
                                product.price,
                                product.quantity
                        )
                    }
                }
                if (productModelSelected != null) {
                    productsToShop.remove(productModelSelected)
                    viewModel.uncheckedProduct(product!!)
                }
            }
        }
    }

    override fun updatedProduct(position: Int, product: Product) {
        val productSelected = if (productsFiltered.isEmpty()) {
            productsToAdd[position]
        } else {
            productsFiltered[position]
        }
        val productModel = ProductsToShopModel(
            product.id,
            product.name,
            product.unit,
            product.userId,
            productSelected.listId,
            product.price,
            product.quantity,
            productSelected.isCheckedToShop,
            productSelected.isCheckedToStorage,
            productSelected.deparment
        )
        if (productsFiltered.isEmpty()) {
            viewModel.updateProductToList(productModel, position)
        } else {
            editing = true
            productsFiltered[position] = productModel
            addShopAdapter.notifyItemChanged(position)
            var positionEdit = -1
            for (pro in productsToAdd) {
                if (pro.id == productModel.id) positionEdit = productsToAdd.indexOf(pro)
            }
            if (positionEdit > 0) viewModel.updateProductToList(productModel, positionEdit)
        }
        if (productSelected.isCheckedToShop) {
            val priceNew = product.price * product.quantity
            viewModel.updatedPrice(priceBeforeUpdated, priceNew)
        }
        for (productSto in productsToStorage) {
            if (productSto.id == productModel.id) {
                productSto.unit = productModel.unit
                productSto.quantityFromShop = productModel.quantity
            }
        }
    }

    override fun onClick() {
        if (productsToShop.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.msg_list_empty), Toast.LENGTH_SHORT).show()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        binding.tfSearchProducts.isEnabled = false
        actived = false
        val dateCurrent = Calendar.getInstance().timeInMillis
        val listFinal: MutableList<ProductsToListModel> = mutableListOf()
        for (i in productsToShop.indices) {
            val prod = ProductsToListModel(
                    productsToShop[i].id,
                    productsToShop[i].name,
                    productsToShop[i].unit,
                    productsToShop[i].userId,
                    Constants.LIST_ID,
                    productsToShop[i].price,
                    productsToShop[i].quantity
            )
            listFinal.add(prod)
        }
        val listToSend = Shop(
                Constants.USERS,
                nameList,
                AuthAPI.getCurrenUser()!!.uid,
                listFinal,
                total,
                dateCurrent,
                rateChange
        )
        viewModel.deleteCurrentShop()
        viewModel.saveShop(listToSend)
        viewModel.saveProductsToStorage(productsToStorage)
        Toast.makeText(requireContext(), getString(R.string.save_data_ok), Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    override fun onClickAddToStorage(product: ProductsToShopModel) {
        val calendar = Calendar.getInstance()
        val productToStorage = StorageModel(
            product.id,
            product.name,
            product.unit,
            product.userId,
            product.quantity,
            calendar.time,
            product.quantity,
            product.price
        )
        productsToStorage.add(productToStorage)
    }

    override fun onClickRemoveToStorage(product: ProductsToShopModel) {
        var position = -1
        for (i in productsToStorage.indices) {
            if (productsToStorage[i].id == product.id) {
                position = i
            }
        }
        if (position > 0) {
            productsToStorage.remove(productsToStorage[position])
        }
    }

    private fun loadDeparmentsToShow() {
        binding.chipGroup.removeAllViews()
        deparmentsToShow.clear()
        for (dep in deparments) {
            for (pro in productsShared) {
                if (dep.name == pro.deparment && !deparmentsToShow.contains(dep)) {
                    deparmentsToShow.add(dep)
                    val chip = Chip(requireContext())
                    chip.text = dep.name
                    chip.isCheckable = true
                    chip.isClickable = true
                    chip.setChipBackgroundColorResource(getColorPrimary())
                    chip.setOnClickListener {
                        if (chip.isChecked) {
                            deparmentsFilter.add(dep)
                        } else {
                            deparmentsFilter.remove(dep)
                        }
                        filterDep()
                    }

                    binding.chipGroup.addView(chip)
                }
            }
        }
    }

    private fun filterDep() {
        productsFiltered.clear()
        for (dep in deparmentsFilter) {
            for (pro in productsToAdd) {
                if (pro.deparment == dep.name && !productsFiltered.contains(pro)) productsFiltered.add(pro)
            }
        }
        if (deparmentsFilter.isEmpty()) addShopAdapter.updateList(productsToAdd)
        else addShopAdapter.updateList(productsFiltered)
    }

    private fun getColorPrimary(): Int {
        val typedValue = TypedValue()
        requireActivity().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.resourceId
    }
}