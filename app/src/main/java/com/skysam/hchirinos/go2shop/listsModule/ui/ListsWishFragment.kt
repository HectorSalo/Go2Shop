package com.skysam.hchirinos.go2shop.listsModule.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.common.classView.UpdatedListWish
import com.skysam.hchirinos.go2shop.database.room.entities.ListWish
import com.skysam.hchirinos.go2shop.databinding.FragmentListsBinding
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListsWishPresenter
import com.skysam.hchirinos.go2shop.listsModule.presenter.ListsWishPresenterClass
import com.skysam.hchirinos.go2shop.listsModule.ui.editListWish.EditListWishDialog
import java.util.*

class ListsWishFragment : Fragment(), ListsWishView, OnClickList, UpdatedListWish, SearchView.OnQueryTextListener {

    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListsWishAdapter
    private lateinit var listsWishPresenter: ListsWishPresenter
    private lateinit var search: SearchView
    private var listsWish: MutableList<ListWish> = mutableListOf()
    private var listToDelete: MutableList<ListWish> = mutableListOf()
    private var listToRestored: MutableList<ListWish> = mutableListOf()
    private var listSearch: MutableList<ListWish> = mutableListOf()
    private var positionsToDelete: MutableList<Int> = mutableListOf()
    var actionMode: androidx.appcompat.view.ActionMode? = null
    var actionModeActived = false
    var firstPositionToDelete = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
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
        val item2 = menu.findItem(R.id.action_sync)
        item2.isVisible = false
        item.isVisible = false
        val itemSearch = menu.findItem(R.id.action_search)
        search = itemSearch.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickDelete(position: Int) {
        var positionOriginal = -1
        val listWish: ListWish
        if (listSearch.isEmpty() || (listSearch.size == listsWish.size)) {
            listWish = listsWish[position]
            positionOriginal = position
        } else {
            listWish = listSearch[position]
            for (i in listsWish.indices) {
                if (listsWish[i].id == listWish.id) {
                    positionOriginal = i
                }
            }
        }
        if (listToDelete.contains(listWish)) {
            listToDelete.remove(listWish)
            positionsToDelete.remove(positionOriginal)
        } else {
            listToDelete.add(listWish)
            positionsToDelete.add(positionOriginal)
        }
        if (listToDelete.size == 1 && !actionModeActived) {
            actionMode = (activity as AppCompatActivity).startSupportActionMode(callback)
            actionModeActived = true
            firstPositionToDelete = positionOriginal
        }
        if (listToDelete.isEmpty()) {
            adapter.clearListToDelete()
            actionModeActived = false
            (activity as AppCompatActivity).startSupportActionMode(callback)!!.finish()
        }
        actionMode?.title = "Seleccionado ${listToDelete.size}"
    }

    override fun onClickEdit(position: Int) {
        val listSelected: ListWish = if (listSearch.isEmpty() || (listSearch.size == listsWish.size)) {
            listsWish[position]
        } else {
            listSearch[position]
        }
        val editListWishDialog = EditListWishDialog(listSelected, position, this)
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
        if (listSearch.isEmpty()) {
            listsWish[position] = listWishResult
            adapter.updateList(listsWish)
        } else {
            var positionOriginal = -1
            for (i in listsWish.indices) {
                if (listsWish[i].id == listWishResult.id) {
                    positionOriginal = i
                }
            }
            listsWish[positionOriginal] = listWishResult
            listSearch[position] = listWishResult
            adapter.updateList(listSearch)
        }
        binding.rvList.scrollToPosition(position)
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
                            listSearch.clear()
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
            listSearch.clear()
            search.onActionViewCollapsed()
            listToDelete.clear()
            adapter.clearListToDelete()
            adapter.updateList(listsWish)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (listsWish.isEmpty()) {
            Toast.makeText(context, getString(R.string.text_list_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.toLowerCase(Locale.ROOT)
            listSearch.clear()

            for (list in listsWish) {
                if (list.name.toLowerCase(Locale.ROOT).contains(userInput)) {
                    listSearch.add(list)
                }
            }
            adapter.updateList(listSearch)
        }
        return false
    }
}