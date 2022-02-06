package com.skysam.hchirinos.go2shop.listsShared

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.models.ListShared
import com.skysam.hchirinos.go2shop.common.models.User
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.databinding.FragmentListsSharedBinding

class ListsSharedFragment : Fragment(), OnClick, SearchView.OnQueryTextListener {
    private var _binding: FragmentListsSharedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListsSharedViewModel by activityViewModels()
    private lateinit var adapter: ListSharedAdapter
    private lateinit var search: SearchView
    private var lists: MutableList<ListShared> = mutableListOf()
    private var listSearch: MutableList<ListShared> = mutableListOf()
    private val users = mutableListOf<User>()
    private var positionEdit = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListsSharedBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ListSharedAdapter(lists, this)
        binding.rvList.setHasFixedSize(true)
        binding.rvList.adapter = adapter
        binding.rvList.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    binding.fab.hide()
                } else {
                    binding.fab.show()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        binding.fab.setOnClickListener {
            selectUserToShare()
        }
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

    override fun onResume() {
        super.onResume()
        binding.fab.show()
    }

    override fun onPause() {
        super.onPause()
        binding.fab.hide()
    }

    private fun loadViewModels() {
        viewModel.listsShared.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    lists.clear()
                    lists.addAll(it)
                    adapter.updateList(lists)
                    binding.rvList.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                    if (listSearch.isNotEmpty()) {
                        binding.rvList.scrollToPosition(positionEdit)
                        listSearch.clear()
                    }
                } else {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvList.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            }
        })
        viewModel.users.observe(viewLifecycleOwner, {
            if (_binding != null) {
                users.clear()
                for (user in it) {
                    if (user.id != AuthAPI.getCurrenUser()?.uid) {
                        users.add(user)
                    }
                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (lists.isEmpty()) {
            Toast.makeText(context, getString(R.string.text_list_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.lowercase()
            listSearch.clear()

            for (list in lists) {
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
        val arrayChecked = BooleanArray(usersAvailable.size)
        val emailsSelected = mutableListOf<String>()
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_dialog_share))
            .setMultiChoiceItems(arrayEmails.toTypedArray(), arrayChecked) { _, i, isChecked ->
                if (isChecked) {
                    emailsSelected.add(arrayEmails[i])
                } else {
                    emailsSelected.remove(arrayEmails[i])
                }
            }
            .setPositiveButton(R.string.btn_share, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
        val buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener {
            if (emailsSelected.isEmpty()) {
                dialog.dismiss()
                return@setOnClickListener
            }
            val userIdSelected = mutableListOf<String>()
            for (email in emailsSelected) {
                for (user in users) {
                    if (email == user.email) userIdSelected.add(user.id)
                }
            }
            val addListSharedDialog = AddListSharedDialog(userIdSelected)
            addListSharedDialog.show(requireActivity().supportFragmentManager, tag)
            dialog.dismiss()
        }
    }

    override fun addUsers(list: ListShared) {
        val arrayEmails = mutableListOf<String>()
        val usersAvailable = mutableListOf<User>()
        for (user in users) {
            if (!user.email.contains("test")) {
                if (!list.usersId.contains(user.id)) {
                    arrayEmails.add(user.email)
                    usersAvailable.add(user)
                }
            }
        }
        val arrayChecked = BooleanArray(usersAvailable.size)
        val emailsSelected = mutableListOf<String>()
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_dialog_share))
            .setMultiChoiceItems(arrayEmails.toTypedArray(), arrayChecked) { _, i, isChecked ->
                if (isChecked) {
                    emailsSelected.add(arrayEmails[i])
                } else {
                    emailsSelected.remove(arrayEmails[i])
                }
            }
            .setPositiveButton(R.string.btn_share, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
        val buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener {
            if (emailsSelected.isEmpty()) {
                dialog.dismiss()
                return@setOnClickListener
            }
            val userIdSelected = mutableListOf<String>()
            for (email in emailsSelected) {
                for (user in users) {
                    if (email == user.email) userIdSelected.add(user.id)
                }
            }
            list.usersId.addAll(userIdSelected)
            Toast.makeText(requireContext(), getString(R.string.text_sharing_list), Toast.LENGTH_LONG).show()
            viewModel.updateListShared(list)
            dialog.dismiss()
        }
    }

    override fun viewList(list: ListShared) {
        val editListSharedDialog = EditListSharedDialog(list)
        editListSharedDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteList(list: ListShared) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_delete_dialog))
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteList(list)
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }
}