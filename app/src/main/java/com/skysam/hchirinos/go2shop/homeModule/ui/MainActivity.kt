package com.skysam.hchirinos.go2shop.homeModule.ui

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.database.firebase.AuthAPI
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenter
import com.skysam.hchirinos.go2shop.homeModule.presenter.InicioPresenterClass

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var inicioPresenter: InicioPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        inicioPresenter = InicioPresenterClass()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val view = navView.getHeaderView(0)
        val tvNameUser = view.findViewById(R.id.title_nav_header) as TextView
        val tvEmailUser = view.findViewById(R.id.subtitle_nav_header) as TextView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_history, R.id.nav_lists, R.id.nav_products
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val user = AuthAPI.getCurrenUser()
        if (user != null) {
            inicioPresenter.getValueWeb()
            inicioPresenter.getProductsFromFirestore()
            inicioPresenter.getListsWishFromFirestore()
            tvNameUser.text = user.displayName
            tvEmailUser.text = user.email
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}