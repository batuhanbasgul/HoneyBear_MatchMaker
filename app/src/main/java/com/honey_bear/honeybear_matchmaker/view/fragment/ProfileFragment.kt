package com.honey_bear.honeybear_matchmaker.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.view.activity.MainActivity
import com.honey_bear.honeybear_matchmaker.view.activity.UpdateActivity
import com.honey_bear.honeybear_matchmaker.view_model.AuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment(private val mContext: Context) : Fragment() {
    private lateinit var userViewModel:UserViewModel
    private lateinit var authViewModel : AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        //Setting service parameters and variables
        setServiceVariables()

        setUserProfileData()
        emailVerification(view)
        setListeners(view)
        return view
    }

    private fun setServiceVariables() {
        userViewModel = ViewModelProvider(this@ProfileFragment).get(UserViewModel::class.java)
        authViewModel = ViewModelProvider(this@ProfileFragment).get(AuthViewModel::class.java)
        authViewModel.currentFirebaseUser.observe(viewLifecycleOwner, Observer {
            userViewModel.setCurrentUserId(it.uid)
            authViewModel.cancelJobs()
        })
    }

    private fun emailVerification(view:View) {
        val textViewVerifyEmail:TextView = view.findViewById(R.id.textViewVerifyEmail)
        authViewModel.currentFirebaseUser.observe(viewLifecycleOwner, Observer {
            if(!it.isEmailVerified){
                textViewVerifyEmail.visibility = View.VISIBLE
            }
            authViewModel.cancelJobs()
        })
    }

    private fun setListeners(view:View) {
        val textViewVerifyEmail:TextView = view.findViewById(R.id.textViewVerifyEmail)
        val buttonProfileUpdate:Button = view.findViewById(R.id.buttonProfileUpdate)

        textViewVerifyEmail.setOnClickListener {
            authViewModel.verifyEmail()
            Toast.makeText(mContext,resources.getString(R.string.profile_verification_mail_sent),Toast.LENGTH_LONG).show()
        }

        buttonProfileUpdate.setOnClickListener {
            startActivity(Intent((activity as MainActivity), UpdateActivity::class.java))
            (activity as MainActivity).finish()
        }
    }

    private fun setUserProfileData() {
        userViewModel.currentUser.observe(viewLifecycleOwner, Observer {
            if (it.userImageUrl != "no_image") {
                Glide.with(imageViewProfile).load(it.userImageUrl).into(imageViewProfile)
            }
            val name = "${it.userName} ${it.userSurname}"
            textViewProfileName.text = name
            textViewProfileCity.text = it.userCity
            textViewProfileBirthDate.text = it.userBirthDate
            textViewProfileGender.text = it.userGender
            textViewProfileEmail.text = it.userMail
            textViewProfileDescription.text = it.userDescription
            textViewProfileZodiac.text = it.userZodiac
            userViewModel.cancelJobs()
        })
    }
}