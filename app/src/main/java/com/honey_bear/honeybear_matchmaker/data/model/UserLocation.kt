package com.honey_bear.honeybear_matchmaker.data.model

import java.io.Serializable

data class UserLocation(var userID:String? = "",
                        var userLongitude:Double? = 0.0,
                        var userLatitude:Double? = 0.0,
                        var userPermission:Boolean = false) : Serializable
