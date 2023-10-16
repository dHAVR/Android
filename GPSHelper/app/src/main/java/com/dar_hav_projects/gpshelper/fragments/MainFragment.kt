package com.dar_hav_projects.gpshelper.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dar_hav_projects.gpshelper.databinding.FragmentMainBinding
import com.dar_hav_projects.gpshelper.utils.checkPermission
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.view.View.OnClickListener
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.dar_hav_projects.gpshelper.MainApp
import com.dar_hav_projects.gpshelper.MainViewModel
import com.dar_hav_projects.gpshelper.R
import com.dar_hav_projects.gpshelper.db.TrackItem
import com.dar_hav_projects.gpshelper.location.LocationModel
import com.dar_hav_projects.gpshelper.location.LocationService
import com.dar_hav_projects.gpshelper.utils.DialogManager
import com.dar_hav_projects.gpshelper.utils.TimeUtils
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Timer
import java.util.TimerTask

class MainFragment : Fragment() {
    private  var stingGeoPoints = StringBuilder()
    private lateinit var mLocOverLay: MyLocationNewOverlay
    private var trackItem: TrackItem? = null
    private var pLine: Polyline? = null
    private lateinit var defPref: SharedPreferences
    private var timer: Timer? = null
    private var startTime = 0L
    private var isServiceRunning = false
    private var isFirstStart = true
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        defPref = PreferenceManager.getDefaultSharedPreferences(activity as AppCompatActivity)
        settingsOsm()
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClicksListener()
        checkServiceState()
        updateTxtTime()
        updateLocation()
        registerLocBroadcast()

    }


    private fun setOnClicksListener() = with(binding) {
        val listener = onClickslistener()
        fMyPosition.setOnClickListener(listener)
        fStartStop.setOnClickListener(listener)
    }

    private fun onClickslistener(): OnClickListener {
        return OnClickListener {
            when (it.id) {
                R.id.fStartStop -> {
                    checkStartStopButtons()
                }
                R.id.fMyPosition->{
                    centerLocation()
                }
            }
        }
    }

    private fun centerLocation(){
        binding.map.controller.animateTo(mLocOverLay.myLocation)
        mLocOverLay.enableFollowLocation()
    }

    private fun updateTxtTime() = with(binding){
        mainViewModel.timeData.observe(viewLifecycleOwner){
            tvTime.text = it
        }
    }
    private fun updateLocation()= with(binding){
        mainViewModel.locLiveData.observe(viewLifecycleOwner){
            val distance_patern = activity?.getString(R.string.distance_patern) + String.format("%.1f", it.distance)+" " + activity?.getString(R.string.meter_patern)
            val speed_patern = activity?.getString(R.string.speed_patern) + String.format("%.1f", 3.6f * it.speed)+" "+ activity?.getString(R.string.km_h_patern)
            val aver_speed_patern = activity?.getString(R.string.averag_speed_patern) + getAverageSpeed(it.distance)+" " + activity?.getString(R.string.km_h_patern)
            tvDistance.text = distance_patern
            tvSpeed.text = speed_patern
            tvAverageSpeed.text =  aver_speed_patern
            createPolyLine(it.geoPointsList)
            trackItem = TrackItem(
                null,
                getCurrentTime(),
                TimeUtils.getDate(),
                String.format("%.1f", it.distance),
                getAverageSpeed(it.distance),
                stingGeoPoints.toString()
            )
            updatePolyLine(it.geoPointsList)
        }
    }
    private fun getAverageSpeed(distance: Float): String{
        return String.format("%.1f", 3.6f * (distance / ((System.currentTimeMillis() - startTime) / 1000.0f)))

    }

    private fun createPolyLine(geoPointsList: ArrayList<GeoPoint>){
        stingGeoPoints.clear()
        geoPointsList.forEach{
            stingGeoPoints.append("${it.latitude},${it.longitude}"+"/")
        }
    }

    private fun startTimer(){
        val time_patern = activity?.getString(R.string.time_patern)
        timer?.cancel()
        timer = Timer()
        startTime = LocationService.startTime
        timer?.schedule(object : TimerTask() {
            override fun run() {
//            так як ця функці запускається на другорядному потоці, а обсервер на основному,
//            то потрбіно запустити це в функції runOnUiThread яка передає це на основний потік
                activity?.runOnUiThread {
                    mainViewModel.timeData.value = time_patern +getCurrentTime()
                }

            }
        }, 1, 1)

    }

    private fun getCurrentTime(): String{
        return TimeUtils.getTime(System.currentTimeMillis() - startTime)
    }

    private fun checkServiceState(){
        isServiceRunning = LocationService.isRunning
        if(isServiceRunning){
            binding.fStartStop.setImageResource(R.drawable.ic_stop)
            startTimer()
        }
    }

    private fun checkStartStopButtons() {
        if (!isServiceRunning) {
            DialogManager.showStartDialog(activity as AppCompatActivity, object: DialogManager.Listener{
                override fun OnClick() {
                    startLocationService()
                    binding.fStartStop.setImageResource(R.drawable.ic_stop)
                    isServiceRunning = !isServiceRunning
                }
            })
        } else {
            activity?.stopService(Intent(activity, LocationService::class.java))
            timer?.cancel()
            binding.fStartStop.setImageResource(R.drawable.ic_start)
            DialogManager.showSaveDialog(requireContext(),trackItem, object : DialogManager.Listener{
                override fun OnClick() {
                    trackItem?.let { mainViewModel.insertTrack(it) }
                }
            })
            isServiceRunning = !isServiceRunning
        }

    }



    private fun startLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        } else {
            activity?.startService(Intent(activity, LocationService::class.java))
        }
        LocationService.startTime = System.currentTimeMillis()
        startTimer()

    }


    override fun onResume() {
        super.onResume()
            checkLocPermission()
    }

    private fun settingsOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initOSM() = with(binding) {
        pLine = Polyline()
        pLine?.outlinePaint?.color = Color.parseColor(
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("routes_color_key", "#8039D1")
        )
        map.controller.setZoom(20.0)
        val mLocProvider = GpsMyLocationProvider(activity)
        mLocOverLay = MyLocationNewOverlay(mLocProvider, map)
        mLocOverLay.enableMyLocation()
        mLocOverLay.enableFollowLocation()
        //як тільки отримали міце розташування то запускається ця функція
        mLocOverLay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(mLocOverLay)
            map.overlays.add(pLine)
        }
    }

    private fun registerPermissions() {

        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission())
        { permission ->
            if (permission == true) {
                Log.d("MyLog1", "registerPermissions() if")
                //DialogStartTrack.showDialog(activity as AppCompatActivity)
                initOSM()
                checkLocationEnabled()
            } else {
                Log.d("MyLog1", "registerPermissions() else")
                Toast.makeText(
                    activity,
                    "You can't use this application without this permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkLocPermission() {
        Log.d("MyLog1", "Version: ${Build.VERSION.SDK_INT}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //якщо версія андроїд вище або = 10 то запускаєься цей код
            Log.d("MyLog1", "checkLocPermission  checkPermissionAfter10()")
            checkPermissionAfter10()
        } else {
            Log.d("MyLog1", "checkLocPermission  checkPermissionBefore10()")
            checkPermissionBefore10()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionAfter10() {
        Log.d("MyLog1", "checkPermissionAfter10")
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            Log.d("MyLog1", "checkPermissionAfter10 if")
            initOSM()
            checkLocationEnabled()
        } else {
            Log.d("MyLog1", "checkPermissionAfter10 else")
            pLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
            pLauncher.launch(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }


    private fun checkPermissionBefore10() {
        Log.d("MyLog1", "checkPermissionBefore10")
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initOSM()
            checkLocationEnabled()
        } else {
            pLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            )

        }
    }

    private fun checkLocationEnabled() {
        Log.d("MyLog1", "checkLocationEnabled")
        val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabled) {
            DialogManager.showLocEnableDialog(
                activity as AppCompatActivity,
                object : DialogManager.Listener {
                    override fun OnClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                })
            Toast.makeText(activity, "QPS is not enabled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "QPS is enabled", Toast.LENGTH_SHORT).show()
        }
    }

    private val reciver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, i: Intent?) {
            if(i?.action == LocationService.LOC_MODEL_INTENT){
               when{
                   Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                       val locModel = i.getSerializableExtra(LocationService.LOC_MODEL_INTENT, LocationModel::class.java)
                       mainViewModel.locLiveData.value = locModel
                   }
                   else -> {
                       @Suppress("DEPRECATION")
                       val locModel = i.getSerializableExtra(LocationService.LOC_MODEL_INTENT)
                               as LocationModel
                       mainViewModel.locLiveData.value = locModel
                   }
               }
            }
        }
    }

    fun registerLocBroadcast(){
        val locFilter = IntentFilter(LocationService.LOC_MODEL_INTENT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity).registerReceiver(reciver, locFilter)
    }

    private fun addPoint(list: List<GeoPoint>){
        pLine?.addPoint(list[list.size-1])
    }

    private fun fillAllPoints(list: List<GeoPoint>){
        list.forEach {
            pLine?.addPoint(it)
        }
    }

    private fun updatePolyLine(list: List<GeoPoint>) {
            if (list.size > 1 && isFirstStart) {
                fillAllPoints(list)
                isFirstStart = false
            } else {
                addPoint(list)
            }
    }


    override fun onDetach() {
        super.onDetach()
       LocalBroadcastManager.getInstance(activity as AppCompatActivity).unregisterReceiver(reciver)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()

    }


}