package com.honey_bear.honeybear_matchmaker.view.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.data.model.User
import com.honey_bear.honeybear_matchmaker.data.service.GpsRepository
import com.honey_bear.honeybear_matchmaker.view.fragment.*
import com.honey_bear.honeybear_matchmaker.view_model.AuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserLocationViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_drawer_header.view.*

class MainActivity : AppCompatActivity() {
    //API
    private lateinit var userViewModel: UserViewModel
    private lateinit var userLocationViewModel: UserLocationViewModel
    private lateinit var authViewModel: AuthViewModel

    //SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    private lateinit var fragmentObject : Fragment
    private lateinit var fabAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Start Page
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutMain, TempFragment()).commit()

        //Setting service parameters and variables
        setServiceVariables()
        //Init toolbar
        toolbar.title=""
        setSupportActionBar(toolbar)

        //Init NavDrawer
        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,
                drawerLayout,
                toolbar,
                0,
                0)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        setNavigationDrawerHeader()
        setAnimations()
        setListeners()
    }

    private fun setServiceVariables() {
        userViewModel=ViewModelProvider(this@MainActivity).get(UserViewModel::class.java)
        userLocationViewModel=ViewModelProvider(this@MainActivity).get(UserLocationViewModel::class.java)
        authViewModel=ViewModelProvider(this@MainActivity).get(AuthViewModel::class.java)
        authViewModel.currentFirebaseUser.observe(this@MainActivity, Observer {
            userViewModel.setCurrentUserId(it.uid)
            userLocationViewModel.setCurrentUserId(it.uid)
            isInterestsGiven(it)
            authViewModel.cancelJobs()
        })
    }

    private fun setNavigationDrawerHeader() {
        val navigationHeaderView:View =navigationView.inflateHeaderView(R.layout.navigation_drawer_header)
        userViewModel.currentUser.observe(this@MainActivity, Observer {
            if (it.userImageUrl != "no_image") {
                Glide.with(navigationHeaderView.imageViewNavHeader).load(it.userImageUrl).into(navigationHeaderView.imageViewNavHeader)
            }
            val name = "${it.userName} ${it.userSurname}"
            navigationHeaderView.textViewNameNavHeader.text = name
            navigationHeaderView.textViewCityNavHeader.text = it.userCity
            userViewModel.cancelJobs()
        })

    }

    private fun updateLocation() {
        GpsRepository.getCoordinates(this).observe(this@MainActivity, Observer { gps ->
            userLocationViewModel.setUserLocation(this, gps)
            GpsRepository.cancelJobs()
            userLocationViewModel.cancelJobs()
        })
    }

    private fun setListeners() {
        //Floating Action Button
        floatingActionButton.setOnClickListener {
            it.startAnimation(fabAnimation)
            updateLocation()
            supportFragmentManager.beginTransaction().replace(R.id.frameLayoutMain, MatchesFragment(this@MainActivity)).commit()
        }

        //Bottom Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener {
            fragmentObject = when(it.itemId){
                R.id.action_bottom_likes -> {
                    LikesFragment(this@MainActivity)
                }
                R.id.action_bottom_messages -> {
                    MessagesFragment()
                }
                R.id.action_bottom_matches -> {
                    MatchesFragment(this@MainActivity)
                }
                else->{
                    TempFragment()
                }
            }
            supportFragmentManager.beginTransaction().replace(R.id.frameLayoutMain, fragmentObject).commit()
            true
        }

        //Navigation Drawer
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_navprofile -> {
                    fragmentObject = ProfileFragment(this@MainActivity)
                }
                R.id.action_navinterests -> {
                    fragmentObject = InterestsFragment()
                }
                R.id.action_navmessages -> {
                    fragmentObject = MessagesFragment()
                }
                R.id.action_navedit -> {
                    fragmentObject = TempFragment()
                    startActivity(Intent(this@MainActivity, UpdateActivity::class.java))
                    finish()
                }
                R.id.action_navsettings -> {
                    fragmentObject = TempFragment()
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                }
                R.id.action_navlogout -> {
                    fragmentObject = TempFragment()
                    sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
                    editor = sharedPreferences.edit()

                    editor.putBoolean("remember_user", false)
                    editor.remove("email")
                    editor.remove("password")
                    editor.commit()
                    authViewModel.signOut(this, LoginActivity())
                }
                R.id.action_likes -> {
                    fragmentObject = LikesFragment(this@MainActivity)
                }
                R.id.action_liked -> {
                    fragmentObject = LikedFragment(this@MainActivity)
                }
                R.id.action_being_liked -> {
                    fragmentObject = BeingLikedFragment(this@MainActivity)
                }
                else->{
                    fragmentObject=TempFragment()
                }
            }
            supportFragmentManager.beginTransaction().replace(R.id.frameLayoutMain, fragmentObject).commit()
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setAnimations() {
        fabAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.animation_bounce_and_rotate)
    }

    private fun isInterestsGiven(firebaseUserData: FirebaseUser) {
        userViewModel.userData.observe(this@MainActivity, Observer { userData ->
            var isGiven = false
            for (user: User in userData) {
                if (user.userID.equals(firebaseUserData.uid)) {
                    isGiven = true
                    break
                }
            }
            if (!isGiven) {
                startActivity(Intent(this@MainActivity, SetInfoActivity::class.java))
                finish()
            }
            userViewModel.cancelJobs()
        })
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}