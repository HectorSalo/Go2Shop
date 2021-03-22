package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.ExitDialog
import com.skysam.hchirinos.go2shop.common.classView.OnClickExit
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.common.classView.ProductSaveFromList
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.DialogAddWishListBinding
import com.skysam.hchirinos.go2shop.listsModule.ui.addListWish.AddWishListAdapter
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import java.text.NumberFormat

class AddListShopFragment : DialogFragment(), OnClickList, ProductSaveFromList,
        OnClickExit {
    private lateinit var addListShopViewModel: AddListShopViewModel
    private var _binding: DialogAddWishListBinding? = null
    private val binding get() = _binding!!
    private var productsFromDB: MutableList<Product> = mutableListOf()
    private var productsToAdd: MutableList<Product> = mutableListOf()
    private var productsName = mutableListOf<String>()
    private lateinit var addWishListAdapter: AddWishListAdapter
    private var total: Double = 0.0
    private var actived = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DialogAddWishListBinding.inflate(inflater, container, false)
        addListShopViewModel = ViewModelProvider(this).get(AddListShopViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListShopViewModel.getProducts().observe(viewLifecycleOwner, Observer {
            productsFromDB.addAll(it)
            fillListProductsDB(it)
        })
        binding.tfNameList.hint = getString(R.string.text_name_listShop)
        binding.rvList.setHasFixedSize(true)
        addWishListAdapter = AddWishListAdapter(productsToAdd, this)
        binding.rvList.adapter = addWishListAdapter
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
            var positionSelected = 0
            val nameSelected = parent.getItemAtPosition(position)
            for (i in productsName.indices) {
                positionSelected = productsName.indexOf(nameSelected)
            }
            addProductToList(positionSelected)
        }
        binding.fabSave.setOnClickListener { //validateToSave()
             }
        binding.fabCancel.setOnClickListener {
            val exitDialog = ExitDialog(this)
            exitDialog.show(requireActivity().supportFragmentManager, tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addProductToList(position: Int) {
        val productSelected = productsFromDB[position]
        if (productsToAdd.contains(productSelected)) {
            Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
            binding.rvList.scrollToPosition(position)
            return
        }
        productsToAdd.add(productSelected)
        addWishListAdapter.updateList(productsToAdd)
        binding.etSarchProduct.setText("")

        val subtotal = productSelected.quantity * productSelected.price
        sumTotal(subtotal)
    }

    private fun sumTotal(subtotal: Double) {
        total += subtotal
        binding.tvTotal.text = getString(R.string.text_total_list, NumberFormat.getInstance().format(total))
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

    }

    override fun productSave(product: Product) {

    }

    override fun onClickExit() {

    }
}