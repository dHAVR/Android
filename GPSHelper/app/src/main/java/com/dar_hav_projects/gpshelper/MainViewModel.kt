package com.dar_hav_projects.gpshelper

import androidx.lifecycle.*
import com.dar_hav_projects.gpshelper.db.MainDataBase
import com.dar_hav_projects.gpshelper.db.TrackItem
import com.dar_hav_projects.gpshelper.location.LocationModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class MainViewModel(database: MainDataBase): ViewModel() {
    val dao = database.getDao()
    val locLiveData = MutableLiveData<LocationModel>()
    val currentTrack = MutableLiveData<TrackItem>()
    val timeData = MutableLiveData<String>()
    val tracks = dao.getAllTracks().asLiveData()

    fun insertTrack(trackItem: TrackItem)= viewModelScope.launch {
        dao.insertTrack(trackItem)
    }
    fun deleteTrack(trackItem: TrackItem)= viewModelScope.launch {
        dao.deleteTrack(trackItem)
    }

    class ViewModelFactory(private val database: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}