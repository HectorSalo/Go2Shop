package com.skysam.hchirinos.go2shop.homeModule.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.databinding.FragmentInicioBinding
import com.skysam.hchirinos.go2shop.listsModule.ui.addListWish.AddListWishDialog
import com.skysam.hchirinos.go2shop.shopsModule.ui.AddListShopActivity

class InicioFragment : Fragment() {

    private val requestIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == Activity.RESULT_OK) {
        }
    }

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AuthAPI.getCurrenUser() == null) {
            startAuthUI()
        }
    }

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
        homeViewModel.lastDateShop.observe(viewLifecycleOwner, {
            binding.textHomeFirstLine.text = getString(R.string.text_last_shopping_first_line, it)
        })
        homeViewModel.priceLastShop.observe(viewLifecycleOwner, {
            binding.textHomeSecondLine.text = getString(R.string.text_last_shopping_second_line, it)
        })
        binding.btnNewShop.setOnClickListener {
           requireActivity().startActivity(Intent(requireContext(),
               AddListShopActivity::class.java))
        }
        binding.btnNewList.setOnClickListener {
            val addListWishDialog = AddListWishDialog()
            addListWishDialog.show(requireActivity().supportFragmentManager, tag)
        }
        binding.btnNewProduct.setOnClickListener {
            val addProductDialog = AddProductDialog(null, null)
            addProductDialog.show(requireActivity().supportFragmentManager, tag)
        }
        binding.btnInventory.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_home_to_storageFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main, menu)
        val itemSearch = menu.findItem(R.id.action_search)
        itemSearch.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_cerrar_sesion) {
            signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        val provider = AuthAPI.getCurrenUser()!!.providerId
        AuthUI.getInstance().signOut(requireContext())
            .addOnSuccessListener {
                if (provider == "google.com") {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    val googleSingInClient : GoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                    googleSingInClient.signOut()
                }
                startAuthUI()
            }
    }

    private fun startAuthUI() {
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