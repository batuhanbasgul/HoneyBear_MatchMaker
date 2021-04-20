package com.honey_bear.honeybear_matchmaker.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.data.model.UserLocation
import com.honey_bear.honeybear_matchmaker.utils.MatchUtils
import com.honey_bear.honeybear_matchmaker.view.activity.MainActivity
import com.honey_bear.honeybear_matchmaker.view.activity.SettingsActivity
import com.honey_bear.honeybear_matchmaker.view.adapters.MatchesAdapter
import com.honey_bear.honeybear_matchmaker.view_model.AuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserLocationViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_matches.*

class MatchesFragment(
        private val mContext: Context
        ) : Fragment() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var userLocationViewModel: UserLocationViewModel
    private lateinit var authViewModel:AuthViewModel
    private lateinit var matchesAdapter: MatchesAdapter
    private lateinit var swipeRefreshLayoutMatches: SwipeRefreshLayout
    private lateinit var currentUserId:String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_matches,container,false)
        swipeRefreshLayoutMatches = view.findViewById(R.id.swipeRefreshLayoutMatches)

        //Setting service parameters and variables
        setServiceVariables()

        setListeners()
        setMatches(view)

        return view
    }

    private fun setServiceVariables() {
        userViewModel= ViewModelProvider(this@MatchesFragment).get(UserViewModel::class.java)
        userLocationViewModel= ViewModelProvider(this@MatchesFragment).get(UserLocationViewModel::class.java)
        authViewModel= ViewModelProvider(this@MatchesFragment).get(AuthViewModel::class.java)

        authViewModel.currentFirebaseUser.observe(viewLifecycleOwner, Observer {
            userLocationViewModel.setCurrentUserId(it.uid)
            userViewModel.setCurrentUserId(it.uid)
            currentUserId=it.uid
            authViewModel.cancelJobs()
        })
    }

    private fun setMatches(view:View) {
        val progressBarMatches:ProgressBar = view.findViewById(R.id.progressBarMatches)
        progressBarMatches.visibility = View.VISIBLE
        userViewModel.userData.observe(viewLifecycleOwner, Observer { allUsers->
            userViewModel.likedUsers.observe(viewLifecycleOwner, Observer { likedUsers->
                userLocationViewModel.nearUserLocations.observe(viewLifecycleOwner, Observer { nearUserLocationList->
                    userLocationViewModel.currentUserLocationData.observe(viewLifecycleOwner, Observer { currentUserLocation->
                        currentUserLocation?.let {
                            if(it.userPermission){
                                val nearUserIds = ArrayList<String>()
                                for(userLocation: UserLocation in nearUserLocationList){
                                    nearUserIds.add(userLocation.userID!!)
                                }
                                val matchList = MatchUtils.getMatches(it.userID!!,nearUserIds,allUsers)
                                matchesAdapter =  MatchesAdapter(currentUserId,mContext,matchList,likedUsers)
                                recyclerViewMatches.setHasFixedSize(true)
                                recyclerViewMatches.layoutManager = LinearLayoutManager(mContext)
                                recyclerViewMatches.adapter=matchesAdapter
                            }else{
                                mContext as Activity
                                mContext.run {
                                    val snackBar = Snackbar.make(toolbar,"İzin vermek istiyor musunuz ?",Snackbar.LENGTH_LONG)
                                    snackBar.setAction("Evet",View.OnClickListener {
                                            startActivity(Intent(this,SettingsActivity::class.java))
                                        })
                                    snackBar.setBackgroundTint(resources.getColor(R.color.custom_3))
                                    snackBar.show()

                                }
                            }
                        }
                        progressBarMatches.visibility = View.INVISIBLE
                        userLocationViewModel.cancelJobs()
                        userViewModel.cancelJobs()
                    })
                })
            })
        })
    }

    private fun setListeners() {
        swipeRefreshLayoutMatches.setOnRefreshListener {
            refreshMatches()
        }
    }

    private fun refreshMatches() {
        (activity as MainActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayoutMain,MatchesFragment(mContext))
                .commit()
    }
}