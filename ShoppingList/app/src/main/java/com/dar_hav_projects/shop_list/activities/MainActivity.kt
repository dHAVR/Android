package com.dar_hav_projects.shop_list.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.dar_hav_projects.shop_list.billing.BilingManager
import com.dar_hav_projects.shop_list.entities.dialogs.NewListDialog
import com.dar_hav_projects.shop_list.fragments.FragmentManager
import com.dar_hav_projects.shop_list.fragments.NoteFragment
import com.dar_hav_projects.shop_list.fragments.ShopListNamesFragment
import com.dar_hav_projects.shop_list.settings.SettingsActivity
import com.dar_hav_projects.shop_list.R
import com.dar_hav_projects.shop_list.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    lateinit var binding: ActivityMainBinding
    private var currentMenuItemId = R.id.notes
    lateinit private var defPref: SharedPreferences
    lateinit private var pref: SharedPreferences
    private var currenTheme = ""
    private var iAd: InterstitialAd? = null
    private var adShowCounter = 0
    private var adShowCounterMax = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        currenTheme = defPref.getString("theme_key", "Green").toString()
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(BilingManager.MAIN_PREF, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.notes)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FragmentManager.setFragment(NoteFragment.newInstance(), this)
        setBottomNavListener()
        if(!pref.getBoolean(BilingManager.REMOVE_ADS_KEY, false)){
            loadInterAd()
        }

    }

   private fun loadInterAd(){
       val request = AdRequest.Builder().build()
       InterstitialAd.load(this, getString(R.string.inter_ad_id), request,
           object : InterstitialAdLoadCallback(){
               override fun onAdLoaded(ad: InterstitialAd) {
                   Log.d("MyLog1", "OK")
                   iAd = ad
               }

               override fun onAdFailedToLoad(p0: LoadAdError) {
                   Log.d("MyLog1", "NOT OK")
                   iAd = null
               }
       })
   }

    private fun showInterAd(adListener: Adlistener) {
        if (iAd != null && adShowCounter > adShowCounterMax ) {
            iAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("MyLog1", "onAdDismissedFullScreenContent")
                    iAd = null
                    loadInterAd()
                    adListener.onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    Log.d("MyLog1", "onAdFailedToShowFullScreenContent")
                    iAd = null
                    loadInterAd()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("MyLog1", "onAdShowedFullScreenContent")
                    iAd = null
                    loadInterAd()
                }
            }
            adShowCounter = 0
            iAd?.show(this)
        } else {
            Log.d("MyLog1", "error")
            adShowCounter ++
            adListener.onFinish()
        }
    }

    private fun setBottomNavListener() {
        binding.BottomNav.selectedItemId =  R.id.notes
        binding.BottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    adShowCounter ++
                    showInterAd(object : Adlistener{
                        override fun onFinish() {
                            startActivity(
                                Intent(this@MainActivity, SettingsActivity::class.java )
                            )
                        }
                    })

                }
                R.id.notes -> {
                    supportActionBar?.title = getString(R.string.notes)
                    currentMenuItemId = R.id.notes
                    FragmentManager.setFragment(NoteFragment.newInstance(), this)
                }
                R.id.shopList -> {
                    currentMenuItemId = R.id.shopList
                    supportActionBar?.title = getString(R.string.shopping_lists)
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
                }
                R.id.newItem -> {
                    FragmentManager.currentFrag?.onClickNew()
                }
            }
            true
        }

    }

    private fun getSelectedTheme():Int{
        return if(defPref.getString("theme_key", "Green") == "Green"){
            R.style.Theme_ShoppingListGreen
        } else{
            R.style.Theme_ShoppingListBlue
        }
    }

    override fun onResume() {
        super.onResume()
        binding.BottomNav.selectedItemId = currentMenuItemId
        if(defPref.getString("theme_key", "Green") != currenTheme) {
            recreate()
        }
    }

    override fun onClick(name: String) {

    }

    interface Adlistener{
        fun onFinish()
    }
}