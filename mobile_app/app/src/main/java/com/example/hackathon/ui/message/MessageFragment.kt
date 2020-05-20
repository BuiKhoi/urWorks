package com.example.hackathon.ui.message

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.hackathon.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MessageFragment : Fragment() {

    companion object {
        fun newInstance() = MessageFragment()
    }

    private lateinit var viewModel: MessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.message_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onPause() {
        super.onPause()
        Log.e("Pause", "Pause")

        // enable item in bottom navigation
        val bottom_nav  = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_nav!!.menu.findItem(R.id.des_message).isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        Log.e("Resume", "Resume")
        // disable item in bottom navigation
        val bottom_nav  = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_nav!!.menu.findItem(R.id.des_message).isEnabled = false
    }
}
