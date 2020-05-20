package com.example.hackathon.ui.fab_button.swipe

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator

import com.example.hackathon.R
import com.example.hackathon.data.model.ItemRecommendJobsModel
import com.example.hackathon.ui.fab_button.swipe.adapter.CardStackViewAdapter
import com.example.hackathon.data.model.SwipeModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.swipe_fragment.*
import kotlinx.android.synthetic.main.swipe_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SwipeFragment : Fragment() {

    private var cardStackAdapter =
        CardStackViewAdapter()
    private lateinit var manager: CardStackLayoutManager

    companion object {
        fun newInstance() = SwipeFragment()
    }

    private lateinit var viewModel: SwipeViewModel

    private var arrayListJobInAdapter: ArrayList<SwipeModel> = ArrayList()
    private var jobLiked = "null"
    override fun onStart() {
        super.onStart()
        addList()
        arrayListJobInAdapter = cardStackAdapter.getAllItems()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.swipe_fragment, container, false)
        root.like_button.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            card_stack_view.swipe()
        }
        root.none_button.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            card_stack_view.swipe()
        }
        root.rewind_button.setOnClickListener {
            card_stack_view.rewind()
            var prefs: SharedPreferences? =
                context?.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
            var IDUser = prefs!!.getString("IDUser", "default")
            FirebaseDatabase.getInstance().getReference("UserLiked").child(IDUser!!).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var lastJob = 0L
                    for (job in p0.children){
                        if(lastJob == p0.childrenCount - 1){
                            job.ref.removeValue()
                        }
                        lastJob++
                    }
                }
            })
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SwipeViewModel::class.java)
        manager = CardStackLayoutManager(context, object : CardStackListener {
            override fun onCardDisappeared(
                view: View?,
                position: Int
            ) {// onCardAppeared là card biến mất
                val textView = view!!.findViewById<TextView>(R.id.title_job)
                Log.d(
                    "CardStackView",
                    "onDisCardAppeared: ${arrayListJobInAdapter.get(position).ID}"
                )
                jobLiked = arrayListJobInAdapter.get(position).ID
            }

            override fun onCardDragging(direction: Direction?, ratio: Float) { // Dragging card
                Log.e("CardStacks", "Dragging d=" + direction?.name + "ratio = " + ratio)
            }

            override fun onCardSwiped(direction: Direction?) {

                if (direction == Direction.Right) {
                    var prefs: SharedPreferences? =
                        context?.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
                    var IDUser = prefs!!.getString("IDUser", "default")
                    var databaseReference = FirebaseDatabase.getInstance().getReference("UserLiked")
                        .child(IDUser!!).child(createIDUser())
                    var hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("IDJob", jobLiked)
                    databaseReference.setValue(hashMap)
                }

                if (manager.getTopPosition() == cardStackAdapter.getItemCount() - 5) {
                }
            }

            override fun onCardCanceled() {
                Log.e("CardStacks", "onCardCanceled " + manager.topPosition)
            }

            override fun onCardAppeared(
                view: View?,
                position: Int
            ) { // onCardAppeared là card hiện lên
                val textView = view!!.findViewById<TextView>(R.id.title_job)
                Log.d("CardStackView", "onCardAppeared: ${arrayListJobInAdapter.get(position).ID}")

            }

            override fun onCardRewound() {
                Log.e("CardStacks", "onCardCanceled " + manager.topPosition)
            }
        })

        val setting = RewindAnimationSetting.Builder()
            .setDirection(Direction.Bottom)
            .setDuration(Duration.Normal.duration)
            .setInterpolator(DecelerateInterpolator())
            .build()

        manager.apply {
            this.setStackFrom(StackFrom.None)
            this.setVisibleCount(3)
            this.setTranslationInterval(1.0f)
            this.setScaleInterval(0.95f)
            this.setSwipeThreshold(0.3f)
            this.setMaxDegree(40.0f)
            this.setDirections(Direction.HORIZONTAL) // Chỉ cho phép swipe left và right
            this.setCanScrollHorizontal(true)
            this.setCanScrollVertical(true)
            this.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            this.setOverlayInterpolator(LinearInterpolator())
            this.setRewindAnimationSetting(setting)
        }

        card_stack_view.apply {
            this.adapter = cardStackAdapter
            this.layoutManager = manager
            this.itemAnimator = DefaultItemAnimator()
        }
    }

    fun addList() {
        var arrayList = ArrayList<SwipeModel>()

        var db = FirebaseDatabase.getInstance().getReference("RecommendJobs")
        db.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.childrenCount > 0) {
                    for (job in p0.children) {
                        var itemRecommendJobsModel = ItemRecommendJobsModel(job)
                        var swipeModel = SwipeModel(
                            itemRecommendJobsModel.id,
                            R.drawable.anh1,
                            itemRecommendJobsModel.title_job,
                            itemRecommendJobsModel.summary_job,
                            itemRecommendJobsModel.salary_job,
                            itemRecommendJobsModel.location_job
                        )
                        arrayList.add(swipeModel)
                    }
                    cardStackAdapter.setArrayListData(arrayList)
                } else {
                    arrayList.add(SwipeModel("ID", R.drawable.anh1, "Null", "Null", "Null", "Null"))
                }
            }
        })
    }

    private fun createIDUser(): String {
        var ID = ""
        var simpleDateFormat = SimpleDateFormat("yyyyMMddkkmmss")
        var date = Calendar.getInstance().time
        ID = simpleDateFormat.format(date)
        return "ul" + ID
    }
}
