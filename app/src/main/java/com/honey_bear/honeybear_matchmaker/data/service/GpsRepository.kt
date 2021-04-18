package com.honey_bear.honeybear_matchmaker.data.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.honey_bear.honeybear_matchmaker.data.model.Gps
import kotlinx.coroutines.*

object GpsRepository : LocationListener{
    var job:CompletableJob? = null
    private lateinit var locationManager: LocationManager
    private lateinit var location : Location
    private var gps: Gps? = null

    fun getCoordinates(context: Context): LiveData<Gps> {
        job = Job()
        return object : LiveData<Gps>(){
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO + it).launch {
                        val activity: Activity = context as Activity
                        if(checkLocationServicePermission(context)){
                            locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            location = locationManager.getLastKnownLocation("gps")!!
                            onLocationChanged(location)
                        }else{
                            gps=null
                        }
                        withContext(Dispatchers.Main){
                            value=gps
                            it.complete()
                        }
                    }
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        gps = Gps(location.longitude,location.latitude,location.altitude)
    }

    private fun checkLocationServicePermission(context: Context):Boolean{
        val activity: Activity = context as Activity
        return if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            true
        }else{
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            false
        }
    }

    fun cancelJobs(){
        job?.cancel()
    }
}