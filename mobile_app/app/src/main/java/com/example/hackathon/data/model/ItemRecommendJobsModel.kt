package com.example.hackathon.data.model

import com.google.firebase.database.DataSnapshot
import java.lang.Exception

class ItemRecommendJobsModel(dataSnapshot: DataSnapshot) {
    var id: String = "null"
    var form_work_job: String = "default"
    var location_job: String = "default"
    var title_job: String = "default"
    var summary_job: String = "default"
    var salary_job: String = "default"
    var request_job: String = "default"
    var phone_number_job: String = "default"

    init {
        try {
            val data: HashMap<String, String> = dataSnapshot.value as HashMap<String, String>
            id = dataSnapshot.key ?: ""
            form_work_job = data["form_work_job"] as String
            title_job = data["title_job"] as String
            summary_job = data["summary_job"] as String
            salary_job = data["salary_job"] as String
            location_job = data["location_job"] as String
            request_job = data["request_job"] as String
            phone_number_job = data["phone_number_job"] as String
        } catch (e: Exception) {
            e.message
        }
    }
}