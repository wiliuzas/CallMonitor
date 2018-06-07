package com.randomcompany.callmonitor.server

import com.google.gson.GsonBuilder
import com.randomcompany.callmonitor.manager.CallStateManager
import com.randomcompany.callmonitor.response.*
import fi.iki.elonen.NanoHTTPD
import java.text.SimpleDateFormat
import java.util.*


class Server(val ipAddress: String, val callStateManager: CallStateManager) : NanoHTTPD(PORT) {
    var serverStartDate: Date? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US)
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val serviceList = arrayListOf(getService("status"), getService("log"))

    override fun start() {
        super.start()
        serverStartDate = Date()
        callStateManager.startListening()
    }

    override fun stop() {
        super.stop()
        callStateManager.stopListening()
    }

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            ROOT_PATH -> {
                getRootResponse()
            }
            LOG_PATH -> {
                getLogsResponse()
            }
            STATUS_PATH -> {
                getCurrentStatusResponse()
            }
            else -> {
                getNotFoundResponse()
            }
        }
    }

    private fun getFormattedDate(date: Date?): String {
        date?.let {
            return dateFormat.format(date)
        } ?: return ""
    }

    private fun getRootResponse(): Response {
        val services = ServicesResponse(getFormattedDate(serverStartDate), serviceList)
        return getResponse(Response.Status.OK, gson.toJson(services))
    }

    private fun getLogsResponse(): Response {
        val callLogs = arrayListOf<CallLogResponse>()
        callStateManager.getCallLog().forEach {
            val callLog = CallLogResponse(getFormattedDate(it.callStartDate),
                    it.duration.toString(), it.phoneNumber, it.name, it.timesQueried)
            callLogs.add(callLog)
        }
        return getResponse(Response.Status.OK, gson.toJson(callLogs))
    }

    private fun getCurrentStatusResponse(): Response {
        val currentCallInfo = callStateManager.getCurrentCallStatus()
        val callStatus = if (currentCallInfo != null) {
            CallStatusResponse(currentCallInfo.phoneNumber, currentCallInfo.name)
        } else {
            CallStatusResponse(ongoing = false)
        }
        return getResponse(Response.Status.OK, gson.toJson(callStatus))
    }

    private fun getService(serviceName: String): ServiceResponse {
        return ServiceResponse(serviceName, "http://$ipAddress:$PORT/$serviceName")
    }

    private fun getNotFoundResponse(): Response {
        val notFoundResponse = NotFoundResponse(Response.Status.NOT_FOUND.requestStatus, "Not found")
        return getResponse(Response.Status.NOT_FOUND, gson.toJson(notFoundResponse))
    }

    private fun getResponse(status: Response.Status, response: String): Response {
        return NanoHTTPD.newFixedLengthResponse(status, "application/json", response)
    }

    companion object {
        const val PORT = 12345

        private const val ROOT_PATH = "/"
        private const val STATUS_PATH = "/status"
        private const val LOG_PATH = "/log"
    }
}