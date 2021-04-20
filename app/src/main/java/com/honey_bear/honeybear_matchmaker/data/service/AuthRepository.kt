package com.honey_bear.honeybear_matchmaker.data.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.facebook.*
import com.facebook.internal.WebDialog
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.*
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.view.activity.MainActivity
import kotlinx.coroutines.*

object AuthRepository {
    private lateinit var job:CompletableJob
    private lateinit var firebaseAuth: FirebaseAuth

    fun getCurrentUser() : LiveData<FirebaseUser>{
        job = Job()

        return object : LiveData<FirebaseUser>(){
            override fun onActive() {
                super.onActive()
                job.let {
                    CoroutineScope(Dispatchers.IO + it).launch {
                        firebaseAuth = FirebaseAuth.getInstance()
                        firebaseAuth.currentUser?.let{ fbUser->
                            withContext(Dispatchers.Main){
                                value = fbUser
                                it.complete()
                            }
                        }
                    }
                }
            }
        }
    }

    fun signIn(mContext: Context, toActivity: Activity, email: String, pass: String) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { firebaseUser->
            mContext as Activity
            mContext.run {
                if(firebaseUser.isSuccessful){
                    startActivity(Intent(this, toActivity::class.java))
                    this.finish()
                }else{
                    try {
                        throw firebaseUser.exception!!
                    } catch (invalidEmail: FirebaseAuthInvalidUserException) {
                        Toast.makeText(
                                this,
                                resources.getString(R.string.invalid_mail),
                                Toast.LENGTH_LONG
                        ).show()
                    } catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                                this,
                                resources.getString(R.string.invalid_email_or_password),
                                Toast.LENGTH_LONG
                        ).show()
                    } catch (e: java.lang.Exception) {
                        Toast.makeText(
                                this,
                                resources.getString(R.string.login_failed),
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    fun cancelJobs(){
        UserLocationRepository.job?.cancel()
    }

    fun signUp(mContext: Context, toActivity: Activity, email: String, pass: String) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            mContext as Activity
            mContext.run {
                if(it.isSuccessful){
                    Toast.makeText(
                            this,
                            resources.getString(R.string.sign_up_successful),
                            Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, toActivity::class.java))
                    this.finish()
                }else{
                    try {
                        throw it.exception!!
                    } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.sign_up_weak_password),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.sign_up_email_error),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (existEmail: FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.sign_up_email_exists),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            resources.getString(R.string.sign_up_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    fun verifyEmail(){
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.sendEmailVerification()
    }

    fun signOut(mContext: Context, toActivity: Activity){
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        mContext as Activity
        mContext.run {
            startActivity(Intent(this, toActivity::class.java))
            this.finish()
        }
    }
}