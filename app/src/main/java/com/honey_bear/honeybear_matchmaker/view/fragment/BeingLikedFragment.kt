package com.honey_bear.honeybear_matchmaker.view.fragment

import android.content.Context
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
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.view.activity.MainActivity
import com.honey_bear.honeybear_matchmaker.view.adapters.BeingLikedAdapter
import com.honey_bear.honeybear_matchmaker.view_model.FirebaseAuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.fragment_being_liked.*

class BeingLikedFragment(
        private val mContext:Context
        ) : Fragment() {
    private lateinit var userViewModel:UserViewModel
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var beingLikedAdapter: BeingLikedAdapter
    private lateinit var swipeRefreshLayoutBeingLiked: SwipeRefreshLayout
    private lateinit var currentUserId:String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_being_liked,container,false)
        swipeRefreshLayoutBeingLiked = view.findViewById(R.id.swipeRefreshLayoutBeingLiked)

        //Setting service parameters and variables
        setServiceVariables()

        setListeners()
        setBeingLikedUsers(view)
        return view
    }

    private fun setServiceVariables() {
        userViewModel = ViewModelProvider(this@BeingLikedFragment).get(UserViewModel::class.java)
        firebaseAuthViewModel = ViewModelProvider(this@BeingLikedFragment).get(FirebaseAuthViewModel::class.java)
        firebaseAuthViewModel.currentFirebaseUser.observe(viewLifecycleOwner, Observer {
            userViewModel.setCurrentUserId(it.uid)
            currentUserId=it.uid
            firebaseAuthViewModel.cancelJobs()
        })
    }

    private fun setBeingLikedUsers(view:View) {
        val progressBarBeingLiked:ProgressBar = view.findViewById(R.id.progressBarBeingLiked)
        progressBarBeingLiked.visibility = View.VISIBLE
        userViewModel.likesFromUsers.observe(viewLifecycleOwner, Observer { likesFromUsersList->
            userViewModel.likedUsers.observe(viewLifecycleOwner, Observer {  likedUsersList->
                beingLikedAdapter = BeingLikedAdapter(currentUserId,mContext,likesFromUsersList,likedUsersList)
                recyclerViewBeingLiked.setHasFixedSize(true)
                recyclerViewBeingLiked.layoutManager = LinearLayoutManager(mContext)
                recyclerViewBeingLiked.adapter=beingLikedAdapter

                progressBarBeingLiked.visibility = View.VISIBLE
                userViewModel.cancelJobs()
            })
        })
    }

    private fun setListeners() {
        swipeRefreshLayoutBeingLiked.setOnRefreshListener {
            refreshBeingLikedUsers()
        }
    }

    private fun refreshBeingLikedUsers() {
        (activity as MainActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayoutMain,LikedFragment(mContext))
                .commit()
    }
}