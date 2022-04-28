package com.skysam.hchirinos.go2shop.deparments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.WrapContentLinearLayoutManager
import com.skysam.hchirinos.go2shop.common.models.Deparment
import com.skysam.hchirinos.go2shop.databinding.FragmentDeparmentsBinding
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel


class DeparmentsFragment : Fragment(), SearchView.OnQueryTextListener, OnClick {
    private var _binding: FragmentDeparmentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var search: SearchView
    private lateinit var adapterDeparment: DeparmentAdapter
    private val deparments = mutableListOf<Deparment>()
    private val listSearch = mutableListOf<Deparment>()
    private val positionEdit = 0
    private lateinit var wrapContentLinearLayoutManager: WrapContentLinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeparmentsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDeparments.setHasFixedSize(true)
        adapterDeparment = DeparmentAdapter(deparments, this)
        wrapContentLinearLayoutManager = WrapContentLinearLayoutManager(requireContext(),
            RecyclerView.VERTICAL, false)
        binding.rvDeparments.adapter = adapterDeparment
        binding.rvDeparments.layoutManager = wrapContentLinearLayoutManager
        binding.fab.setOnClickListener {
            val addDeparmentDialog = AddDeparmentDialog()
            addDeparmentDialog.show(requireActivity().supportFragmentManager, tag)
        }
        loadViewModels()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main, menu)
        val item2 = menu.findItem(R.id.action_sync)
        item2.isVisible = false
        val item = menu.findItem(R.id.action_cerrar_sesion)
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
        viewModel.deparments.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    val listTemp = mutableListOf<Deparment>()
                    listTemp.addAll(deparments)
                    deparments.clear()
                    deparments.addAll(it)
                    for (depCloud in it) {
                        var add = true
                        if (listSearch.isEmpty()) {
                            for (depLocal in listTemp) {
                                if (depLocal.id == depCloud.id) {
                                    add = false
                                    if (depLocal.name != depCloud.name)
                                        adapterDeparment.notifyItemChanged(it.indexOf(depCloud))
                                }
                                if (deparments.size < listTemp.size) {
                                    if (!deparments.contains(depLocal))
                                        adapterDeparment.notifyItemRemoved(listTemp.indexOf(depLocal))
                                }
                            }
                            if (add) adapterDeparment.notifyItemInserted(it.indexOf(depCloud))
                        } else {
                            adapterDeparment.updateList(deparments)
                            listSearch.clear()
                            search.onActionViewCollapsed()
                        }
                    }
                    binding.rvDeparments.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                    if (listSearch.isNotEmpty()) {
                        binding.rvDeparments.scrollToPosition(positionEdit)
                        listSearch.clear()
                    }
                } else {
                    binding.rvDeparments.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val userInput: String = newText!!.lowercase()
        listSearch.clear()

        if (newText != "") {
            for (dep in deparments) {
                if (dep.name.lowercase().contains(userInput)) {
                    listSearch.add(dep)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adapterDeparment.updateList(listSearch)
        } else {
            adapterDeparment.updateList(deparments)
            binding.lottieAnimationView.visibility = View.GONE
        }
        return false
    }

    override fun edit(deparment: Deparment) {
        viewModel.departmentToEdit(deparment)
        val editDeparmentDialog = EditDeparmentDialog()
        editDeparmentDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun delete(deparment: Deparment) {
        viewModel.deleteDeparment(deparment)
    }
}