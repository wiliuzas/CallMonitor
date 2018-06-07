package com.randomcompany.callmonitor.model

class CallLog(phoneNumber: String, name: String) : CallInfo(phoneNumber, name) {
    var timesQueried: Int = 0
    var duration: Long = 0
}
