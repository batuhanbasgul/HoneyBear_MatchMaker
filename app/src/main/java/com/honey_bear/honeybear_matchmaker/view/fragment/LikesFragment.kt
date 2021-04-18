package com.honey_bear.honeybear_matchmaker.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.honey_bear.honeybear_matchmaker.R
import com.honey_bear.honeybear_matchmaker.utils.AppUtils
import com.honey_bear.honeybear_matchmaker.view.activity.MainActivity
import com.honey_bear.honeybear_matchmaker.view.adapters.LikesAdapter
import com.honey_bear.honeybear_matchmaker.view_model.FirebaseAuthViewModel
import com.honey_bear.honeybear_matchmaker.view_model.UserViewModel
import kotlinx.android.synthetic.main.fragment_likes.*

class LikesFragment(
        private val mContext: Context
        ) : Fragment() {
    private lateinit var userViewModel:UserViewModel
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel
    private lateinit var likesAdapter:LikesAdapter
    private lateinit var swipeRefreshLayoutLikes:SwipeRefreshLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_likes,container,false)
        swipeRefreshLayoutLikes = view.findViewById(R.id.swipeRefreshLayoutLikes)

        //Setting service parameters and variables
        setServiceVariables()

        setLikes(view)
        setListeners()
        return view
    }

    private fun setServiceVariables() {
        userViewModel = ViewModelProvider(this@LikesFragment).get(UserViewModel::class.java)
        firebaseAuthViewModel = ViewModelProvider(this@LikesFragment).get(FirebaseAuthViewModel::class.java)
        firebaseAuthViewModel.currentFirebaseUser.observe(viewLifecycleOwner, Observer {
            userViewModel.setCurrentUserId(it.uid)
            firebaseAuthViewModel.cancelJobs()
        })
    }

    private fun setLikes(view:View) {
        val progressBarLikes:ProgressBar = view.findViewById(R.id.progressBarLikes)
        progressBarLikes.visibility = View.VISIBLE
        userViewModel.mutualLikes.observe(viewLifecycleOwner, Observer {
            likesAdapter = LikesAdapter(mContext,it)
            recyclerViewLikes.setHasFixedSize(true)
            recyclerViewLikes.layoutManager=LinearLayoutManager(mContext)
            recyclerViewLikes.adapter=likesAdapter

            progressBarLikes.visibility = View.INVISIBLE
            userViewModel.cancelJobs()
        })
    }

    private fun setListeners() {
        swipeRefreshLayoutLikes.setOnRefreshListener {
            refreshLikes()
        }
    }

    private fun refreshLikes() {
        (activity as MainActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayoutMain,LikesFragment(mContext))
                .commit()
    }
}