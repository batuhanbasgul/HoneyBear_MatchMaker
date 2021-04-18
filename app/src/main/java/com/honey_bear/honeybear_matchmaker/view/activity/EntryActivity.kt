package com.honey_bear.honeybear_matchmaker.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.view_model.FirebaseAuthViewModel
import kotlinx.android.synthetic.main.activity_entry.*
import kotlinx.android.synthetic.main.activity_login.*

class EntryActivity : AppCompatActivity() {
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var animationObject: Animation
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        //Setting service parameters and variables
        setServiceVariables()

        loadEntryPageAnimation()
        setCounter()                //Wait till animation finish
        countDownTimer.start()
    }

    private fun setServiceVariables() {
        firebaseAuthViewModel = ViewModelProvider(this@EntryActivity).get(FirebaseAuthViewModel::class.java)
    }

    private fun loadEntryPageAnimation(){
        //Logo
        animationObject=AnimationUtils.loadAnimation(this@EntryActivity,R.anim.animation_entry_logo)
        imageViewEntry.animation=animationObject

        //Title
        animationObject=AnimationUtils.loadAnimation(this@EntryActivity,R.anim.animation_entry_title)
        appTitleEntry.animation=animationObject
    }

    private fun setCounter(){
        countDownTimer = object: CountDownTimer(6000,1000){
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                checkRememberMe()
            }
        }
    }

    private fun checkRememberMe(){
        firebaseAuthViewModel.currentFirebaseUser.observe(this@EntryActivity, Observer {
            it?.let{
                startActivity(Intent(this@EntryActivity,MainActivity::class.java))
                finish()
            }
        })
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        if(sharedPreferences.getBoolean("remember_user", false)){
            val email:String? = sharedPreferences.getString("email", null)
            val password:String? = sharedPreferences.getString("password", null)
            if(!email.isNullOrEmpty() && !password.isNullOrEmpty()){
                firebaseAuthViewModel.signIn(this,MainActivity(),email,password)
            }
        }else{
            startActivity(Intent(this@EntryActivity, LoginActivity::class.java))
            finish()
        }
    }
}