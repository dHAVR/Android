package com.dar_hav_projects.shop_list.entities.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.dar_hav_projects.shop_list.databinding.DeleteDialogBinding

object DeleteDialog {

    fun showDialog(context: Context, listener: Listener) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            bDelete.setOnClickListener {
                listener.onClick()

                dialog?.dismiss()
            }
            bCancel.setOnClickListener {

                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.window?.attributes?.windowAnimations = android.R.anim.fade_in
        dialog.show()

    }

    interface Listener {
        fun onClick()
    }

}