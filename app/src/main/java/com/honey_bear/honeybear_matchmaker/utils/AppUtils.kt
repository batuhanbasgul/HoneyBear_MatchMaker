package com.honey_bear.honeybear_matchmaker.utils

import android.app.Activity
import android.content.Context
import com.honey_bear.honeybear_matchmaker.data.model.UserLocation
import com.honey_bear.honeybear_matchmaker.view.activity.MainActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.cos

object AppUtils {
    /*
        Get year from string date
     */
    fun getYear(date: String):Int{
        var reverse=""
        var year=""
        reverse = date.reversed()
        for(index in 3 downTo 0){
            year+=reverse[index]
        }
        return year.toInt()
    }

    /*
       Get age from string date
    */
    fun getAge(date: String):Int{
        val dateYear = getYear(date)
        val currentYear:Int = Calendar.getInstance().get(Calendar.YEAR)
        return currentYear-dateYear
    }

    /*
        Get permitted distances
     */
    fun getGaps(userLocation: UserLocation) : HashMap<String, Double>{
        val searchSize = 50.0
        val gapLatitude=searchSize/111;                                                  //111 is the distance between latitudes
        val gapLongitude=searchSize/(111* cos(userLocation.userLatitude!!));             //111*Math.cos(latitude) is the distance between longitudes

        val map = HashMap<String, Double>()
        map["longitudeGap"] = gapLongitude
        map["latitudeGap"] = gapLatitude

        return map
    }

    /*
        Get latitude difference between two users location
     */
    fun getLatitudeDiff(firstLatitude: Double, secondLatitude: Double):Double{
        return if (firstLatitude < 0 && secondLatitude < 0) {
            //Both of them negative
            if (firstLatitude >= secondLatitude) {
                secondLatitude * -1 + firstLatitude
            } else {
                firstLatitude * -1 + secondLatitude
            }
        } else if (firstLatitude >= 0 && secondLatitude < 0) {
            //first is positive, second is negative
            firstLatitude - secondLatitude
        } else if (firstLatitude < 0 && secondLatitude >= 0) {
            //first is negative, second is positive
            secondLatitude - firstLatitude
        } else {
            //Both of them positive
            if (firstLatitude >= secondLatitude) {
                firstLatitude - secondLatitude
            } else {
                secondLatitude - firstLatitude
            }
        }
    }

    /*
        Get longitude difference between two users location
     */
    fun getLongitudeDiff(firstLongitude: Double, secondLongitude: Double):Double{
        return if (firstLongitude < 0 && secondLongitude < 0) {
            //Both of them negative
            if (firstLongitude >= secondLongitude) {
                secondLongitude * -1 + firstLongitude
            } else {
                firstLongitude * -1 + secondLongitude
            }
        } else if (firstLongitude >= 0 && secondLongitude < 0) {
            //first is positive, second is negative
            firstLongitude - secondLongitude
        } else if (firstLongitude < 0 && secondLongitude >= 0) {
            //first is negative, second is positive
            secondLongitude - firstLongitude
        } else {
            //Both of them positive
            if (firstLongitude >= secondLongitude) {
                firstLongitude - secondLongitude
            } else {
                secondLongitude - firstLongitude
            }
        }
    }

    /*
        Get mutual ıd list from two list
     */
    fun getMutualIdList(likedUsersIds: ArrayList<String>, likesFromUsersIds: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()
        for(likedId in likedUsersIds){
            for(likesId in likesFromUsersIds){
                if(likedId == likesId){
                    result.add(likedId)
                    break
                }
            }
        }
        return result
    }
}