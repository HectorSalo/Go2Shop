package com.skysam.hchirinos.go2shop.historyModule.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        galleryViewModel =
                ViewModelProvider(this).get(GalleryViewModel::class.java)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textGallery.text = it
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