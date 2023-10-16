package com.dar_hav_projects.shop_list.fragments

import androidx.appcompat.app.AppCompatActivity
import com.dar_hav_projects.shop_list.R
// потрібно для того щоб визнчати який фрагмент зараз працює
object FragmentManager {
    var currentFrag: BaseFragment? = null

    fun setFragment(newFrag: BaseFragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeHolder, newFrag)
        transaction.commit()
        currentFrag = newFrag
    }
}