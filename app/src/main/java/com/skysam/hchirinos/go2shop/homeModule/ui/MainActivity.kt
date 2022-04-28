package com.skysam.hchirinos.go2shop.homeModule.ui

import android.os.Bundle
import android.widget.ImageView
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
import com.bumptech.glide.Glide
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.comunicationAPI.AuthAPI

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val view = navView.getHeaderView(0)
        val tvNameUser = view.findViewById(R.id.title_nav_header) as TextView
        val tvEmailUser = view.findViewById(R.id.subtitle_nav_header) as TextView
        val ivUser = view.findViewById(R.id.iv_header) as ImageView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_history, R.id.nav_lists, R.id.nav_lists_shared,
            R.id.nav_products, R.id.nav_deparments, R.id.nav_settings), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val user = AuthAPI.getCurrenUser()
        if (user != null) {
            tvNameUser.text = user.displayName
            tvEmailUser.text = user.email
            if (user.photoUrl != null) {
                Glide.with(this).load(user.photoUrl)
                    .circleCrop().into(ivUser)
            } else {
                Glide.with(this).load(R.drawable.ic_person_pin_48)
                    .circleCrop().into(ivUser)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}