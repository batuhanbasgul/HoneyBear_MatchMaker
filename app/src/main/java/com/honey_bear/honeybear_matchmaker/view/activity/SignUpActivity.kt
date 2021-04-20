package com.honey_bear.honeybear_matchmaker.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.view_model.AuthViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //Setting service parameters and variables
        setServiceVariables()

        setListeners()
    }

    private fun setServiceVariables() {
        authViewModel = ViewModelProvider(this@SignUpActivity).get(AuthViewModel::class.java)
    }

    private fun setListeners() {
        buttonSignUpCreateAccount.setOnClickListener {
            register()
        }

        textViewSignUpCancel.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
            finish()
        }
    }

    private fun register(){
        val email:String = editTextSignUpEmail.text.toString()
        val pass1:String = editTextSignUpPass1.text.toString()
        val pass2:String = editTextSignUpPass2.text.toString()
        if(checkValidation(email,pass1,pass2)){
            authViewModel.signUp(this,LoginActivity(),email,pass1)
        }
    }

    private fun checkValidation(email:String,pass1:String,pass2:String):Boolean{
        when {
            email.isEmpty() -> {
                Snackbar.make(appTitleSignUp,R.string.sign_up_email_empty_error,Snackbar.LENGTH_LONG).show()
                editTextSignUpEmail.error = resources.getString(R.string.sign_up_email_empty_error)
                return false
            }
            pass1.isEmpty() -> {
                Snackbar.make(appTitleSignUp,R.string.sign_up_password_empty_error,Snackbar.LENGTH_LONG).show()
                editTextSignUpPass1.error = resources.getString(R.string.sign_up_password_empty_error)
                return false
            }
            pass2.isEmpty() -> {
                Snackbar.make(appTitleSignUp,R.string.sign_up_password_empty_error,Snackbar.LENGTH_LONG).show()
                editTextSignUpPass2.error = resources.getString(R.string.sign_up_password_empty_error)
                return false
            }
            pass1 != pass2 -> {
                Snackbar.make(appTitleSignUp,R.string.sign_up_password_match_error,Snackbar.LENGTH_LONG).show()
                return false
            }
            else -> {
                return true
            }
        }
    }

}