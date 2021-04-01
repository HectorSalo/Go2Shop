package com.skysam.hchirinos.go2shop.settingsModule

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.skysam.hchirinos.go2shop.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)

        val aboutPreference: PreferenceScreen = findPreference("about")!!
        aboutPreference.setOnPreferenceClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_settingsFragment_to_aboutFragment)
            true
        }
    }
}