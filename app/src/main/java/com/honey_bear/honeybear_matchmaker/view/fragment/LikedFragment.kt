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
import com.honey_bear.honeybear_matchmaker.view.adapters.LikedAdapter
import com.honey_bear.honeybear_matchmaker.view_model.FirebaseAuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_liked.*

class LikedFragment(
        private val mContext: Context
        ) : Fragment() {
    private lateinit var userViewModel:UserViewModel
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var likedAdapter: LikedAdapter
    private lateinit var swipeRefreshLayoutLiked:SwipeRefreshLayout
    private lateinit var currentUserId:String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_liked,container,false)
        swipeRefreshLayoutLiked = view.findViewById(R.id.swipeRefreshLayoutLiked)

        //Setting service parameters and variables
        setServiceVariables()

        setLikedUsers(view)
        setListeners()
        return view
    }

    private fun setServiceVariables() {
        userViewModel = ViewModelProvider(this@LikedFragment).get(UserViewModel::class.java)
        firebaseAuthViewModel = ViewModelProvider(this@LikedFragment).get(FirebaseAuthViewModel::class.java)
        firebaseAuthViewModel.currentFirebaseUser.observe(viewLifecycleOwner, Observer {
            userViewModel.setCurrentUserId(it.uid)
            currentUserId=it.uid
            firebaseAuthViewModel.cancelJobs()
        })
    }

    private fun setLikedUsers(view:View) {
        val progressBarLiked: ProgressBar = view.findViewById(R.id.progressBarLiked)
        progressBarLiked.visibility = View.VISIBLE
        userViewModel.likedUsers.observe(viewLifecycleOwner, Observer {
            likedAdapter = LikedAdapter(currentUserId,mContext,it)
            recyclerViewLiked.setHasFixedSize(true)
            recyclerViewLiked.layoutManager = LinearLayoutManager(mContext)
            recyclerViewLiked.adapter = likedAdapter

            progressBarLiked.visibility = View.INVISIBLE
            userViewModel.cancelJobs()
        })
    }

    private fun setListeners() {
        swipeRefreshLayoutLiked.setOnRefreshListener {
            refreshLikedUsers()
        }
    }

    fun refreshLikedUsers() {
        (activity as MainActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayoutMain,LikedFragment(mContext))
                .commit()
    }
}