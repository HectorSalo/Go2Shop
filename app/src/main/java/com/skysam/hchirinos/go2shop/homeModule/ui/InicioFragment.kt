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
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.databinding.FragmentInicioBinding
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenter
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenterClass
import com.skysam.hchirinos.go2shop.initModule.ui.InitActivity
import com.skysam.hchirinos.go2shop.listsModule.ui.addListWish.AddListWishDialog
import com.skysam.hchirinos.go2shop.shopsModule.ui.AddListShopActivity
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

class InicioFragment : Fragment(), InicioView {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var inicioPresenter: InicioPresenter
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inicioPresenter = InicioPresenterClass(this)
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
        if (AuthAPI.getCurrenUser() != null) {
            syncServer()
        }
        homeViewModel.listFlow.observe(viewLifecycleOwner, {shop->
            if (!shop.isNullOrEmpty()) {
                val listFinal = shop.sortedWith(compareBy { it.dateCreated }).toMutableList()
                val date = DateFormat.getDateInstance().format(Date(listFinal[listFinal.size - 1].dateCreated))
                val ammount = NumberFormat.getInstance().format(listFinal[listFinal.size - 1].total)
                binding.textHomeFirstLine.text = getString(R.string.text_last_shopping_first_line, date)
                binding.textHomeSecondLine.text = getString(R.string.text_last_shopping_second_line, ammount)
                return@observe
            }
            binding.textHomeFirstLine.text = getString(R.string.text_no_shopping)
            binding.textHomeSecondLine.text = getString(R.string.texto_vacio)
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
        } else if (item.itemId == R.id.action_sync) {
            syncServer()
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
                requireActivity().startActivity(Intent(requireContext(), InitActivity::class.java))
                requireActivity().finish()
            }
    }

    private fun syncServer() {
        inicioPresenter.getValueWeb()
        inicioPresenter.getProductsFromFirestore()
        binding.progressBar.visibility = View.VISIBLE
        snackbar = Snackbar.make(binding.root, R.string.msg_sync, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun resultSync(statusOk: Boolean) {
        if (_binding != null) {
            val msg = if (statusOk) {
                getString(R.string.msg_sync_ok)
            } else {
                getString(R.string.msg_sync_error)
            }
            binding.progressBar.visibility = View.GONE
            snackbar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }
}