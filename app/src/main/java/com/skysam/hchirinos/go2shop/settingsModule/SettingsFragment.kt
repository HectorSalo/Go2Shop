package com.skysam.hchirinos.go2shop.settingsModule

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.skysam.hchirinos.go2shop.R
import com.skysam.hchirinos.go2shop.common.Constants
import com.skysam.hchirinos.go2shop.database.sharedPref.SharedPreferenceBD

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)

        val deleteShops: PreferenceScreen = findPreference("deleteShops")!!
        deleteShops.setOnPreferenceClickListener {
            val deleteShopsDialog = DeleteShopsDialog()
            deleteShopsDialog.show(requireActivity().supportFragmentManager, tag)
            true
        }

        val listTheme: ListPreference = findPreference("theme")!!
        when (SharedPreferenceBD.getTheme()) {
            Constants.PREFERENCE_THEME_SYSTEM -> listTheme.value = Constants.PREFERENCE_THEME_SYSTEM
            Constants.PREFERENCE_THEME_DARK -> listTheme.value = Constants.PREFERENCE_THEME_DARK
            Constants.PREFERENCE_THEME_LIGTH -> listTheme.value = Constants.PREFERENCE_THEME_LIGTH
        }

        listTheme.setOnPreferenceChangeListener { _, newValue ->
            var changeTheme = false
            when (val themeSelected = newValue as String) {
                Constants.PREFERENCE_THEME_SYSTEM -> {
                    if (themeSelected != SharedPreferenceBD.getTheme()) {
                        SharedPreferenceBD.saveTheme(Constants.PREFERENCE_THEME_SYSTEM)
                        changeTheme = true
                    }
                }
                Constants.PREFERENCE_THEME_DARK -> {
                    if (themeSelected != SharedPreferenceBD.getTheme()) {
                        SharedPreferenceBD.saveTheme(Constants.PREFERENCE_THEME_DARK)
                        changeTheme = true
                    }
                }
                Constants.PREFERENCE_THEME_LIGTH -> {
                    if (themeSelected != SharedPreferenceBD.getTheme()) {
                        SharedPreferenceBD.saveTheme(Constants.PREFERENCE_THEME_LIGTH)
                        changeTheme = true
                    }
                }
            }
            if (changeTheme) {
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                requireActivity().finish()
                requireActivity().startActivity(intent)
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            true
        }

        val aboutPreference: PreferenceScreen = findPreference("about")!!
        aboutPreference.setOnPreferenceClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_settingsFragment_to_aboutFragment)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().finish()
        }
        return super.onOptionsItemSelected(item)
    }
}