package com.dar_hav_projects.gpshelper.location

import org.osmdroid.util.GeoPoint
import java.io.Serializable

data class LocationModel(
    val speed: Float =0.0f,
    val distance: Float =0.0f,
    val geoPointsList: ArrayList<GeoPoint>
) : Serializable
