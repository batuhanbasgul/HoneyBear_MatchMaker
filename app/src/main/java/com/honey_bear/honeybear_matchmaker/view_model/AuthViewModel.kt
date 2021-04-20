package com.honey_bear.honeybear_matchmaker.view_model

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.honey_bear.honeybear_matchmaker.data.service.AuthRepository

class AuthViewModel : ViewModel() {

    val currentFirebaseUser : LiveData<FirebaseUser> = AuthRepository.getCurrentUser()

    fun signIn(mContext: Context, toActivity: Activity, email:String, pass:String){
        AuthRepository.signIn(mContext,toActivity,email,pass)
    }

    fun signUp(mContext: Context, toActivity: Activity, email:String, pass:String){
        AuthRepository.signUp(mContext,toActivity,email,pass)
    }

    fun verifyEmail(){
        AuthRepository.verifyEmail()
    }

    fun signOut(mContext: Context, toActivity: Activity){
        AuthRepository.signOut(mContext,toActivity)
    }

    fun cancelJobs(){
        AuthRepository.cancelJobs()
    }
}