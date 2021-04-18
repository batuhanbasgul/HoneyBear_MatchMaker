package com.honey_bear.honeybear_matchmaker.data.model

import java.io.Serializable

data class User(var userID:String? = "",
                var userImageName:String? = "",
                var userImageUrl:String? = "",
                var userName:String? = "",
                var userSurname:String? = "",
                var userMail:String? = "",
                var userGender:String? = "",
                var userBirthDate:String? = "",
                var userCity:String? = "",
                var userZodiac:String? = "",
                var userDescription:String? = "",
                var userAgeRate:String? = "",
                var userInterestsGender:String? = "",
                var userInterestsSeason:String? = "",
                var userInterestsWeather:String? = "",
                var userInterestsMusicType1:String? = "",
                var userInterestsMusicType2:String? = "",
                var userInterestsSportType1:String? = "",
                var userInterestsSportType2:String? = "",
                var userInterestsBookType1:String? = "",
                var userInterestsBookType2:String? = "",
                var userInterestsHobby1:String? = "",
                var userInterestsHobby2:String? = "",
                var userInterestsMovieType1:String? = "",
                var userInterestsMovieType2:String? = "") : Serializable
