package com.dar_hav_projects.gpshelper

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.dar_hav_projects.gpshelper.databinding.ActivityMainBinding
import com.dar_hav_projects.gpshelper.fragments.MainFragment
import com.dar_hav_projects.gpshelper.fragments.TracksFragment
import com.dar_hav_projects.gpshelper.fragments.SettingsFragment
import com.dar_hav_projects.gpshelper.utils.openFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var defPref: SharedPreferences
    private var currenTheme = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        currenTheme = defPref.getString("theme_choose_key", "Dark").toString()
        selectedTheme()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBottomMenuListener()
        openFragment(MainFragment.newInstance())

    }
    override fun onResume() {
        super.onResume()
        binding.bNav.selectedItemId =  R.id.id_home_but_menu
    }


    private fun onBottomMenuListener(){

        binding.bNav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.id_home_but_menu ->{
                    openFragment(MainFragment.newInstance())
                }
                R.id.id_settings_but_menu ->{
                    openFragment(SettingsFragment())
                }
                R.id.id_routs_but_menu ->{
                    openFragment(TracksFragment.newInstance())
                }
            }
            true
        }
    }

    private fun selectedTheme(){
        if (defPref.getString("theme_choose_key", "Dark") == "Dark"){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }



}