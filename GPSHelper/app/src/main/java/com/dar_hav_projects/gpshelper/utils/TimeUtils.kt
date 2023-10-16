package com.dar_hav_projects.gpshelper.utils

import android.annotation.SuppressLint

import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object TimeUtils {
    private val timeFormat = SimpleDateFormat("HH:mm:ss:SSS")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")

    fun getTime(timeInMillis: Long): String{
       val calendar = Calendar.getInstance()
        timeFormat.timeZone = TimeZone.getTimeZone("UTC")
        calendar.timeInMillis = timeInMillis
       return timeFormat.format(calendar.time)
    }

     fun getDate(): String{
        val cv = Calendar.getInstance()
        return dateFormat.format(cv.time)
    }
}