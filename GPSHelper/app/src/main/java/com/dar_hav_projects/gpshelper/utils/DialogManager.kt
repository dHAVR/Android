package com.dar_hav_projects.gpshelper.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import com.dar_hav_projects.gpshelper.R
import com.dar_hav_projects.gpshelper.databinding.DialogSaveTrackBinding
import com.dar_hav_projects.gpshelper.databinding.DialogStartTrackBinding
import com.dar_hav_projects.gpshelper.db.TrackItem
import com.dar_hav_projects.gpshelper.location.LocationService

object DialogManager {
    fun showLocEnableDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.location_disabled)
        dialog.setMessage(context.getString(R.string.location_dialog_message))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.location_dialog_button_pos)){
            _, _ ->
            listener.OnClick()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.location_dialog_button_neg)){
            _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showStartDialog(context: Context, listener: Listener){
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = DialogStartTrackBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            fButtWalking.setOnClickListener {
                LocationService.wayOfMoving = 1
                listener.OnClick()
                dialog?.dismiss()
            }
            fButtCyclist.setOnClickListener {
                LocationService.wayOfMoving = 2
                listener.OnClick()
                dialog?.dismiss()
            }
            fButtCar.setOnClickListener {
                LocationService.wayOfMoving = 3
                listener.OnClick()
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = android.R.anim.fade_in
        dialog.show()

    }

    fun showSaveDialog(context: Context, item: TrackItem?,  listener: Listener) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = DialogSaveTrackBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        binding.apply {
            val speed = context.getString(R.string.averag_speed_patern) +"${item?.avg_speed}"+" "+ context?.getString(R.string.km_h_patern)
            val distance = context.getString(R.string.distance_patern) + "${item?.distance}"+" "+context?.getString(R.string.meter_patern)
            val time = context.getString(R.string.time_patern) + item?.time
            tvTime.text = time
            tvSpeed.text = speed
            tvDistance.text = distance

            bSave.setOnClickListener {
                listener.OnClick()
                dialog?.dismiss()
            }
            bCancel.setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = android.R.anim.fade_in
        dialog.show()

    }

    interface Listener{
        fun OnClick()
    }
}