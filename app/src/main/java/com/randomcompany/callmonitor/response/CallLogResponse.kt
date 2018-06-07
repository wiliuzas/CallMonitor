package com.randomcompany.callmonitor.response

import com.google.gson.annotations.SerializedName

data class CallLogResponse(
        @SerializedName("beginning") val beginning: String,
        @SerializedName("duration") val duration: String,
        @SerializedName("number") val number: String,
        @SerializedName("name") val name: String,
        @SerializedName("timesQueried") val timesQueried: Int)