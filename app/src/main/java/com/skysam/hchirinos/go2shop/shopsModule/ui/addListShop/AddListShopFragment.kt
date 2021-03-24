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
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.*
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.FragmentAddListShopBinding
import com.skysam.hchirinos.go2shop.listsModule.ui.addListWish.AddWishListAdapter
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.shopsModule.viewModel.AddListShopViewModel
import com.skysam.hchirinos.go2shop.shopsModule.viewModel.SharedViewModel
import java.text.NumberFormat

class AddListShopFragment : Fragment(), OnClickList, ProductSaveFromList,
        OnClickExit, OnSwitchChange {
    private val addListShopViewModel: AddListShopViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentAddListShopBinding? = null
    private val binding get() = _binding!!
    private var productsFromDB: MutableList<Product> = mutableListOf()
    private var productsToAdd: MutableList<ProductsToListModel> = mutableListOf()
    private var productsDuplicated: MutableList<ProductsToListModel> = mutableListOf()
    private var productsName = mutableListOf<String>()
    private lateinit var addListShopAdapter: AddListShopAdapter
    private var rateChange: Double = 0.0
    private var total: Double = 0.0
    private var actived = true

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

        loadViewModels()
        binding.rvList.setHasFixedSize(true)
        addListShopAdapter = AddListShopAdapter(productsToAdd, this)
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
            var positionSelected = 0
            val nameSelected = parent.getItemAtPosition(position)
            for (i in productsName.indices) {
                positionSelected = productsName.indexOf(nameSelected)
            }
            addProductToList(positionSelected)
        }
    }

    private fun loadViewModels() {
        sharedViewModel.nameShop.observe(viewLifecycleOwner, {
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
            toolbar.title = it
            toolbar.setNavigationIcon(R.drawable.ic_close_24)
        })
        sharedViewModel.productsSelected.observe(viewLifecycleOwner, {
            if (it.size >= 2) {
                for (i in it.indices) {
                    var j = i + 1
                    while (j <= it.lastIndex) {
                        if (it[i].name == it[j].name) {
                            if (!productsDuplicated.contains(it[i])) {
                                productsDuplicated.add(it[i])
                            }
                            productsDuplicated.add(it[j])
                        }
                        j++
                    }
                }
            }
            val test = productsDuplicated.size
            productsToAdd.addAll(it)
            addListShopAdapter.updateList(it)
        })
        sharedViewModel.rateChange.observe(viewLifecycleOwner, {
            rateChange = it
        })
        addListShopViewModel.getProducts().observe(viewLifecycleOwner, {
            productsFromDB.addAll(it)
            fillListProductsDB(it)
        })
        addListShopViewModel.productsSelected.observe(viewLifecycleOwner, {
            //productsToAdd.addAll(it)
            //addListShopAdapter.updateList(it)
        })
        addListShopViewModel.productInList.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
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
        addListShopViewModel.addProductToList(productSelected)
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
        if (actived) {
            addListShopViewModel.removeProductFromList(position)
        }
    }

    override fun onClickEdit(position: Int) {

    }

    override fun productSave(product: Product) {

    }

    override fun onClickExit() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_addListShopFragment_to_nav_home)
    }

    override fun switchChange(
        isChecked: Boolean,
        product: ProductsToListModel?,
        list: MutableList<ProductsToListModel>?
    ) {

    }
}