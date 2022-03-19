package com.skysam.hchirinos.go2shop.listsShared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.*
import com.skysam.hchirinos.go2shop.common.models.ListShared
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.DialogAddWishListBinding
import com.skysam.hchirinos.go2shop.listsModule.ui.addListWish.AddWishListAdapter
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.productsModule.ui.EditProductDialog
import java.text.NumberFormat

/**
 * Created by Hector Chirinos (Home) on 4/11/2021.
 */
class EditListSharedDialog(private val listShared: ListShared): DialogFragment(), OnClickList,
    ProductSaveFromList, OnClickExit, UpdatedProduct {
    private var _binding: DialogAddWishListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListsSharedViewModel by activityViewModels()
    private var productsFromDB: MutableList<Product> = mutableListOf()
    private var productsToAdd: MutableList<Product> = mutableListOf()
    private var productsOriginal: MutableList<ProductsToListModel> = mutableListOf()
    private var productsName = mutableListOf<String>()
    private lateinit var addWishListAdapter: AddWishListAdapter
    private var total: Double = 0.0
    private var actived = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddWishListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addWishListAdapter = AddWishListAdapter(productsToAdd, this)
        binding.rvList.adapter = addWishListAdapter
        binding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.etNameList.setText(listShared.name)
        binding.etNameList.doAfterTextChanged { binding.tfNameList.error = null }
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
        binding.fabSave.setOnClickListener { validateToSave() }
        binding.fabCancel.setOnClickListener {
            val exitDialog = ExitDialog(this)
            exitDialog.show(requireActivity().supportFragmentManager, tag)
        }
        loadViewModels()
        sumTotal(listShared.total)
        loadProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModels() {
        viewModel.products.observe(viewLifecycleOwner) {
            if (_binding != null) {
                productsFromDB.clear()
                productsFromDB.addAll(it)
                fillListProductsDB(productsFromDB)
            }
        }
    }

    private fun fillListProductsDB(list: MutableList<Product>){
        productsName.clear()
        for (i in list.indices) {
            productsName.add(i, list[i].name)
        }
        val adapterSearchProduct = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, productsName.sorted())
        binding.etSarchProduct.setAdapter(adapterSearchProduct)
    }

    private fun validateToSave() {
        Keyboard.close(binding.root)
        binding.tfNameList.error = null
        val nameList = binding.etNameList.text.toString()
        if (nameList.isEmpty()) {
            binding.tfNameList.error = getString(R.string.error_field_empty)
            binding.etNameList.requestFocus()
            return
        }
        if (productsToAdd.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.msg_list_empty), Toast.LENGTH_SHORT).show()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        binding.fabSave.isEnabled = false
        binding.fabCancel.isEnabled = false
        binding.tfNameList.isEnabled = false
        binding.tfSearchProducts.isEnabled = false
        actived = false

        val listFinal: MutableList<ProductsToListModel> = mutableListOf()
        for (i in productsToAdd.indices) {
            val prod = ProductsToListModel(
                productsToAdd[i].id,
                productsToAdd[i].name,
                productsToAdd[i].unit,
                listShared.listProducts[0].userId,
                listShared.listProducts[0].listId,
                productsToAdd[i].price,
                productsToAdd[i].quantity
            )
            listFinal.add(prod)
        }

        listShared.name = nameList
        listShared.listProducts.clear()
        listShared.listProducts.addAll(listFinal)
        viewModel.updateListShared(listShared)
        dismiss()
    }

    private fun addProductToList(product: Product) {
        var exist = false
        for (i in productsToAdd.indices) {
            if (productsToAdd[i].name == product.name) {
                exist = true
            }
        }
        if (exist) {
            Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
            binding.rvList.scrollToPosition(productsToAdd.indexOf(product))
            return
        }
        productsToAdd.add(product)
        addWishListAdapter.updateList(productsToAdd)
        binding.etSarchProduct.setText("")

        val subtotal = product.quantity * product.price
        sumTotal(subtotal)
    }

    private fun sumTotal(subtotal: Double) {
        total += subtotal
        binding.tvTotal.text = getString(R.string.text_total_list, NumberFormat.getInstance().format(total))
    }

    private fun loadProducts() {
        productsOriginal.addAll(listShared.listProducts)
        for (i in listShared.listProducts.indices) {
            val product = Product(
                listShared.listProducts[i].id,
                listShared.listProducts[i].name,
                listShared.listProducts[i].unit,
                listShared.listProducts[i].userId,
                listShared.listProducts[i].price,
                listShared.listProducts[i].quantity
            )
            productsToAdd.add(product)
        }
        addWishListAdapter.updateList(productsToAdd)
    }

    override fun onClickExit() {
        dialog!!.dismiss()
    }

    override fun onClickDelete(position: Int) {
        if (actived) {
            val productSelected = productsToAdd[position]
            productsToAdd.removeAt(position)
            val subtotal = productSelected.quantity * productSelected.price * (-1)
            sumTotal(subtotal)
            addWishListAdapter.updateList(productsToAdd)
        }
    }

    override fun onClickEdit(position: Int) {
        if (actived) {
            val productSelected = productsToAdd[position]
            val editProductDialog = EditProductDialog(productSelected, position, true, this, null)
            editProductDialog.show(requireActivity().supportFragmentManager, tag)
        }
    }

    override fun updatedProduct(position: Int, product: Product) {
        val oldPrice = productsToAdd[position].price * productsToAdd[position].quantity
        productsToAdd[position] = product
        val subtotal = (product.quantity * product.price) - oldPrice
        sumTotal(subtotal)
        addWishListAdapter.updateList(productsToAdd)
    }

    override fun productSave(product: Product) {
        viewModel.addProduct(product)
        addProductToList(product)
    }
}