package com.randomcompany.callmonitor.response

import com.google.gson.annotations.SerializedName

data class ServicesResponse(
        @SerializedName("start") val start: String,
        @SerializedName("services") val services: List<ServiceResponse>)