package com.honey_bear.honeybear_matchmaker.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.honey_bear.honeybear_matchmaker.data.model.Gps
import com.honey_bear.honeybear_matchmaker.data.model.UserLocation
import com.honey_bear.honeybear_matchmaker.data.service.UserLocationRepository
import com.honey_bear.honeybear_matchmaker.view.activity.MainActivity

class UserLocationViewModel() : ViewModel() {
    private val _currentUserId:MutableLiveData<String> = MutableLiveData()

    val userLocationData : LiveData<ArrayList<UserLocation>> = UserLocationRepository.getUserLocations()
    val currentUserLocationData : LiveData<UserLocation> =Transformations.switchMap(_currentUserId){
        UserLocationRepository.getCurrentUsersLocation(it)
    }
    val nearUserLocations : LiveData<ArrayList<UserLocation>> = Transformations.switchMap(_currentUserId) {
        UserLocationRepository.getNearUserLocations(it)
    }


    fun cancelJobs(){
        UserLocationRepository.cancelJobs()
    }

    fun setUserLocation(context: Context, gps: Gps?){
        _currentUserId.value?.let {
            UserLocationRepository.setUserLocation(context,gps,it)
        }
    }

    fun setCurrentUserId(currentUserId:String){
        if(_currentUserId.value == currentUserId){
            return
        }
        _currentUserId.value = currentUserId
    }
}

