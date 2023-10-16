package com.dar_hav_projects.gpshelper

import android.app.Application
import com.dar_hav_projects.gpshelper.db.MainDataBase

class MainApp: Application() {
    val database by lazy {
        MainDataBase.getDataBase(this)
     }
}