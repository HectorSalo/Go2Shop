package com.skysam.hchirinos.go2shop.listsModule.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.databinding.FragmentListsBinding

class ListsFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
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
        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textSlideshow.text = it
        })
        return binding.root
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
}