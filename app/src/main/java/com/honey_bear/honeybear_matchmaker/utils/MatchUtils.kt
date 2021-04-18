package com.honey_bear.honeybear_matchmaker.utils

import com.honey_bear.honeybear_matchmaker.data.model.User
import com.honey_bear.honeybear_matchmaker.data.model.UserAndPercent

object MatchUtils {

    fun getMatches(currentUserId:String,nearUserIds:ArrayList<String>,allUsers:ArrayList<User>) : ArrayList<UserAndPercent>{
        val result = ArrayList<UserAndPercent>()
        var currentUser:User? = null

        for(user:User in allUsers){
            if(user.userID == currentUserId){
                currentUser=user
                break
            }
        }

        for(nearUserId:String in nearUserIds){
            for (user:User in allUsers){
                if(user.userID == nearUserId){
                    currentUser?.let{
                        result.add(UserAndPercent(user, getRateForMatch(currentUser,user)))
                    }
                    break
                }
            }
        }
        return sortByPercent(result)
    }

    private fun sortByPercent(list:ArrayList<UserAndPercent>):ArrayList<UserAndPercent>{
        list.sortBy {
            it.percent
        }
        val result = ArrayList<UserAndPercent>()
        for(i in list.size-1 downTo 0){
            result.add(list[i])
        }
        return result
    }


    private fun getRateForMatch(firstUser: User, secondUser: User): Int {
        var correctMatchAmount = 1.0
        var result = 0.0

        //AGE
        val ageDiff: Int
        val user1year: Int = AppUtils.getYear(firstUser.userBirthDate!!)
        val user2year: Int = AppUtils.getYear(secondUser.userBirthDate!!)
        ageDiff = if (user1year > user2year) {
            user1year - user2year
        } else {
            user2year - user1year
        }

        //AGE RATE
        if (firstUser.userAgeRate.equals("15+")) {
            correctMatchAmount += 1.0
        } else if (ageDiff >= firstUser.userAgeRate!!.toInt() && ageDiff >= secondUser.userAgeRate!!.toInt()) {
            correctMatchAmount += -1.0
        } else {
            correctMatchAmount += 1.0
        }


        //GENDER
        if (!firstUser.userInterestsGender!!.contains(secondUser.userGender!!) &&
                !secondUser.userInterestsGender!!.contains(firstUser.userGender!!)
        ) {
            correctMatchAmount += -1.0
        } else {
            correctMatchAmount += 1.0
        }


        //ZODIAC
        if (firstUser.userZodiac!! == secondUser.userZodiac!!) {
            correctMatchAmount += 1.0
        }

        //SEASON
        if (firstUser.userInterestsSeason.equals(secondUser.userInterestsSeason)) {
            correctMatchAmount += 1.0
        }

        //WEATHER
        if (firstUser.userInterestsWeather.equals(secondUser.userInterestsWeather)) {
            correctMatchAmount += 1.0
        }

        //MUSIC1
        if (firstUser.userInterestsMusicType1.equals(secondUser.userInterestsMusicType1) ||
                firstUser.userInterestsMusicType1.equals(secondUser.userInterestsMusicType2)
        ) {
            correctMatchAmount += 1.0
        }

        //MUSIC2
        if (firstUser.userInterestsMusicType2.equals(secondUser.userInterestsMusicType2) ||
                firstUser.userInterestsMusicType2.equals(secondUser.userInterestsMusicType1)
        ) {
            correctMatchAmount += 1.0
        }

        //SPORT1
        if (firstUser.userInterestsSportType1.equals(secondUser.userInterestsSportType1) ||
                firstUser.userInterestsSportType1.equals(secondUser.userInterestsSportType2)
        ) {
            correctMatchAmount += 1.0
        }

        //SPORT2
        if (firstUser.userInterestsSportType2.equals(secondUser.userInterestsSportType1) ||
                firstUser.userInterestsSportType2.equals(secondUser.userInterestsSportType2)
        ) {
            correctMatchAmount += 1.0
        }

        //BOOK1
        if (firstUser.userInterestsBookType1.equals(secondUser.userInterestsBookType1) ||
                firstUser.userInterestsBookType1.equals(secondUser.userInterestsBookType2)
        ) {
            correctMatchAmount += 1.0
        }

        //BOOK2
        if (firstUser.userInterestsBookType2.equals(secondUser.userInterestsBookType1) ||
                firstUser.userInterestsBookType2.equals(secondUser.userInterestsBookType2)
        ) {
            correctMatchAmount += 1.0
        }

        //HOBBY1
        if (firstUser.userInterestsHobby1.equals(secondUser.userInterestsHobby1) ||
                firstUser.userInterestsHobby1.equals(secondUser.userInterestsHobby2)
        ) {
            correctMatchAmount += 1.0
        }

        //HOBBY2
        if (firstUser.userInterestsHobby2.equals(secondUser.userInterestsHobby1) ||
                firstUser.userInterestsHobby2.equals(secondUser.userInterestsHobby2)
        ) {
            correctMatchAmount += 1.0
        }

        //MOVIE1
        if (firstUser.userInterestsMovieType1.equals(secondUser.userInterestsMovieType1) ||
                firstUser.userInterestsMovieType1.equals(secondUser.userInterestsMovieType2)
        ) {
            correctMatchAmount += 1.0
        }

        //MOVIE2
        if (firstUser.userInterestsMovieType2.equals(secondUser.userInterestsMovieType1) ||
                firstUser.userInterestsMovieType2.equals(secondUser.userInterestsMovieType2)
        ) {
            correctMatchAmount += 1.0
        }

        result = correctMatchAmount * 100 / 15
        return when {
            result <= 0.0 -> {
                0
            }
            result >= 100.0 -> {
                100
            }
            else -> {
                result.toInt()
            }
        }
    }
}