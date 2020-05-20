package com.example.hackathon.ui.fab_button.swipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hackathon.R
import com.example.hackathon.data.model.SwipeModel
import kotlinx.android.synthetic.main.swipe_item_card.view.*

class CardStackViewAdapter : RecyclerView.Adapter<CardStackViewAdapter.ViewHolder>() {
    private val arrayList : ArrayList<SwipeModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.swipe_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(arrayList.size != 0) arrayList.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var context = holder.title_job.context
        holder.title_job.text = arrayList.get(position).title_job
        holder.summary_job.text = "Summary job : " + arrayList.get(position).summary_job
        holder.salary_job.text = "Salary job : " + arrayList.get(position).salary_job
        holder.location_job.text = "Location job : " + arrayList.get(position).location_job
        Glide.with(context)
            .load(arrayList.get(position).image)
            .centerCrop()
            .placeholder(R.drawable.loading_spinner)
            .into(holder.item_image);
    }

    fun setArrayListData(data : ArrayList<SwipeModel>){
        arrayList.addAll(data)
        this.notifyDataSetChanged()
    }

    fun getAllItems() : ArrayList<SwipeModel>{
        return arrayList
    }

    inner class ViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        var item_image = itemview.item_image as ImageView
        var title_job = itemview.title_job as TextView
        var summary_job = itemview.summary_job as TextView
        var salary_job = itemview.salary_job as TextView
        var location_job = itemview.location_job as TextView
    }
}