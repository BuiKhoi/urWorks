package com.example.hackathon.ui.fab_button.selected

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.hackathon.R

class SelectedFragment : Fragment() {

    companion object {
        fun newInstance() = SelectedFragment()
    }

    private lateinit var viewModel: SelectedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.selected_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SelectedViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
