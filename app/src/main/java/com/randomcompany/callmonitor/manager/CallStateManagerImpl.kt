package com.randomcompany.callmonitor.manager

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.*
import com.randomcompany.callmonitor.model.CallInfo
import com.randomcompany.callmonitor.model.CallLog
import java.util.*

class CallStateManagerImpl(private val context: Context) : CallStateManager {

    private var currentCallStatus: CallInfo? = null
    private var callLogMap: HashMap<String, CallLog> = HashMap()
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    override fun startListening() {
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun stopListening() {
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE)
    }

    override fun getCurrentCallStatus(): CallInfo? {
        return currentCallStatus
    }

    override fun getCallLog(): List<CallLog> {
        return callLogMap.values.toList()
    }

    private val phoneListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String?) {
            if (incomingNumber != null && !incomingNumber.isBlank()) {
                when (state) {
                    CALL_STATE_IDLE -> {
                        currentCallStatus?.let {
                            ongoingCallFinished(it)
                            currentCallStatus = null
                        }
                    }
                    CALL_STATE_OFFHOOK -> {
                        currentCallStatus = CallInfo(incomingNumber, getNameByNumber(incomingNumber))
                    }
                    CALL_STATE_RINGING -> {
                        currentCallStatus = null
                    }
                }
                println("asd " + incomingNumber + "state " + state + "name " + getNameByNumber(incomingNumber))
            } else {
                println("state " + state)
            }
            super.onCallStateChanged(state, incomingNumber)
        }
    }

    private fun ongoingCallFinished(callInfo: CallInfo) {
        val callDuration = getCallDuration(callInfo.callStartDate, Date())
        var callLog = callLogMap[callInfo.phoneNumber]
        if (callLog == null) {
            callLog = CallLog(callInfo.phoneNumber, callInfo.name)
        }
        callLog.timesQueried++
        callLog.duration += callDuration

        callLogMap.put(callInfo.phoneNumber, callLog)
    }

    private fun getCallDuration(callStartDate: Date, callEndDate: Date): Long {
        return (callEndDate.time - callStartDate.time) / 1000
    }

    private fun getNameByNumber(phoneNumber: String): String {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        var contactName = "unknown"
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }

        return contactName
    }
}