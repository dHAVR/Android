package com.dar_hav_projects.gpshelper.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dar_hav_projects.gpshelper.MainActivity
import com.dar_hav_projects.gpshelper.R
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import org.osmdroid.util.GeoPoint
import java.io.Serializable

class LocationService : Service() {
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locRequest: LocationRequest
    private var lastLocatoion: Location? = null
    private var distance = 0f
    private lateinit var geoPointsList: ArrayList<GeoPoint>

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    //якщо оперативна пам'ять заб'ється і почне вбивати процеси
    // то після того як вб'ють наш процес він перезапуститься тому що вибрана константа START_STICKY
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        startLocUpdates()
        isRunning = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        geoPointsList = ArrayList()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        locationProvider.removeLocationUpdates(locCallBack)
    }


    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChannel = NotificationChannel(
                CHANNEL_ID,
                "Location service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChannel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nIntent = Intent(this, MainActivity::class.java)
            val pIntent = PendingIntent.getActivity(
                this,
                1,
                nIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val notification = NotificationCompat.Builder(
                this,
                CHANNEL_ID
            ).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Tracker is running!")
                .setContentIntent(pIntent).build()
            startForeground(100, notification)
        }else{
            val nIntent = Intent(this, MainActivity::class.java)
            val pIntent = PendingIntent.getActivity(
                this,
                1,
                nIntent,
                0
            )
            val notification = NotificationCompat.Builder(
                this,
                CHANNEL_ID
            ).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Tracker is running!")
                .setContentIntent(pIntent).build()
            startForeground(100, notification)
        }
    }


    private fun initLocation() {
        locationProvider = LocationServices.getFusedLocationProviderClient(baseContext)
        locRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 5000).build()
        locRequest.fastestInterval = 5000
    }

    private val locCallBack = object : LocationCallback() {
        override fun onLocationResult(locResult: LocationResult) {
            super.onLocationResult(locResult)
            val currentLocation = locResult.lastLocation
            if (lastLocatoion != null && currentLocation != null) {
                    distance += sumDistance(lastLocatoion, currentLocation)
                geoPointsList.add(GeoPoint(currentLocation.latitude, currentLocation.longitude))
                val locModel = LocationModel(
                    currentLocation.speed,
                    distance,
                    geoPointsList
                )
                sentLocData(locModel)
            }
            lastLocatoion = locResult.lastLocation



        }
    }

    private fun sumDistance(lastLocatoion: Location?, currentLocation: Location?): Float{
        when(wayOfMoving){
            1->{
               if(currentLocation?.speed!! > 0.2 && currentLocation?.speed!! < 0.5){
                    distance += (lastLocatoion?.distanceTo(currentLocation!!)!!)
                }
            }
            2->{
                if(currentLocation?.speed!! > 0.6 && currentLocation?.speed!! < 0.9){
                    distance += (lastLocatoion?.distanceTo(currentLocation)!!)
                }
            }
            3->{
                if(currentLocation?.speed!! > 1 && currentLocation?.speed!! < 3){
                    distance += (lastLocatoion?.distanceTo(currentLocation)!!)
                }
            }
            else->{
                if(currentLocation?.speed!! > 0.2 && currentLocation?.speed!! < 0.6 ){
                    distance += (lastLocatoion?.distanceTo(currentLocation)!!)
                }
            }
        }
        return distance
    }

    private fun sentLocData(locModel: LocationModel){
        val intent = Intent(LOC_MODEL_INTENT)
        intent.putExtra(LOC_MODEL_INTENT,locModel)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun startLocUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationProvider.requestLocationUpdates(
            locRequest,
            locCallBack,
            Looper.myLooper()

        )

    }


    companion object {
        const val CHANNEL_ID = "channel_1"
        var isRunning = false
        var startTime = 0L
        var wayOfMoving = 0
        const val LOC_MODEL_INTENT = "intent"
    }


}