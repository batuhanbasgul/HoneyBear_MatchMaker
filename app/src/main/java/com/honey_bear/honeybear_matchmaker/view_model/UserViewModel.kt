package com.honey_bear.honeybear_matchmaker.view_model

import android.net.Uri
import androidx.lifecycle.*
import com.honey_bear.honeybear_matchmaker.data.model.User
import com.honey_bear.honeybear_matchmaker.data.service.UserRepository

class UserViewModel : ViewModel() {
    private val _currentUserId:MutableLiveData<String> = MutableLiveData<String>()

    val userData : LiveData<ArrayList<User>> = UserRepository.getUsers()
    val currentUser : LiveData<User> = Transformations.switchMap(_currentUserId){
        UserRepository.getCurrentUser(it)
    }
    val likesFromUsers : LiveData<ArrayList<User>> = Transformations.switchMap(_currentUserId){
        UserRepository.getBeingLikedUsers(it)
    }
    val likedUsers : LiveData<ArrayList<User>> = Transformations.switchMap(_currentUserId){
        UserRepository.getLikedUsers(it)
    }
    val mutualLikes : LiveData<ArrayList<User>> = Transformations.switchMap(_currentUserId){
        UserRepository.getMutualLikes(it)
    }

    fun cancelJobs(){
        UserRepository.cancelJobs()
    }

    fun insertUser(user: User, imageUri: Uri?){
        UserRepository.insertUser(user,imageUri)
    }

    fun insertFacebookUser(user:User){
        UserRepository.insertFacebookUser(user)
    }

    fun updateUser(user: User, imageUri: Uri?){
        UserRepository.updateUser(user,imageUri)
    }

    fun addLikedUser(currentUserId: String,likedUserId:String){
            UserRepository.addLikedUser(currentUserId,likedUserId)
    }

    fun removeLikedUser(currentUserId: String,likedUserId:String){
            UserRepository.removeLikedUser(currentUserId,likedUserId)
    }

    fun setCurrentUserId(currentUserId:String?){
            if(_currentUserId.value == currentUserId){
                return
            }
            _currentUserId.value = currentUserId
        }
}