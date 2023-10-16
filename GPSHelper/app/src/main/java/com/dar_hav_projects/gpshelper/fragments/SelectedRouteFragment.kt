package com.dar_hav_projects.gpshelper.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.dar_hav_projects.gpshelper.MainApp
import com.dar_hav_projects.gpshelper.MainViewModel
import com.dar_hav_projects.gpshelper.R
import com.dar_hav_projects.gpshelper.databinding.FragmentSelectedRouteBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


class SelectedRouteFragment : Fragment() {
    private lateinit var binding: FragmentSelectedRouteBinding
    private var startPoint: GeoPoint? = null

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        settingsOsm()
        binding = FragmentSelectedRouteBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        listener()
    }

    private fun listener()=with(binding){
        fCenter.setOnClickListener {
           if(startPoint != null) {
               binding.map.controller.animateTo(startPoint)
           }
        }
    }

    private fun settingsOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }



    private fun observer()= with(binding){
        mainViewModel.currentTrack.observe(viewLifecycleOwner){
            val distancePatern = context?.getString(R.string.distance_patern) + "${it?.distance}"+" "+context?.getString(R.string.meter_patern)
            val avgSpeedPatern = context?.getString(R.string.averag_speed_patern) +"${it?.avg_speed}"+" "+ context?.getString(R.string.km_h_patern)
            val timePatern = context?.getString(R.string.time_patern) + it.time
            val datePatern = context?.getString(R.string.date_patern) + it.date

            tvData.text = datePatern
            tvAverageSpeed.text = avgSpeedPatern
            tvTime.text = timePatern
            tvDistance.text = distancePatern
            val polyline = getPolyLine(it.geo_points)
            map.overlays.clear()
            map.overlays.add(polyline)
            setMarkers(polyline.actualPoints)
            startPoint = polyline.actualPoints[0]
            goToStartPosition(polyline.actualPoints[0])
        }
    }

    private fun goToStartPosition(startPosition: GeoPoint){
        binding.map.controller.zoomTo(16.0)
        binding.map.controller.animateTo(startPosition)
    }

    private fun getPolyLine(geoPoints: String): Polyline{
        val polyLine = Polyline()
        polyLine.outlinePaint.color = Color.parseColor(
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("routes_color_key", "#8039D1")
        )
        val list = geoPoints.split("/")
        list.forEach {
            if (it.isEmpty()){
                return@forEach
            }
            val poins = it.split(",")
            polyLine.addPoint(GeoPoint(poins[0].toDouble(), poins[1].toDouble()))
        }
        return polyLine
    }

    private fun setMarkers(list: List<GeoPoint>) = with(binding){
        val startMarker = Marker(map)
        val finishMarker = Marker(map)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        finishMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = getDrawable(requireContext(), R.drawable.ic_start_position)
        finishMarker.icon = getDrawable(requireContext(), R.drawable.ic_finish_position)
        startMarker.position= list[0]
        finishMarker.position= list[list.size-1]
        map.overlays.add(startMarker)
        map.overlays.add(finishMarker)
    }


    companion object {
        @JvmStatic
        fun newInstance() = SelectedRouteFragment()
            }

}