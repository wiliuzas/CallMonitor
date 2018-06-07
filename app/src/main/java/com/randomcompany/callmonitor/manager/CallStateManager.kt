package com.randomcompany.callmonitor.manager

import com.randomcompany.callmonitor.model.CallInfo
import com.randomcompany.callmonitor.model.CallLog

interface CallStateManager {
    fun startListening()
    fun stopListening()
    fun getCurrentCallStatus(): CallInfo?
    fun getCallLog(): List<CallLog>
}