package com.skysam.hchirinos.go2shop.listsModule.ui.addListWish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.*
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.DialogAddWishListBinding
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListWishPresenter
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListWishPresenterClass
import com.skysam.hchirinos.go2shop.listsModule.ui.ListWishView
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.productsModule.ui.EditProductDialog

class AddListWishDialog : DialogFragment(), ListWishView, OnClickList, ProductSaveFromList, OnClickExit, EditProductFromList {
    private lateinit var listWishPresenter: ListWishPresenter
    private var _binding: DialogAddWishListBinding? = null
    private val binding get() = _binding!!
    private var productsFromDB: MutableList<Product> = mutableListOf()
    private var productsToAdd: MutableList<Product> = mutableListOf()
    private lateinit var addWishListAdapter: AddWishListAdapter
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddWishListBinding.inflate(inflater, container, false)
        listWishPresenter = ListWishPresenterClass(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listWishPresenter.getProducts()
        binding.rvList.setHasFixedSize(true)
        addWishListAdapter = AddWishListAdapter(productsToAdd, this)
        binding.rvList.adapter = addWishListAdapter
        binding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.tvTotal.text = getString(R.string.text_total_list, total)
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
        binding.etSarchProduct.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Keyboard.close(binding.root)
            addProductToList(position)
        }

        binding.fabCancel.setOnClickListener {
            val exitDialog = ExitDialog(this)
            exitDialog.show(requireActivity().supportFragmentManager, tag)
        }
    }

    private fun addProductToList(position: Int) {
        val productSelected = productsFromDB[position]
        if (productsToAdd.contains(productSelected)) {
            Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
            return
        }
        productsToAdd.add(productSelected)
        addWishListAdapter.updateList(productsToAdd)
        binding.etSarchProduct.setText("")

        val subtotal = productSelected.quantity * productSelected.price
        sumTotal(subtotal)
    }

    private fun fillListProductsDB(list: MutableList<Product>){
        val productsName = mutableListOf<String>()
        for (i in list.indices) {
            productsName.add(i, list[i].name)
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, productsName)
        binding.etSarchProduct.setAdapter(adapter)
    }

    private fun sumTotal(subtotal: Double) {
        total += subtotal
        binding.tvTotal.text = getString(R.string.text_total_list, total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun resultGetProducts(products: MutableList<Product>) {
        productsFromDB = products
        fillListProductsDB(products)
    }

    override fun onClickDelete(position: Int) {
        val productSelected = productsToAdd[position]
        productsToAdd.removeAt(position)
        val subtotal = productSelected.quantity * productSelected.price * (-1)
        sumTotal(subtotal)
        addWishListAdapter.updateList(productsToAdd)
    }

    override fun onClickEdit(position: Int) {
        val productSelected = productsToAdd[position]
        val editProductDialog = EditProductDialog(productSelected, position, true, this)
        editProductDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun productSave(product: Product) {
        productsFromDB.add(product)
        fillListProductsDB(productsFromDB)
        addProductToList(productsFromDB.size - 1)
    }

    override fun onClickExit() {
        dialog!!.dismiss()
    }

    override fun editProduct(position: Int, product: Product) {
        val oldPrice = productsToAdd[position].price
        productsToAdd[position] = product
        val subtotal = (product.quantity * product.price) - oldPrice
        sumTotal(subtotal)
        addWishListAdapter.updateList(productsToAdd)
    }
}
