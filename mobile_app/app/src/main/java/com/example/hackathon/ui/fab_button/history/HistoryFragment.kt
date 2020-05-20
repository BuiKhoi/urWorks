package com.example.hackathon.ui.fab_button.history

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.hackathon.R
import com.example.hackathon.data.model.ItemRecommendJobsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.history_fragment.view.*

class HistoryFragment : Fragment() {

    private lateinit var recycler_timeline: RecyclerView
    private lateinit var timeLineViewAdapter : TimeLineViewAdapter

    override fun onStart() {
        super.onStart()
        timeLineViewAdapter = TimeLineViewAdapter()
        addTimeLine()
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.history_fragment, container, false)

        recycler_timeline = root.recycler_timeline
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun addTimeLine() {
        var arrayList = ArrayList<TimeLineModel>()
        var prefs: SharedPreferences? =
            context?.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
        var IDUser = prefs!!.getString("IDUser", "default")

        FirebaseDatabase.getInstance().getReference("RecommendJobs").child(IDUser!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (time in p0.children) {
                        var itemRecommendJobsModel = ItemRecommendJobsModel(time)
                        arrayList.add(TimeLineModel(itemRecommendJobsModel.id, itemRecommendJobsModel.title_job, itemRecommendJobsModel.location_job))
                    }
                    arrayList.add(TimeLineModel("ID", "1324567895454455", "45adassdass6"))
                    arrayList.add(TimeLineModel("ID", "1324567895454455", "45adassdass6"))
                    arrayList.add(TimeLineModel("ID", "1324567895454455", "45adassdass6"))
                    arrayList.add(TimeLineModel("ID", "1324567895454455", "45adassdass6"))
                    timeLineViewAdapter.setArrayTimeLine(arrayList)
                    recycler_timeline.adapter = timeLineViewAdapter
                    recycler_timeline.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    recycler_timeline.setHasFixedSize(true)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        var main_bottom = activity?.findViewById<CoordinatorLayout>(R.id.main_bottom)
        main_bottom!!.animate().translationY(0f).setDuration(300).setInterpolator(
            AccelerateDecelerateInterpolator()
        )
    }
}
