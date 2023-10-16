package com.dar_hav_projects.shop_list.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object TimeManager {
 const val DEF_TIME_FORMAT = "hh:mm:ss - yyyy/MM/dd"
    //функція для отримання часу з компютера
     fun getCurrentTime(): String {
        //ми вкахуємо який формат хочемо отрмати
        val formatеer = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault());
        return formatеer.format(Calendar.getInstance().time)
    }

    fun getTimeFormat(time: String, defPreferences: SharedPreferences): String? {
        val defFormater = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault())
        val defDate = defFormater.parse(time)
        val newFormat = defPreferences.getString("time_format_key", DEF_TIME_FORMAT)
        val newFormater = SimpleDateFormat(newFormat, Locale.getDefault())
        return if(defDate != null){
            newFormater?.format(defDate)
        }else{
            time
        }
    }
}