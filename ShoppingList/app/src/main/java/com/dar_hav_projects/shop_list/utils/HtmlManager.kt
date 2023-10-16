package com.dar_hav_projects.shop_list.utils

import android.text.Html
import android.text.Spanned

object HtmlManager {

    //функція щоб діставати наш html код з бази данних
    //він повертає клас Spanned який editText розуміє
    // і форматує текст згідно того що нам потрібно було
    fun getFromHtml(text: String): Spanned {
        //робим перевірку версіх андроід
        // бо старий метод не працює на нових версыях андроїд
        return if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N){
        //для старої версії
            Html.fromHtml(text)
        }else{
            //для нової версії
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        }
    }

    //функція для зберігання в базі данних
    fun toHTML(text: Spanned):String{
        return if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N){
            //для старої версії
            Html.toHtml(text)
        }else{
            //для нової версії
            Html.toHtml(text, Html.FROM_HTML_MODE_COMPACT)
        }
    }

}