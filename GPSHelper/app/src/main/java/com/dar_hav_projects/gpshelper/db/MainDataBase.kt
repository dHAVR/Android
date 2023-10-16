package com.dar_hav_projects.gpshelper.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackItem::class], version = 1)
abstract class MainDataBase: RoomDatabase() {
    abstract fun getDao(): Dao

    companion object{
         @Volatile
        private var INSATNCE: MainDataBase? = null
        fun getDataBase(contex: Context): MainDataBase{
            return INSATNCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    contex.applicationContext,
                    MainDataBase::class.java,
                    "gps_tracker.db"
                ).build()
                INSATNCE = instance
                return instance
            }
        }
    }
}