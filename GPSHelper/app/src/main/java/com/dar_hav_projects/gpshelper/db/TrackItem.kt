package com.dar_hav_projects.gpshelper.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track")
data class TrackItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "data")
    val date: String,
    @ColumnInfo(name = "distance")
    val distance: String,
    @ColumnInfo(name = "avg_speed")
    val avg_speed: String,
    @ColumnInfo(name = "geo_points")
    val geo_points: String
    )
