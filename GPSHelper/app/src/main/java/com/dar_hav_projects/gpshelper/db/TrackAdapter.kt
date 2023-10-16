package com.dar_hav_projects.gpshelper.db

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dar_hav_projects.gpshelper.R
import com.dar_hav_projects.gpshelper.databinding.TrackItemBinding

class TrackAdapter(val listener: Listener): ListAdapter<TrackItem, TrackAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return ItemHolder(view, listener ,parent.context)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
       holder.setData(getItem(position))
    }

    class ItemHolder(view: View,private val listener: Listener,private val context: Context): RecyclerView.ViewHolder(view), OnClickListener{
        private val binding = TrackItemBinding.bind(view)
        private var trackTemp:TrackItem? = null

        init {
           binding.imDelete.setOnClickListener(this)
           binding.cardView.setOnClickListener(this)
        }
       fun setData(trackItem: TrackItem) = with(binding){
           trackTemp = trackItem
           val avgSpeedPattern =trackItem.avg_speed +" "+context.getString(R.string.km_h_patern)
           val distancePattern = trackItem.distance +" "+ context.getString(R.string.meter_patern)
           val timePattern = trackItem.time
           tvDate.text = trackItem.date
           tvSpeed.text =avgSpeedPattern
           tvDistance.text = distancePattern
           tvTime.text = timePattern
       }

        override fun onClick(view: View?) {
            val type = when(view?.id){
                 R.id.imDelete -> ClickType.DELETE
                 R.id.cardView -> ClickType.OPEN
                else -> ClickType.OPEN
            }
            trackTemp?.let {
                listener.onClick(it, type)
            }
        }
    }

    class  ItemComparator: DiffUtil.ItemCallback<TrackItem>() {
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener{
        fun onClick(trackItem: TrackItem,type: ClickType)
    }

    enum class ClickType{
        DELETE, OPEN
    }



}