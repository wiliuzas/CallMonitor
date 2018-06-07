package com.randomcompany.callmonitor.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import com.randomcompany.callmonitor.MainActivity.Companion.SERVER_STOPPED
import com.randomcompany.callmonitor.R
import com.randomcompany.callmonitor.extension.getFormattedIpAddress
import com.randomcompany.callmonitor.manager.CallStateManagerImpl
import com.randomcompany.callmonitor.server.Server
import com.randomcompany.callmonitor.server.Server.Companion.PORT


class HttpService : Service() {

    private val server by lazy {
        Server(getIpAddress(), CallStateManagerImpl(applicationContext))
    }

    override fun onCreate() {
        super.onCreate()
        showCloseNotification()
        server.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(SERVER_STOPPED))
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.getInt(SERVICE_COMMAND)?.let {
            when (it) {
                STOP_SERVER -> stopSelf()
            }
        }
        return START_STICKY
    }

    private fun showCloseNotification() {
        val intent = Intent(baseContext, HttpService::class.java)
        intent.putExtra(SERVICE_COMMAND, STOP_SERVER)
        val pendingIntent = PendingIntent.getService(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        createNotificationChannel()
        val builder = NotificationCompat.Builder(baseContext, CHANNEL_ID)
                .setContentTitle(baseContext.getString(R.string.server_running))
                .setContentText(baseContext.getString(R.string.ip_port, getIpAddress(), PORT))
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(baseContext, R.color.colorAccent))
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        baseContext.getString(R.string.stop_server), pendingIntent)
                .setOngoing(true)

        startForeground(NOTIFICATION_ID, builder.build())
    }

    private fun getIpAddress(): String {
        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wm.connectionInfo.getFormattedIpAddress()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "chanel_name"
            val description = "chanel_description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val STOP_SERVER = 0
        private const val SERVICE_COMMAND = "service_command"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "http_chanel"

        fun startService(context: Context) {
            context.startService(Intent(context, HttpService::class.java))
        }
    }


}