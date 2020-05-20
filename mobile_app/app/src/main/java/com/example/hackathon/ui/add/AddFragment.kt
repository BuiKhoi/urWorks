package com.example.hackathon.ui.add

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.hackathon.R
import com.example.hackathon.ui.activities.RegisterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.add_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
    }

    private lateinit var databaseJob: DatabaseReference

    private lateinit var viewModel: AddViewModel
    private lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var prefs: SharedPreferences? =
            context?.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
        var IDUser = prefs!!.getString("IDUser", "default")
        databaseJob = FirebaseDatabase.getInstance().getReference("RecommendJobs").child(IDUser!!).child(createIDJobs())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.add_fragment, container, false)
        // add for location input text
        val items_location = listOf(
            "Cần Thơ",
            "Đà Nẵng",
            "Hải Phòng",
            "Hà Nội",
            "TP HCM",
            "An Giang",
            "Bà Rịa - Vũng Tàu",
            "Bắc Giang",
            "Bắc Kạn",
            "Bạc Liêu",
            "Bắc Ninh",
            "Bến Tre",
            "Bình Định",
            "Bình Dương",
            "Bình Phước",
            "Bình Thuận",
            "Cà Mau",
            "Cao Bằng",
            "Đắk Lắk",
            "Đắk Nông",
            "Điện Biên",
            "Đồng Nai",
            "Đồng Tháp",
            "Gia Lai",
            "Hà Giang",
            "Hà Nam",
            "Hà Tĩnh",
            "Hải Dương",
            "Hậu Giang",
            "Hòa Bình",
            "Hưng Yên",
            "Khánh Hòa",
            "Kiên Giang",
            "Kon Tum",
            "Lai Châu",
            "Lâm Đồng",
            "Lạng Sơn",
            "Lào Cai",
            "Long An",
            "Nam Định",
            "Nghệ An",
            "Ninh Bình",
            "Ninh Thuận",
            "Phú Thọ",
            "Quảng Bình",
            "Quảng Nam",
            "Quảng Ngãi",
            "Quảng Ninh",
            "Quảng Trị",
            "Sóc Trăng",
            "Sơn La",
            "Tây Ninh",
            "Thái Bình",
            "Thái Nguyên",
            "Thanh Hóa",
            "Thừa Thiên Huế",
            "Tiền Giang",
            "Trà Vinh",
            "Tuyên Quang",
            "Vĩnh Long",
            "Vĩnh Phúc",
            "Yên Bái",
            "Phú Yên"
        )

        val adapter_location =
            ArrayAdapter(mContext!!, R.layout.item_function_register, items_location)
        (root.location_job as AutoCompleteTextView).setAdapter(adapter_location)

        // add for form work job
        val items_works = listOf(
            "Material",
            "Design",
            "Part Time",
            "Full Time"
        )
        
        val adapter_work = ArrayAdapter(mContext!!, R.layout.item_function_register, items_works)
        (root.form_work_job as AutoCompleteTextView).setAdapter(adapter_work)

        root.add_job_button.setOnClickListener {
            var title_job = root.title_job.text.toString().trim()
            var summary_job = root.summary_job.text.toString().trim()
            var salary_job = root.salary_job.text.toString().trim()
            var request_job = root.request_job.text.toString().trim()
            var phone_number_job = root.phone_number_job.text.toString().trim()
            var form_work_job = root.form_work_job.text.toString().trim()
            var location_job = root.location_job.text.toString().trim()

            if (title_job.equals("") ||
                summary_job.equals("") ||
                salary_job.equals("") ||
                request_job.equals("") ||
                phone_number_job.equals("") ||
                location_job.equals("") ||
                form_work_job.equals("")
            ) {
                Toast.makeText(context, "Can't empty value", Toast.LENGTH_LONG).show()
            } else {
                var hashMap: HashMap<String, String> = HashMap()
//                var prefs : SharedPreferences = mContext.getSharedPreferences("prefs", MODE_PRIVATE)
//                var IDUser = prefs.getString("IDUser", "default")

                hashMap.put("title_job", title_job)
                hashMap.put("summary_job", summary_job)
                hashMap.put("salary_job", salary_job)
                hashMap.put("request_job", request_job)
                hashMap.put("phone_number_job", phone_number_job)
                hashMap.put("form_work_job", form_work_job)
                hashMap.put("location_job", location_job)
                databaseJob.setValue(hashMap).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Added success", Toast.LENGTH_LONG).show()
                        root.title_job.text!!.clear()
                        root.summary_job.text!!.clear()
                        root.salary_job.text!!.clear()
                        root.request_job.text!!.clear()
                        root.phone_number_job.text!!.clear()
                        root.form_work_job.text!!.clear()
                        root.location_job.text!!.clear()
                    }
                }
            }
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onPause() {
        super.onPause()
        Log.e("Pause", "Pause")

        // enable item in bottom navigation
        val bottom_nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_nav!!.menu.findItem(R.id.des_add).isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        Log.e("Resume", "Resume")
        // disable item in bottom navigation
        val bottom_nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_nav!!.menu.findItem(R.id.des_add).isEnabled = false
    }

    fun createIDJobs(): String {
        var ID = ""
        var simpleDateFormat = SimpleDateFormat("yyyyMMddkkmmss")
        var date = Calendar.getInstance().time
        ID = simpleDateFormat.format(date)
        return "jb" + ID
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
