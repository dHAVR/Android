package com.dar_hav_projects.shop_list.entities.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.dar_hav_projects.shop_list.R
import com.dar_hav_projects.shop_list.databinding.NewListDialogBinding

object NewListDialog {

    fun showDialog(context: Context, listener: Listener, name: String){
        var dialog: AlertDialog ?= null
        val builder = AlertDialog.Builder(context)
        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            edNewListName.setText(name)
            if(name.isNotEmpty()){
                bCreateNewList.text = context.getString(R.string.edit_list_but)
            }
            bCreateNewList.setOnClickListener {
                val listName = edNewListName.text.toString()
                if(listName.isNotEmpty()){
                    listener.onClick(listName)
                }

                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.window?.attributes?.windowAnimations = android.R.anim.fade_in
        dialog.show()

    }

    interface Listener {
        fun onClick(name: String)
    }

}