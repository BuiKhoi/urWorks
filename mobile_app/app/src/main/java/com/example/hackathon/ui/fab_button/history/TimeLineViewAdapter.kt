package com.example.hackathon.ui.fab_button.history

import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon.R
import com.github.vipulasri.timelineview.TimelineView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.item_timeline.view.*


class TimeLineViewAdapter : RecyclerView.Adapter<TimeLineViewAdapter.ViewHoder>() {

    private var arrayList = ArrayList<TimeLineModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoder {

        val view =
            View.inflate(parent.context, R.layout.item_timeline, null)
        return ViewHoder(view, viewType)
    }

    override fun getItemCount(): Int {
        return if (arrayList.size != 0) return arrayList.size else 0
    }

    fun setArrayTimeLine(arrayList: ArrayList<TimeLineModel>) {
        this.arrayList = arrayList
        this.notifyDataSetChanged()
    }

    fun getAllTimeLine(): ArrayList<TimeLineModel> {
        return this.arrayList
    }

    override fun onBindViewHolder(holder: ViewHoder, position: Int) {
        var activity = holder.timeline_title.context.applicationContext
        holder.button_timeline_detail.scaleX = 0f
        holder.button_timeline_detail.scaleY = 0f
        holder.timeline_title.text = arrayList.get(position).title
        holder.timeline_name.text = arrayList.get(position).name
        holder.itemView.setOnClickListener {
            holder.timeline_content.animate().translationX(15f).setDuration(300)
                .setInterpolator(OvershootInterpolator())
            holder.button_timeline_detail.animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(OvershootInterpolator())
        }

        holder.button_timeline_detail.setOnClickListener {
            var bundle = bundleOf(
                "id" to arrayList.get(position).id
            )
            it.findNavController().navigate(R.id.next_action_des_history_to_des_detail, bundle)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    class ViewHoder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        var timeline_content = itemView.timeline_content as MaterialCardView
        var timeline = itemView.timeline as TimelineView
        var button_timeline_detail = itemView.button_detail_timeline as FloatingActionButton
        var timeline_title = itemView.timeline_title as TextView
        var timeline_name = itemView.timeline_name as TextView

        init {
            timeline.initLine(viewType)
        }
    }
}