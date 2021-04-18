package com.honey_bear.honeybear_matchmaker.view.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.Observer
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.view_model.FirebaseAuthViewModel
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var callbackManager: CallbackManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        //Setting service parameters and variables
        setServiceVariables()

        checkRememberMe()
        startListeners()

        initFacebook()
    }

    private fun setServiceVariables() {
        firebaseAuthViewModel = ViewModelProvider(this@LoginActivity).get(FirebaseAuthViewModel::class.java)
    }

    private fun initFacebook() {
        FacebookSdk.sdkInitialize(this@LoginActivity)
        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this@LoginActivity, listOf(
                    "public_profile",
                    "email"
                )
            )
            LoginManager.getInstance().registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        handlerFacebookAccessToken(loginResult.accessToken)
                    }

                    override fun onCancel() {}
                    override fun onError(error: FacebookException) {}
                })
        }
    }

    private fun handlerFacebookAccessToken(token: AccessToken) {
        val mAuth = FirebaseAuth.getInstance()
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential).addOnCompleteListener(
            this@LoginActivity,
            OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Auth failed", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startListeners() {
        buttonLogin.setOnClickListener {
            if(switchRememberMe.isChecked){
                editor.putBoolean("remember_user", true)
                editor.putString("email", editTextLoginEmail.text.toString())
                editor.putString("password", editTextLoginPass.text.toString())
                editor.commit()
            }else{
                editor.putBoolean("remember_user", false)
                editor.remove("email")
                editor.remove("password")
                editor.commit()
            }
            firebaseAuthViewModel.signIn(
                this,
                MainActivity(),
                editTextLoginEmail.text.toString(),
                editTextLoginPass.text.toString()
            )
        }

        textViewLoginSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }
    }

    private fun checkRememberMe() {
        if(sharedPreferences.getBoolean("remember_user", false)){
            switchRememberMe.isChecked=true
            val email:String? = sharedPreferences.getString("email", null)
            val password:String? = sharedPreferences.getString("password", null)

            if(!email.isNullOrEmpty() && !password.isNullOrEmpty()){
                editTextLoginEmail.setText(email.toString())
                editTextLoginPass.setText(password.toString())
            }
        }else{
            switchRememberMe.isChecked=false
        }
    }
}