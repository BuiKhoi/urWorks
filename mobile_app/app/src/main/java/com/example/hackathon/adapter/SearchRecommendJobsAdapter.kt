package com.example.hackathon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathon.R
import com.example.hackathon.data.model.ItemRecommendJobsModel
import kotlinx.android.synthetic.main.item_recommend_jobs.view.*

class SearchRecommendJobsAdapter : RecyclerView.Adapter<SearchRecommendJobsAdapter.ViewHolder>() {
    private var arrayValueOfJobs = ArrayList<ItemRecommendJobsModel>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchRecommendJobsAdapter.ViewHolder {
        var itemview =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recommend_jobs, parent, false)
        return ViewHolder(itemview)
    }

    override fun getItemCount(): Int {
        return if (arrayValueOfJobs.size == 0) 0 else arrayValueOfJobs.size
    }

    override fun onBindViewHolder(holder: SearchRecommendJobsAdapter.ViewHolder, position: Int) {
        var mContext = holder.title_job.context
//        if (arrayValueOfJobs.get(position).avatar_recuiter.equals("default")) {
//            holder.avatar.setImageResource(R.drawable.ic_launcher_foreground)
//        } else {
//            Glide.with(mContext)
//                .load(arrayValueOfJobs.get(position).avatar_recuiter).centerCrop()
//                .fitCenter().into(holder.avatar)
//        }

        Glide.with(mContext)
            .load(R.drawable.anh1).centerCrop()
            .fitCenter().into(holder.avatar)

        holder.title_job.text = arrayValueOfJobs.get(position).title_job
        holder.location_job.text = "Location : " + arrayValueOfJobs.get(position).location_job
        holder.salary_job.text = "Salary : " + arrayValueOfJobs.get(position).salary_job
        holder.summary_job.text = "Summary : " + arrayValueOfJobs.get(position).summary_job
        holder.function_job.text = "Form work job : " + arrayValueOfJobs.get(position).form_work_job
    }

    fun setArrayValueOfJobs(arrayList: ArrayList<ItemRecommendJobsModel>) {
        this.arrayValueOfJobs = arrayList
        this.notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var avatar = itemView.avatar_of_item_recommend_jobs as ImageView
        var title_job = itemView.title_job_item_recommend_jobs as TextView
        var salary_job = itemView.salary_item_recommend_jobs as TextView
        var summary_job = itemView.summary_item_recommend_jobs as TextView
        var function_job = itemView.function_item_recommend_jobs as TextView
        var location_job = itemView.location_item_recommend_jobs as TextView
    }
}