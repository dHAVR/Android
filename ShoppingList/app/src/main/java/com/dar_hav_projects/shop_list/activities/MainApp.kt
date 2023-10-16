package com.dar_hav_projects.shop_list.activities

import android.app.Application
import com.dar_hav_projects.shop_list.db.MainDataBase

class MainApp : Application() {
    val dataBase by lazy {
        MainDataBase.getDataBase(this)
    }
}