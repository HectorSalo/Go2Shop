package com.skysam.hchirinos.go2shop.listsModule.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.databinding.FragmentListsBinding
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListsWishPresenter
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListsWishPresenterClass
import com.skysam.hchirinos.go2shop.listsModule.ui.editListWish.EditListWishDialog

class ListsWishFragment : Fragment(), ListsWishView, OnClickList {

    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListsWishAdapter
    private lateinit var listsWishPresenter: ListsWishPresenter
    private var listsWish: MutableList<ListWish> = mutableListOf()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        /*addWishListViewModel =
                ViewModelProvider(this).get(AddWishListViewModel::class.java)
        addWishListViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.tvSarchProduct.text = it
        })*/

        _binding = FragmentListsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        listsWishPresenter = ListsWishPresenterClass(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listsWishPresenter.getLists()
        adapter = ListsWishAdapter(listsWish, this)
        binding.rvList.setHasFixedSize(true)
        binding.rvList.adapter = adapter
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

    override fun onClickDelete(position: Int) {

    }

    override fun onClickEdit(position: Int) {
        val editListWishDialog = EditListWishDialog(listsWish[position])
        editListWishDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun resultGetLists(lists: MutableList<ListWish>) {
        binding.progressBar.visibility = View.GONE
        if (lists.isNullOrEmpty()) {
            binding.tvListEmpty.visibility = View.VISIBLE
        } else {
            listsWish.addAll(lists)
            adapter.updateList(lists)
            binding.rvList.visibility = View.VISIBLE
        }
    }
}