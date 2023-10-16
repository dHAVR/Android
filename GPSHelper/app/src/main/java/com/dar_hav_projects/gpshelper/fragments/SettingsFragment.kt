package com.dar_hav_projects.gpshelper.fragments


import android.content.Intent
import android.content.res.Resources.Theme
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.dar_hav_projects.gpshelper.MainActivity
import com.dar_hav_projects.gpshelper.R
import com.dar_hav_projects.gpshelper.utils.openFragment


class SettingsFragment : PreferenceFragmentCompat(){
    private lateinit var timePref: Preference
    private lateinit var themePref: Preference
    private lateinit var colorPref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference, rootKey)
        init()
    }

  private fun init(){
      timePref = findPreference("update_time_key")!!
      themePref= findPreference("theme_choose_key")!!
      colorPref = findPreference("routes_color_key")!!
      timePref.onPreferenceChangeListener = onchangeListener()
      themePref.onPreferenceChangeListener = onchangeListener()
      colorPref.onPreferenceChangeListener = onchangeListener()
      initPrefs()
  }

    private fun onchangeListener():OnPreferenceChangeListener{
        return OnPreferenceChangeListener(){
            pref, value ->
            when(pref.key){
                "update_time_key" -> {
                    setChangesForPreferences(pref, value)
                }

                "theme_choose_key" ->{
                    setChangesForPreferences(pref, value)
                    if (value == "Dark"){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }

                "routes_color_key"->{
                    setChangesForPreferences(pref, value)
                }
            }
           true
        }
    }

    private fun initPrefs(){
        val prefValueTime = timePref.preferenceManager.sharedPreferences?.getString("update_time_key", "3000")
        val nameArrayTime = resources.getStringArray(R.array.loc_time_update_name)
        val valueArrayTime = resources.getStringArray(R.array.loc_time_update_values)
        timePref.title= "${timePref.title}: ${nameArrayTime[valueArrayTime.indexOf(prefValueTime)]}"

        val prefValueTheme = themePref.preferenceManager.sharedPreferences?.getString("theme_choose_key", "Dark")
        val nameArrayTheme = resources.getStringArray(R.array.theme_choose_name)
        val valueArrayTheme = resources.getStringArray(R.array.theme_choose_values)
        themePref.title= "${themePref.title}: ${nameArrayTheme[valueArrayTheme.indexOf(prefValueTheme)]}"

        val prefValueColor = themePref.preferenceManager.sharedPreferences?.getString("routes_color_key", "#8039D1")
        val nameArrayColor = resources.getStringArray(R.array.routes_color_name)
        val valueArrayColor = resources.getStringArray(R.array.routes_color_values)
        colorPref.title= "${colorPref.title}: ${nameArrayColor[valueArrayColor.indexOf(prefValueColor)]}"
        colorPref.icon?.setTint(Color.parseColor(prefValueColor))

    }

    private fun setChangesForPreferences(pref: Preference, value: Any){
        when(pref.key){
            "update_time_key"->{
                val nameArray = resources.getStringArray(R.array.loc_time_update_name)
                val valueArray = resources.getStringArray(R.array.loc_time_update_values)
                val title = pref.title.toString().substringBefore(":")
                pref.title = "$title: ${nameArray[valueArray.indexOf(value)]}"
            }
            "theme_choose_key" ->{
                val nameArray = resources.getStringArray(R.array.theme_choose_name)
                val valueArray = resources.getStringArray(R.array.theme_choose_values)
                val title = pref.title.toString().substringBefore(":")
                pref.title = "$title: ${nameArray[valueArray.indexOf(value)]}"
            }
            "routes_color_key"->{
                val nameArray = resources.getStringArray(R.array.routes_color_name)
                val valueArray = resources.getStringArray(R.array.routes_color_values)
                val title = pref.title.toString().substringBefore(":")
                pref.title = "$title: ${nameArray[valueArray.indexOf(value)]}"
                pref.icon?.setTint(Color.parseColor(value.toString()))
            }
        }

    }

}