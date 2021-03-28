package com.skysam.hchirinos.go2shop.listsModule.ui.editListWish

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
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.DialogAddWishListBinding
import com.skysam.hchirinos.go2shop.listsModule.presenter.EditListWishPresenter
import com.skysam.hchirinos.go2shop.listsModule.presenter.EditListWishPresenterClass
import com.skysam.hchirinos.go2shop.listsModule.ui.addListWish.AddWishListAdapter
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductsPresenter
import com.skysam.hchirinos.go2shop.productsModule.presenter.ProductsPresenterClass
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.productsModule.ui.EditProductDialog
import com.skysam.hchirinos.go2shop.productsModule.ui.ProductsView
import java.text.NumberFormat
import java.util.*

/**
 * Created by Hector Chirinos on 16/03/2021.
 */
class EditListWishDialog(private val listWish: ListWish, private val position: Int, private val updatedListWish: UpdatedListWish):
        DialogFragment(), OnClickExit, ProductsView,
        OnClickList, UpdatedProduct, ProductSaveFromList, EditListWishView {
    private var _binding: DialogAddWishListBinding? = null
    private val binding get() = _binding!!
    private lateinit var productsPresenter: ProductsPresenter
    private lateinit var editListWishPresenter: EditListWishPresenter
    private var productsFromDB: MutableList<Product> = mutableListOf()
    private var productsToAdd: MutableList<Product> = mutableListOf()
    private var productsOriginal: MutableList<ProductsToListModel> = mutableListOf()
    private var productsName = mutableListOf<String>()
    private lateinit var listToSend: ListWish
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
        productsPresenter = ProductsPresenterClass(this)
        editListWishPresenter = EditListWishPresenterClass(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsPresenter.getProducts()
        addWishListAdapter = AddWishListAdapter(productsToAdd, this)
        binding.rvList.adapter = addWishListAdapter
        binding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.etNameList.setText(listWish.name)
        sumTotal(listWish.total)
        loadProducts()

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
            var positionSelected = 0
            val nameSelected = parent.getItemAtPosition(position)

            for (i in productsName.indices) {
                positionSelected = productsName.indexOf(nameSelected)
            }
            addProductToList(positionSelected)
        }
        binding.fabSave.setOnClickListener { validateToSave() }
        binding.fabCancel.setOnClickListener {
            val exitDialog = ExitDialog(this)
            exitDialog.show(requireActivity().supportFragmentManager, tag)
        }
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
        val dateCurrent = Calendar.getInstance().timeInMillis
        val listFinal: MutableList<ProductsToListModel> = mutableListOf()
        val listToSave: MutableList<ProductsToListModel> = mutableListOf()
        val listToDelete: MutableList<ProductsToListModel> = mutableListOf()
        val listToUpdate: MutableList<ProductsToListModel> = mutableListOf()
        for (i in productsToAdd.indices) {
            val prod = ProductsToListModel(
                productsToAdd[i].id,
                productsToAdd[i].name,
                productsToAdd[i].unit,
                listWish.listProducts[0].userId,
                listWish.listProducts[0].listId,
                productsToAdd[i].price,
                productsToAdd[i].quantity
            )
            listFinal.add(prod)
        }
        for (i in listFinal.indices) {
            var update = false
            for (j in productsOriginal.indices) {
                if (listFinal[i].id == productsOriginal[j].id) {
                    update = true
                }
            }
            if (update) {
                listToUpdate.add(listFinal[i])
            } else {
                listToSave.add(listFinal[i])
            }
        }
        for (i in productsOriginal.indices) {
            var delete = true
            for (j in listFinal.indices) {
                if (productsOriginal[i].id == listFinal[j].id) {
                    delete = false
                }
            }
            if (delete) {
                listToDelete.add(productsOriginal[i])
            }
        }
        listToSend = ListWish(
            listWish.id,
            nameList,
            listWish.userId,
            listFinal,
            total,
            listWish.dateCreated,
            dateCurrent
        )
        editListWishPresenter.editListWish(listToSend, listToSave, listToUpdate, listToDelete)
    }

    private fun addProductToList(position: Int) {
        var positionInList = -1
        var add = true
        val productSelected = productsFromDB[position]
        if (productsToAdd.contains(productSelected)) {
            Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
            return
        }
        for (i in productsToAdd.indices) {
            if (productsToAdd[i].name == productSelected.name) {
                add = false
                positionInList = i
            }
        }
        if (add) {
            productsToAdd.add(productSelected)
            addWishListAdapter.updateList(productsToAdd)
            binding.etSarchProduct.setText("")

            val subtotal = productSelected.quantity * productSelected.price
            sumTotal(subtotal)
        } else {
            Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
            binding.rvList.scrollToPosition(positionInList)
        }
    }

    private fun sumTotal(subtotal: Double) {
        total += subtotal
        binding.tvTotal.text = getString(R.string.text_total_list, NumberFormat.getInstance().format(total))
    }

    private fun loadProducts() {
        productsOriginal.addAll(listWish.listProducts)
        for (i in listWish.listProducts.indices) {
            val product = Product(
                listWish.listProducts[i].id,
                listWish.listProducts[i].name,
                listWish.listProducts[i].unit,
                listWish.listProducts[i].userId,
                listWish.listProducts[i].price,
                listWish.listProducts[i].quantity
            )
            productsToAdd.add(product)
        }
        addWishListAdapter.updateList(productsToAdd)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickExit() {
        dialog!!.dismiss()
    }

    override fun resultGetProducts(products: MutableList<Product>) {
        productsFromDB.addAll(products)
        fillListProductsDB(products)
    }

    private fun fillListProductsDB(list: MutableList<Product>){
        for (i in list.indices) {
            productsName.add(i, list[i].name)
        }
        val adapterSearchProduct = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, productsName)
        binding.etSarchProduct.setAdapter(adapterSearchProduct)
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
        val oldPrice = productsToAdd[position].price
        productsToAdd[position] = product
        val subtotal = (product.quantity * product.price) - oldPrice
        sumTotal(subtotal)
        addWishListAdapter.updateList(productsToAdd)
    }

    override fun productSave(product: Product) {
        productsFromDB.add(product)
        fillListProductsDB(productsFromDB)
        addProductToList(productsFromDB.size - 1)
    }

    override fun resultEditListWishFirestore(statusOk: Boolean, msg: String) {
        if (_binding != null) {
            if (statusOk) {
                updatedListWish.updatedListWish(position, listToSend)
                Toast.makeText(requireContext(), getString(R.string.update_data_ok), Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()
            } else {
                binding.progressBar.visibility = View.GONE
                binding.fabSave.isEnabled = true
                binding.fabCancel.isEnabled = true
                binding.tfNameList.isEnabled = true
                binding.tfSearchProducts.isEnabled = true
                actived = true
                Toast.makeText(requireContext(), getString(R.string.update_data_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
}