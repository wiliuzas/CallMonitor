package com.randomcompany.callmonitor.response

import com.google.gson.annotations.SerializedName

data class ServiceResponse(
        @SerializedName("name") val name: String,
        @SerializedName("uri") val uri: String)