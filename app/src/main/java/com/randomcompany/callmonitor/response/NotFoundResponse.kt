package com.randomcompany.callmonitor.response

import com.google.gson.annotations.SerializedName

data class NotFoundResponse(
        @SerializedName("status") val status: Int,
        @SerializedName("error_message") val errorMessage: String)