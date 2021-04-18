package com.honey_bear.honeybear_matchmaker.data.service

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.google.firebase.database.*
import com.honey_bear.honeybear_matchmaker.data.model.Gps
import com.honey_bear.honeybear_matchmaker.data.model.UserLocation
import com.honey_bear.honeybear_matchmaker.utils.AppUtils
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object UserLocationRepository {
    private lateinit var userLocation:UserLocation
    val userLocationRef = FirebaseDatabase.getInstance().reference
    var job: CompletableJob? = null

    /*
        Get all user location data
     */
    fun getUserLocations() : LiveData<ArrayList<UserLocation>> {
        val userLocationList = ArrayList<UserLocation>()
        job = Job()

        return object : LiveData<ArrayList<UserLocation>>() {
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO + it).launch {
                        val query: Query = userLocationRef.child("UserLocation")
                        userLocationList.clear()
                        val userLocationListener = object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (d: DataSnapshot in snapshot.children){
                                    d.getValue(UserLocation::class.java)?.let { userLocation ->
                                        userLocationList.add(userLocation)
                                    }
                                }
                                value = userLocationList
                                it.complete()
                            }
                            override fun onCancelled(error: DatabaseError) {
                                //TODO
                            }
                        }
                        query.addListenerForSingleValueEvent(userLocationListener)
                    }
                }
            }
        }
    }

    /*
        Current users location
     */
    fun getCurrentUsersLocation(currentUserId:String) : LiveData<UserLocation>{
        job = Job()
        return object : LiveData<UserLocation>(){
            override fun onActive() {
                super.onActive()
                job?.let{
                    CoroutineScope(Dispatchers.IO + it).launch {
                        val query:Query = userLocationRef.child("UserLocation").orderByChild("userID").equalTo(
                            currentUserId
                        )
                        val dataListener = object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(d:DataSnapshot in snapshot.children){
                                    d.getValue(UserLocation::class.java)?.let{ userLocation->
                                        if(userLocation.userID.equals(currentUserId)){
                                            value = userLocation
                                            it.complete()
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                //TODO
                            }
                        }
                        query.addListenerForSingleValueEvent(dataListener)
                    }
                }
            }
        }
    }

    /*
        Near Users
     */
    fun getNearUserLocations(currentUserId:String) : LiveData<ArrayList<UserLocation>>{
        val nearUserLocationList = ArrayList<UserLocation>()
        var myLocation = UserLocation()
        job = Job()

        return object : LiveData<ArrayList<UserLocation>>(){
            override fun onActive() {
                super.onActive()
                job?.let {
                    CoroutineScope(Dispatchers.IO + it).launch {
                        val query:Query = userLocationRef.child("UserLocation")
                        val dataListener = object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                nearUserLocationList.clear()

                                //Current User
                                for (d:DataSnapshot in snapshot.children){
                                    d.getValue(UserLocation::class.java)?.let{ userLocation->
                                        if(userLocation.userID.equals(currentUserId)){
                                            myLocation = userLocation
                                        }
                                    }
                                }

                                //Near Users
                                for(d:DataSnapshot in snapshot.children){
                                    d.getValue(UserLocation::class.java)?.let{ userLocation->
                                        if(userLocation.userPermission){
                                            val latitudeDiff = AppUtils.getLatitudeDiff(
                                                    myLocation.userLatitude!!,
                                                    userLocation.userLatitude!!
                                            )
                                            val longitudeDiff = AppUtils.getLongitudeDiff(
                                                    myLocation.userLongitude!!,
                                                    userLocation.userLongitude!!
                                            )
                                            val gapMap = AppUtils.getGaps(myLocation)
                                            if (latitudeDiff <= gapMap["latitudeGap"]!! && longitudeDiff <= gapMap["longitudeGap"]!!) {
                                                if(userLocation.userID != myLocation.userID){
                                                    nearUserLocationList.add(userLocation) //NEAR USERS
                                                }
                                            }
                                        }
                                    }
                                }
                                value = nearUserLocationList
                                it.complete()
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        }
                        query.addListenerForSingleValueEvent(dataListener)
                    }
                }
            }
        }
    }

    fun cancelJobs(){
        job?.cancel()
    }

    /*
        Gets gps coordinates
        gets User's permission for matching
        If user's location info is already exists updates it else just insert
     */
    fun setUserLocation(context: Context, gps: Gps?, currentUserId:String){
        var isUserLocationExists = false

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences((context as Activity).baseContext)
        val matchPermission:Boolean = sharedPreferences.getBoolean("match", true)

        gps?.let{
            userLocation = UserLocation(currentUserId, it.longitude, it.latitude, matchPermission)
            val query: Query = userLocationRef.child("UserLocation")
            val dataListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (d: DataSnapshot in snapshot.children) {
                        d.getValue(UserLocation::class.java)?.let {
                            if (it.userID == userLocation.userID) {
                                isUserLocationExists = true
                            }
                        }
                    }
                    if (isUserLocationExists) {
                        updateUserLocation(userLocation)
                    } else {
                        insertUserLocation(userLocation)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO
                }

            }
            query.addListenerForSingleValueEvent(dataListener)
        }
    }
    private fun insertUserLocation(_userLocation: UserLocation?) {
        _userLocation?.let {
            val dbRef = userLocationRef.child("UserLocation")
            dbRef.push().setValue(_userLocation)
        }
    }
    private fun updateUserLocation(_userLocation: UserLocation?) {
        _userLocation?.let{
            val mapList = HashMap<String, Any>()
            mapList["userID"]=_userLocation.userID!!
            mapList["userLongitude"]=_userLocation.userLongitude!!
            mapList["userLatitude"]=_userLocation.userLatitude!!
            mapList["userPermission"]=_userLocation.userPermission

            val dbRef = userLocationRef.child("UserLocation")
            val dataListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(d:DataSnapshot in snapshot.children){
                        d.getValue(UserLocation::class.java)?.let{ user->
                            if(user.userID == _userLocation.userID){
                                d.key?.let {
                                    dbRef.child(it).updateChildren(mapList)
                                }
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    //TODO
                }
            }
            dbRef.addListenerForSingleValueEvent(dataListener)
        }
    }
}
