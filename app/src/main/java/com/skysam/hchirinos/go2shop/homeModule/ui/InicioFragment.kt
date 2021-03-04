package com.skysam.hchirinos.go2shop.homeModule.ui

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.databinding.FragmentInicioBinding

class InicioFragment : Fragment() {

    private val requestIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
        }
    }

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNewProduct.setOnClickListener {
            val addProductDialog = AddProductDialog()
            addProductDialog.show(requireActivity().supportFragmentManager, tag)
        }
        binding.btnInventory.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_home_to_storageFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.action_search)
        item.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_cerrar_sesion) {
            testAuthUI()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun testAuthUI() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

// Create and launch sign-in intent
        requestIntentLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_Go2Shop)
                .build())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}