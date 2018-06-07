package com.randomcompany.callmonitor.model

import java.util.*

open class CallInfo(val phoneNumber: String, val name: String) {
    val callStartDate: Date = Date()
}