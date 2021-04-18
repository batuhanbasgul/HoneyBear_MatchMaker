package com.honey_bear.honeybear_matchmaker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.honey_bear.honeybear_matchmaker.R

class MessagesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_messages,container,false)
        //TODO add messaging module
        return view
    }
}