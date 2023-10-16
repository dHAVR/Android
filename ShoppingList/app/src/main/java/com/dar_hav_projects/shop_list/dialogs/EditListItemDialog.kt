package com.dar_hav_projects.shop_list.entities.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.dar_hav_projects.shop_list.R
import com.dar_hav_projects.shop_list.databinding.EditListItemDialogBinding
import com.dar_hav_projects.shop_list.entities.ShopListItem

object EditListItemDialog {

    fun showDialog(context: Context, item: ShopListItem, listener: Listener){
        var dialog: AlertDialog ?= null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            edName.setText(item.name)
            edInfo.setText(item.info)
            if(item.item_type == 1){
                tvTitle.text = context.getString(R.string.update_library_item)
                edInfo.visibility = View.GONE
            } else{
                tvTitle.text = context.getString(R.string.update_shop_item)
            }
            bUpdate.setOnClickListener {
                if(edName.text.toString().isNotEmpty()){
                    val itemInfo = if(edInfo.text.toString().isEmpty())
                        null
                    else
                        edInfo.text.toString()
                    listener.onClick(item.copy(name = edName.text.toString(), info = itemInfo))
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
        fun onClick(item: ShopListItem)
    }

}