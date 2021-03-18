package com.skysam.hchirinos.go2shop.listsModule.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.common.classView.UpdatedListWish
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.database.room.entities.Product
import com.skysam.hchirinos.go2shop.databinding.FragmentListsBinding
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListsWishPresenter
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListsWishPresenterClass
import com.skysam.hchirinos.go2shop.listsModule.ui.editListWish.EditListWishDialog

class ListsWishFragment : Fragment(), ListsWishView, OnClickList, UpdatedListWish {

    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListsWishAdapter
    private lateinit var listsWishPresenter: ListsWishPresenter
    private var listsWish: MutableList<ListWish> = mutableListOf()
    private var listToDelete: MutableList<ListWish> = mutableListOf()
    private var listToRestored: MutableList<ListWish> = mutableListOf()
    private var positionsToDelete: MutableList<Int> = mutableListOf()
    var actionMode: androidx.appcompat.view.ActionMode? = null
    var actionModeActived = false
    var firstPositionToDelete = 0

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
        val list = listsWish[position]
        if (listToDelete.contains(list)) {
            listToDelete.remove(list)
            positionsToDelete.remove(position)
        } else {
            listToDelete.add(list)
            positionsToDelete.add(position)
        }
        if (listToDelete.size == 1 && !actionModeActived) {
            actionMode = (activity as AppCompatActivity).startSupportActionMode(callback)
            actionModeActived = true
            firstPositionToDelete = position
        }
        if (listToDelete.isEmpty()) {
            adapter.clearListToDelete()
            actionModeActived = false
            (activity as AppCompatActivity).startSupportActionMode(callback)!!.finish()
        }
        actionMode?.title = "Seleccionado ${listToDelete.size}"
    }

    override fun onClickEdit(position: Int) {
        val editListWishDialog = EditListWishDialog(listsWish[position], position, this)
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

    override fun resultDeleteLists(statusOk: Boolean, msg: String) {
        if (_binding != null) {
            if (!statusOk) {
                for (i in listToRestored.indices) {
                    listsWish.add(positionsToDelete[i], listToRestored[i])
                }
                adapter.updateList(listsWish)
                binding.rvList.scrollToPosition(firstPositionToDelete)
            }
        }
    }

    override fun updatedListWish(position: Int, listWishResult: ListWish) {
        listsWish[position] = listWishResult
        adapter.updateList(listsWish)
    }

    private val callback = object : androidx.appcompat.view.ActionMode.Callback {

        override fun onCreateActionMode(
            mode: androidx.appcompat.view.ActionMode?,
            menu: Menu?
        ): Boolean {
            requireActivity().menuInflater.inflate(R.menu.contextual_action_bar_products, menu)
            return true
        }

        override fun onPrepareActionMode(
            mode: androidx.appcompat.view.ActionMode?,
            menu: Menu?
        ): Boolean {
            return false
        }

        override fun onActionItemClicked(
            mode: androidx.appcompat.view.ActionMode?,
            item: MenuItem?
        ): Boolean {
            return when (item?.itemId) {
                R.id.action_delete -> {
                    listToRestored.addAll(listToDelete)
                    listsWish.removeAll(listToDelete)
                    Snackbar.make(binding.rvList, getString(R.string.text_deleting), Snackbar.LENGTH_INDEFINITE)
                        .setDuration(3500)
                        .setAction(getString(R.string.btn_undo)) {
                            for (i in listToRestored.indices) {
                                listsWish.add(positionsToDelete[i], listToRestored[i])
                            }
                            listToRestored.clear()
                            positionsToDelete.clear()
                            adapter.updateList(listsWish)
                            binding.rvList.scrollToPosition(firstPositionToDelete)
                        }
                        .show()
                    actionMode?.finish()

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (listToRestored.isNotEmpty()) {
                            listsWishPresenter.deleteLists(listToRestored)
                            listToRestored.clear()
                            positionsToDelete.clear()
                        }
                    }, 4500)
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
            actionModeActived = false
            listToDelete.clear()
            adapter.clearListToDelete()
            adapter.updateList(listsWish)
        }
    }
}