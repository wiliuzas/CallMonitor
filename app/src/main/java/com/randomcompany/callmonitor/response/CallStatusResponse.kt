package com.randomcompany.callmonitor.response

import com.google.gson.annotations.SerializedName

data class CallStatusResponse(
        @SerializedName("number") val number: String? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("ongoing") val ongoing: Boolean = true)