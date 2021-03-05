package com.skysam.hchirinos.go2shop.listsModule.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.FragmentListsBinding
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListWishPresenter
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListWishPresenterClass

class ListsFragment : Fragment(), ListWishView {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private lateinit var listWishPresenter: ListWishPresenter
    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        slideshowViewModel =
                ViewModelProvider(this).get(SlideshowViewModel::class.java)
        _binding = FragmentListsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        listWishPresenter = ListWishPresenterClass(this)
        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {
            //binding.tvSarchProduct.text = it
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listWishPresenter.getProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.action_cerrar_sesion)
        item.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun resultGetProducts(productsName: MutableList<String>) {
        if (!productsName.isNullOrEmpty()) {
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, productsName)
            binding.tvSarchProduct.setAdapter(adapter)
        }
    }
}