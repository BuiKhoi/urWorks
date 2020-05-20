package com.example.hackathon.data.register.FaceID

import com.example.hackathon.data.register.FaceID.ImageFaceIDLink
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    // API to send two image for khoi
    //https://api.openweathermap.org/data/2.5/weather?lat={lat}?lon={lon}&appid={KEYID}&units=metric
    //http://192.168.1.24:8000/
    @GET("/test_api")
    fun postImageURL(
        @Query("img") image_face_url: String,
        @Query("cmt") image_cmnd_url : String
    ) : Call<ImageFaceIDLink>
}