package com.dar_hav_projects.shop_list.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.dar_hav_projects.shop_list.R


class SettingsActivity : AppCompatActivity() {
    lateinit private var defPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = getString(R.string.settings)
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.placeHolder, SettingsFragment()).commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun getSelectedTheme():Int{
        return if(defPref.getString("theme_key", "Green") == "Green"){
            R.style.Theme_ShoppingListGreen
        } else{
            R.style.Theme_ShoppingListBlue
        }
    }
}