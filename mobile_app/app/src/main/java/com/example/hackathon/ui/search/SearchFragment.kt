package com.example.hackathon.ui.search

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.hackathon.R
import com.example.hackathon.adapter.SearchRecommendJobsAdapter
import com.example.hackathon.data.model.ItemRecommendJobsModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.search_fragment.view.*

class SearchFragment : Fragment() {
    //private var navController : NavController ?= null
    private var arrayRequest : ArrayList<ArrayList<String>> = ArrayList()
    private var arrayRecommendJob : ArrayList<ItemRecommendJobsModel> = ArrayList()
    private lateinit var databaseReference: DatabaseReference
    private var  recommendJobsAdapter = SearchRecommendJobsAdapter()
    private lateinit var recyclerView: RecyclerView
    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get value request
        if(arguments != null){
            arrayRequest = arguments?.get("request") as ArrayList<ArrayList<String>>

            getRecommendJobs()
        } else {
            recommendJobsAdapter.setArrayValueOfJobs(arrayRecommendJob)
        }

        // request value to update recycler view in here
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.search_fragment, container, false)
        recyclerView = root.recycler_view_search_job
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recommendJobsAdapter.setArrayValueOfJobs(arrayRecommendJob)
        recyclerView.adapter = recommendJobsAdapter

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //navController = Navigation.findNavController(view)
        Log.e("Value in search ", arrayRequest.toString())
        view.findViewById<Button>(R.id.button_to_filter).setOnClickListener {

            // Move to filter fragment
            // You can send value from fragment to orther fragment by add a bundle into navigate of nacController
            // ex: navController.navigate(id of fragment or action, bundle)
            // navController!!.navigate(R.id.next_action_searchFragment_to_filter_fragment)
            // Move to other fragment by action fragment between
            val nextAction = SearchFragmentDirections.nextActionSearchToFilter()
            Navigation.findNavController(it).navigate(nextAction)
        }
    }

    override fun onPause() {
        super.onPause()
        // enable item in bottom navigation
        val bottom_nav  = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_nav!!.menu.findItem(R.id.des_search).isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        // disable item in bottom navigation
        val bottom_nav  = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_nav!!.menu.findItem(R.id.des_search).isEnabled = false

        // show bottom navigation
        var main_bottom = activity?.findViewById<CoordinatorLayout>(R.id.main_bottom)
        main_bottom!!.animate().translationY(0f).setDuration(300).setInterpolator(
            AccelerateDecelerateInterpolator()
        )
    }

    private fun getRecommendJobs(){
        // if la nguoi tim viec ne
        databaseReference = FirebaseDatabase.getInstance().getReference("RecommendJobs")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.e("Recommend Jobs", "Cancelled")
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(job in p0.children){
                    var itemRecommendJobsModel = ItemRecommendJobsModel(job)
                    if(itemRecommendJobsModel.form_work_job in arrayRequest
                        || itemRecommendJobsModel.location_job in arrayRequest){
                        arrayRecommendJob.add(ItemRecommendJobsModel(job))
                    }
                }
                recommendJobsAdapter.setArrayValueOfJobs(arrayRecommendJob)
            }
        })
    }
}
