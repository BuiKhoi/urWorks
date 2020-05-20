package com.example.hackathon.data.register.FaceID

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ImageFaceIDLink(
    @SerializedName("likely") @Expose var likely : String,
    @SerializedName("approve") @Expose var approve : String
)