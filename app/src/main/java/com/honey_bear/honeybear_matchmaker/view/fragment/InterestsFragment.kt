package com.honey_bear.honeybear_matchmaker.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.view.activity.MainActivity
import com.honey_bear.honeybear_matchmaker.view.activity.UpdateActivity
import com.honey_bear.honeybear_matchmaker.view_model.FirebaseAuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.fragment_interests.*

class InterestsFragment : Fragment() {
    private lateinit var userViewModel:UserViewModel
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_interests, container, false)

        //Setting service parameters and variables
        setServiceVariables()

        setUserInterests()
        setListeners(view)

        return view
    }

    private fun setServiceVariables() {
        userViewModel = ViewModelProvider(this@InterestsFragment).get(UserViewModel::class.java)
        firebaseAuthViewModel = ViewModelProvider(this@InterestsFragment).get(FirebaseAuthViewModel::class.java)
        firebaseAuthViewModel.currentFirebaseUser.observe(viewLifecycleOwner, Observer {
            userViewModel.setCurrentUserId(it.uid)
            firebaseAuthViewModel.cancelJobs()
        })
    }

    private fun setListeners(view: View) {
        val buttonInterestsUpdate:Button = view.findViewById(R.id.buttonInterestsUpdate)

        buttonInterestsUpdate.setOnClickListener {
            startActivity(Intent(activity as MainActivity,UpdateActivity::class.java))
            (activity as MainActivity).finish()
        }
    }

    private fun setUserInterests() {
        userViewModel.currentUser.observe(viewLifecycleOwner, Observer {
            textViewInterestsAgeRate.text = it.userAgeRate
            textViewInterestsGenderInterest.text = it.userInterestsGender
            textViewInterestsSeason.text = it.userInterestsSeason
            textViewInterestsWeather.text = it.userInterestsWeather
            textViewInterestsMusic1.text = it.userInterestsMusicType1
            textViewInterestsMusic2.text = it.userInterestsMusicType2
            textViewInterestsSport1.text = it.userInterestsSportType1
            textViewInterestsSport2.text = it.userInterestsSportType2
            textViewInterestsBook1.text = it.userInterestsBookType1
            textViewInterestsBook2.text = it.userInterestsBookType2
            textViewInterestsHobby1.text = it.userInterestsHobby1
            textViewInterestsHobby2.text = it.userInterestsHobby2
            textViewInterestsMovie1.text = it.userInterestsMovieType1
            textViewInterestsMovie2.text = it.userInterestsMovieType2
            userViewModel.cancelJobs()
        })
    }
}