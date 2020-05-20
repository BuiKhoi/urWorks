package com.example.hackathon.ui.filter_recommen.type

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.example.hackathon.R
import com.igalata.bubblepicker.BubblePickerListener
import com.igalata.bubblepicker.adapter.BubblePickerAdapter
import com.igalata.bubblepicker.model.PickerItem
import com.igalata.bubblepicker.rendering.BubblePicker
import kotlinx.android.synthetic.main.filter_recommend_fragment.view.*

class FilterRecommendFragment : Fragment() {

    private var navController: NavController? = null
    private var bubblePicker: BubblePicker? = null

    private var value_request: ArrayList<String> = ArrayList()

    private var array_types: ArrayList<String> = arrayListOf(
        "Part Time",
        "Full time",
        "Outside",
        "Inside",
        "Laptop",
        "IT",
        "Peru",
        "Venezuela",
        "Costa Rica",
        "Chile",
        "Bolivia",
        "Argentina"
    )
    private var array_type_of_types: ArrayList<String> = arrayListOf(
        "Part Time 2",
        "Full time 2",
        "Outside 2",
        "Inside 2",
        "Laptop 2",
        "IT 2",
        "Peru 2",
        "Venezuela 2",
        "Costa Rica 2",
        "Chile 2",
        "Bolivia 2",
        "Argentina 2"
    )

    private var maxRequestValue = 2

    private var arrayIsAccessing = 0

    private var colorPicker = "#FF2F53"

    companion object {
        fun newInstance() =
            FilterRecommendFragment()
    }

    private lateinit var viewModel: FilterRecommendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide bottom navigation to full screen
        var main_bottom = activity?.findViewById<CoordinatorLayout>(R.id.main_bottom)
        main_bottom!!.animate().translationY(250f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.filter_recommend_fragment, container, false)
        // find bubble view by id
        bubblePicker = root.findViewById(R.id.bubble_picker)
        // Set up bubble
        createBubbles(array_types)

        root.next_action_filter_button.setOnClickListener {
            // begin add value into array to request
            if(bubblePicker!!.selectedItems.size != 0){
                for(item in bubblePicker!!.selectedItems.toList()){
                    value_request.add(item!!.title.toString())
                }
            }
            // end add value into array to request
            // next bubble
            nextBubbles(++arrayIsAccessing)
            Toast.makeText(context, "Value : ${value_request.toString()}" , Toast.LENGTH_LONG).show()
        }
        root.reset_filter_button.setOnClickListener {
            resetBubbles(arrayIsAccessing)
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FilterRecommendViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<TextView>(R.id.cancel_filter_button).setOnClickListener {

            // Move to filter fragment
            // You can send value from fragment to orther fragment by add a bundle into navigate of nacController
            // ex: navController.navigate(id of fragment or action, bundle)
            activity?.onBackPressed()
        }
    }

    private fun createBubbles(arrayList: ArrayList<String>){
        bubblePicker.apply {
            this!!.adapter = object : BubblePickerAdapter {
                override val totalCount: Int = arrayList.size
                override fun getItem(position: Int): PickerItem {
                    return PickerItem().apply {
                        title = arrayList[position]
                        textColor = Color.WHITE
                        color = Color.parseColor(colorPicker)
                    }
                }
            }
            this!!.listener = object : BubblePickerListener {
                override fun onBubbleDeselected(item: PickerItem) {
                    Log.e("BubblePicker", "Deselected + ${item.title}")
                }

                override fun onBubbleSelected(item: PickerItem) {
                    Log.e("BubblePicker", "Selected + ${item.title}")
                }
            }
            this!!.bubbleSize = 5
        }
    }

    private fun nextBubbles(nameValues: Int){
        if(nameValues <maxRequestValue){
            bubblePicker!!.onPause()
            bubblePicker!!.onResume()
            when(nameValues){
                0 -> createBubbles(array_types)
                1 -> createBubbles(array_type_of_types)
            }
        } else {
            // move back searchFragment and request
            Log.e("Request", "Ready to request")
            // value send to searchFragment to make recommend
            val bundle = bundleOf(
                "request" to  value_request
            )
            navController!!.navigate(R.id.next_action_filter_to_search, bundle)
        }
    }

    private fun resetBubbles(nameValues : Int){
        bubblePicker!!.onPause()
        bubblePicker!!.onResume()
        when(nameValues){
            0 -> createBubbles(array_types)
            1 -> createBubbles(array_type_of_types)
        }
    }
    override fun onResume() {
        super.onResume()
        bubblePicker!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        bubblePicker!!.onPause()
    }
}
