package com.dar_hav_projects.shop_list.utils

import android.content.Intent
import com.dar_hav_projects.shop_list.entities.ShopListItem

object ShareHelper {
    fun shareShopList(shopList: List<ShopListItem>, listName: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plane"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT,makeShareText(shopList, listName))
        }
        return intent
    }

    private fun makeShareText(shopList: List<ShopListItem>, listName: String): String{
        val sBuilder = StringBuilder()
        sBuilder.append("<<$listName>>")
        sBuilder.append("\n")
        var counter = 1
        shopList.forEach {
            sBuilder.append("${counter++} - ${it.name}")
            if(it.info != null ) {
                sBuilder.append(" (${it.info})")
            }
            sBuilder.append("\n")
        }
        return sBuilder.toString()
    }
}