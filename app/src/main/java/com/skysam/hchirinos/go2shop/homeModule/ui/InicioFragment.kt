package com.skysam.hchirinos.go2shop.homeModule.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.classView.ProductSaveFromList
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI
import com.skysam.hchirinos.go2shop.comunicationAPI.CloudMessaging
import com.skysam.hchirinos.go2shop.common.models.Product
import com.skysam.hchirinos.go2shop.productsModule.ui.AddProductDialog
import com.skysam.hchirinos.go2shop.databinding.FragmentInicioBinding
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenter
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenterClass
import com.skysam.hchirinos.go2shop.initModule.ui.InitActivity
import com.skysam.hchirinos.go2shop.listsModule.ui.addListWish.AddListWishDialog
import com.skysam.hchirinos.go2shop.shopsModule.ui.addListShop.AddShopActivity
import com.skysam.hchirinos.go2shop.viewmodels.MainViewModel
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

class InicioFragment : Fragment(), InicioView, ProductSaveFromList {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private var products: MutableList<Product> = mutableListOf()
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
        binding.btnNewShop.setOnClickListener {
            requireActivity().startActivity(Intent(requireContext(),
               AddShopActivity::class.java))
        }
        binding.btnNewList.setOnClickListener {
            val addListWishDialog = AddListWishDialog()
            addListWishDialog.show(requireActivity().supportFragmentManager, tag)
        }
        binding.btnNewProduct.setOnClickListener {
            val addProductDialog = AddProductDialog(null, this, products)
            addProductDialog.show(requireActivity().supportFragmentManager, tag)
        }
        binding.btnInventory.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_home_to_storageFragment)
        }
        loadViewModels()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModels() {
        viewModel.shops.observe(viewLifecycleOwner) { shop ->
            if (!shop.isNullOrEmpty()) {
                val date = DateFormat.getDateInstance().format(Date(shop[0].dateCreated))
                val ammount = NumberFormat.getInstance().format(shop[0].total)
                binding.textHomeFirstLine.text =
                    getString(R.string.text_last_shopping_first_line, date)
                binding.textHomeSecondLine.text =
                    getString(R.string.text_last_shopping_second_line, ammount)
                return@observe
            }
            binding.textHomeFirstLine.text = getString(R.string.text_no_shopping)
            binding.textHomeSecondLine.text = getString(R.string.text_empty)
        }
        viewModel.products.observe(viewLifecycleOwner) {
            if (_binding != null) {
                products.clear()
                products.addAll(it)
            }
        }
        viewModel.users.observe(viewLifecycleOwner) {
            var isExists = false
            for (user in it) {
                if (user.id == AuthAPI.getCurrenUser()?.uid) isExists = true
            }
            if (!isExists) viewModel.addUser(AuthAPI.getCurrenUser()!!)
        }
        viewModel.syncReady.observe(viewLifecycleOwner) {
            if (!it) syncServer()
        }
    }

    private fun signOut() {
        CloudMessaging.unsubscribeTopicMessagingForUser()
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
        binding.progressBar.visibility = View.VISIBLE
        snackbar = Snackbar.make(binding.root, R.string.msg_sync, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    override fun resultSync(statusOk: Boolean) {
        if (_binding != null) {
            val msg = if (statusOk) {
                getString(R.string.msg_sync_ok)
            } else {
                getString(R.string.msg_sync_error)
            }
            viewModel.syncReadyTrue()
            binding.progressBar.visibility = View.GONE
            snackbar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }

    override fun productSave(product: Product) {
        viewModel.addProduct(product)
    }
}