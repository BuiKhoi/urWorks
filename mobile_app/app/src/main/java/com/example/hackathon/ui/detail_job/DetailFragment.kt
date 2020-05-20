package com.example.hackathon.ui.detail_job

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

import com.example.hackathon.R
import com.example.hackathon.data.model.ItemRecommendJobsModel
import com.example.hackathon.ui.fab_button.history.TimeLineModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.detail_fragment.view.*

class DetailFragment : Fragment() {

    companion object {
        fun newInstance() = DetailFragment()
    }

    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var main_bottom = activity?.findViewById<CoordinatorLayout>(R.id.main_bottom)
        main_bottom!!.animate().translationY(250f).setDuration(300).setInterpolator(
            AccelerateDecelerateInterpolator()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.detail_fragment, container, false)
        var idJob = arguments?.getString("id")
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
                        if(itemRecommendJobsModel.id == idJob){
                            root.title_job.text = itemRecommendJobsModel.title_job
                            root.summary_job.text = itemRecommendJobsModel.summary_job
                            root.location_job.text = itemRecommendJobsModel.location_job
                            root.salary_job.text = itemRecommendJobsModel.salary_job
                            root.request_job.text = itemRecommendJobsModel.request_job
                            root.form_work_job.text = itemRecommendJobsModel.form_work_job
                        }
                    }
                }
            })

        root.cancel_filter_button.setOnClickListener {
            activity?.onBackPressed()
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
