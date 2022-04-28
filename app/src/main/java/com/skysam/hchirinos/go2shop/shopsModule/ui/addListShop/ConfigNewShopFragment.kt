package com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.common.Keyboard
import com.skysam.hchirinos.go2shop.common.classView.ExitDialog
import com.skysam.hchirinos.go2shop.common.classView.OnClickExit
import com.skysam.hchirinos.go2shop.common.classView.OnSwitchChange
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.common.models.ProductsToShopModel
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.databinding.FragmentConfigNewShopBinding
import com.skysam.hchirinos.go2shop.viewmodels.AddShopViewModel
import java.text.DecimalFormatSymbols
import java.text.NumberFormat


class ConfigNewShopFragment : Fragment(), OnClickExit, OnSwitchChange {

    private var _binding: FragmentConfigNewShopBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddShopViewModel by activityViewModels()
    private lateinit var adapter: ConfigNewShopAdapter
    private val lists: MutableList<ListWish> = mutableListOf()
    private val myLists: MutableList<ListWish> = mutableListOf()
    private val listsShared: MutableList<ListWish> = mutableListOf()
    private val listsProducts: MutableList<ProductsToListModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigNewShopBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        NumberFormat.getInstance().isGroupingUsed = true
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
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.nav_products)
        loadViewModels()
        adapter = ConfigNewShopAdapter(lists, this)
        binding.rvLists.setHasFixedSize(true)
        binding.rvLists.adapter = adapter
        binding.etNameList.doAfterTextChanged { binding.tfNameList.error = null }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.new_shop, menu)
        val item = menu.findItem(R.id.action_check)
        item.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                getOut()
                true
            }
            R.id.action_check -> {
                validateData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModels() {
        viewModel.lists.observe(viewLifecycleOwner) {
            if (_binding != null) {
                myLists.clear()
                if (it.isNotEmpty()) {
                    myLists.addAll(it)
                    showList()
                }
            }
        }
        viewModel.listsShared.observe(viewLifecycleOwner) {
            if (_binding != null) {
                listsShared.clear()
                if (it.isNotEmpty()) {
                    for (list in it) {
                        val listToShow = ListWish(
                            list.id,
                            list.name,
                            Constants.USER_ID,
                            list.listProducts,
                            list.total,
                            list.dateCreated,
                            list.dateCreated
                        )
                        listsShared.add(listToShow)
                    }
                    showList()
                }
            }
        }
        viewModel
        viewModel.rateChange.observe(viewLifecycleOwner) {
            if (_binding != null) {
                binding.etCotizacion.setText(NumberFormat.getInstance().format(it))
            }
        }
    }

    private fun showList() {
        lists.clear()
        lists.addAll(listsShared)
        lists.addAll(myLists)
        if (lists.isNotEmpty()) {
            adapter.updateList(lists)
            binding.rvLists.visibility = View.VISIBLE
            binding.tvListEmpty.visibility = View.GONE
        } else {
            binding.rvLists.visibility = View.GONE
            binding.tvListEmpty.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun validateData() {
        Keyboard.close(binding.root)
        val decimalSeparator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
        var rateChange = 0.0
        binding.tfNameList.error = null
        binding.tfCotizacion.error = null
        if (binding.etNameList.text.isNullOrEmpty()) {
            binding.tfNameList.error = getString(R.string.error_field_empty)
            binding.etNameList.requestFocus()
            return
        }
        if (binding.etCotizacion.text.isNullOrEmpty()){
            binding.tfCotizacion.error = getString(R.string.error_field_empty)
            binding.etCotizacion.requestFocus()
            return
        }
        var rateChangeString = binding.etCotizacion.text.toString()
        if (decimalSeparator == ",") {
            try {
                rateChangeString = rateChangeString.replace(".", "").replace(",", ".")
                rateChange = rateChangeString.toDouble()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error en el número ingresado", Toast.LENGTH_SHORT).show()
            }
        } else {
            try {
                rateChangeString = rateChangeString.replace(",", "")
                rateChange = rateChangeString.toDouble()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error en el número ingresado", Toast.LENGTH_SHORT).show()
            }
        }
        if (rateChange <= 0) {
            binding.tfCotizacion.error = getString(R.string.error_field_zero)
            return
        }
        viewModel.sharedData(binding.etNameList.text.toString(), rateChange, listsProducts)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_configNewShopFragment_to_addListShopFragment)
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
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
        for (i in list!!.indices) {
            val productToShop = ProductsToListModel(
                list[i].id,
                list[i].name,
                list[i].unit,
                list[i].userId,
                nameList!!,
                list[i].price,
                list[i].quantity,
                list[i].deparment
            )
            if (isChecked) {
                listsProducts.add(productToShop)
            } else {
                listsProducts.remove(productToShop)
            }
        }
    }
}