package com.dar_hav_projects.shop_list.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

import com.dar_hav_projects.shop_list.billing.BilingManager
import com.dar_hav_projects.shop_list.R

class SettingsFragment: PreferenceFragmentCompat() {
    private lateinit var removeaAdsPref: Preference
    private lateinit var bManager: BilingManager
    private lateinit var themePref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        init()
    }

    private fun onchangeListener(): Preference.OnPreferenceChangeListener {
        return Preference.OnPreferenceChangeListener() { pref, value ->
            when (pref.key) {
                "theme_key" -> {
                    activity?.recreate()
                }

            }

            true
        }
    }

    private fun init(){
        bManager  = BilingManager(activity as AppCompatActivity)
        removeaAdsPref = findPreference("remove_ads_key")!!
        removeaAdsPref.setOnPreferenceClickListener {
        bManager.startConection()
            true
        }
        themePref = findPreference("theme_key")!!
        themePref.onPreferenceChangeListener = onchangeListener()

    }

    override fun onDestroy() {
        bManager.closeConection()
        super.onDestroy()
    }
}