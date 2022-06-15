package com.skysam.hchirinos.go2shop.listsModule.ui

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.OnClickList
import com.skysam.hchirinos.go2shop.common.classView.UpdatedListWish
import com.skysam.hchirinos.go2shop.common.models.ProductsToListModel
import com.skysam.hchirinos.go2shop.common.models.User
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.common.models.ListWish
import com.skysam.hchirinos.go2shop.databinding.FragmentListsBinding
import com.skysam.hchirinos.go2shop.listsModule.ui.editListWish.EditListWishDialog
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel

class ListsWishFragment : Fragment(), OnClickList, UpdatedListWish, SearchView.OnQueryTextListener {

    private var _binding: FragmentListsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ListsWishAdapter
    private lateinit var search: SearchView
    private var listsWish: MutableList<ListWish> = mutableListOf()
    private var listToDelete: MutableList<ListWish> = mutableListOf()
    private var listToRestored: MutableList<ListWish> = mutableListOf()
    private var listToSend = mutableListOf<ListWish>()
    private var listSearch: MutableList<ListWish> = mutableListOf()
    private var positionsToDelete: MutableList<Int> = mutableListOf()
    private val users = mutableListOf<User>()
    var actionMode: androidx.appcompat.view.ActionMode? = null
    private var deleting = false
    var actionModeActived = false
    var firstPositionToDelete = 0
    private var positionEdit = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ListsWishAdapter(listsWish, this)
        binding.rvList.setHasFixedSize(true)
        binding.rvList.adapter = adapter
        loadViewModels()
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

    private fun loadViewModels() {
        viewModel.listsWish.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    if (!deleting) {
                        listsWish.clear()
                        listsWish.addAll(it)
                        adapter.updateList(listsWish)
                        binding.rvList.visibility = View.VISIBLE
                        binding.tvListEmpty.visibility = View.GONE
                        if (listSearch.isNotEmpty()) {
                            binding.rvList.scrollToPosition(positionEdit)
                            listSearch.clear()
                        }
                    }
                    deleting = false
                } else {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvList.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            if (_binding != null) {
                users.clear()
                for (user in it) {
                    if (user.id != AuthAPI.getCurrenUser()?.uid) {
                        users.add(user)
                    }
                }
            }
        }
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
        positionEdit = listsWish.indexOf(listSelected)
        val editListWishDialog = EditListWishDialog(listSelected, position, this)
        editListWishDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun updatedListWish(
        position: Int,
        listWishResult: ListWish,
        productsToSave: MutableList<ProductsToListModel>,
        productsToUpdate: MutableList<ProductsToListModel>,
        productsToDelete: MutableList<ProductsToListModel>
    ) {
        viewModel.editListWish(listWishResult, productsToSave, productsToUpdate, productsToDelete)
        search.onActionViewCollapsed()
    }

    private val callback = object : androidx.appcompat.view.ActionMode.Callback {

        override fun onCreateActionMode(
            mode: androidx.appcompat.view.ActionMode?,
            menu: Menu?
        ): Boolean {
            requireActivity().menuInflater.inflate(R.menu.contextual_action_bar_lists, menu)
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
                            deleting = true
                            viewModel.deleteListsWish(listToRestored)
                            listToRestored.clear()
                            positionsToDelete.clear()
                        }
                    }, 4500)
                    true
                }
                R.id.action_share -> {
                    listToSend.addAll(listToDelete)
                    selectUserToShare()
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
            val userInput: String = newText!!.lowercase()
            listSearch.clear()

            for (list in listsWish) {
                if (list.name.lowercase().contains(userInput)) {
                    listSearch.add(list)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adapter.updateList(listSearch)
        }
        return false
    }

    private fun selectUserToShare() {
        val arrayEmails = mutableListOf<String>()
        val usersAvailable = mutableListOf<User>()
        for (user in users) {
            if (!user.email.contains("test")) {
                arrayEmails.add(user.email)
                usersAvailable.add(user)
            }
        }
        var userSelected: User? = null
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_dialog_share))
            .setSingleChoiceItems(arrayEmails.toTypedArray(), -1) { _, i ->
                userSelected = usersAvailable[i]
            }
            .setPositiveButton(R.string.btn_share, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
        val buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener {
            if (userSelected != null) {
                viewModel.shareLists(userSelected!!, listToSend)
                listToSend.clear()
                Toast.makeText(requireContext(), getString(R.string.text_sharing_list), Toast.LENGTH_LONG).show()
                actionMode?.finish()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_user_share_null), Toast.LENGTH_LONG).show()
            }
        }
    }
}