package com.honey_bear.honeybear_matchmaker.view_model

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.honey_bear.honeybear_matchmaker.data.service.FirebaseAuthRepository

class FirebaseAuthViewModel : ViewModel() {

    val currentFirebaseUser : LiveData<FirebaseUser> = FirebaseAuthRepository.getCurrentUser()

    fun signUp(mContext: Context, toActivity: Activity, email:String, pass:String){
        FirebaseAuthRepository.signUp(mContext,toActivity,email,pass)
    }

    fun signIn(mContext: Context, toActivity: Activity, email:String, pass:String){
        FirebaseAuthRepository.signIn(mContext,toActivity,email,pass)
    }

    fun verifyEmail(){
        FirebaseAuthRepository.verifyEmail()
    }

    fun signOut(mContext: Context, toActivity: Activity){
        FirebaseAuthRepository.signOut(mContext,toActivity)
    }

    fun cancelJobs(){
        FirebaseAuthRepository.cancelJobs()
    }
}