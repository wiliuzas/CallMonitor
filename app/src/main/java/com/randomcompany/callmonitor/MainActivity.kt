package com.randomcompany.callmonitor

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.randomcompany.callmonitor.extension.getFormattedIpAddress
import com.randomcompany.callmonitor.server.Server.Companion.PORT
import com.randomcompany.callmonitor.services.HttpService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val PERMISSIONS_REQUEST = 10

    private val permissions = arrayOf(Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE)

    private val localBroadcastReceiver by lazy {
        LocalBroadcastManager.getInstance(this)
    }
    var serviceBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == SERVER_STOPPED) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        textIp.text = getString(R.string.ip_port, wm.connectionInfo.getFormattedIpAddress(), PORT)

        if (isPermissionsGranted()) {
            grantPermissionsButton.visibility = View.GONE
            startCallService()
        } else {
            grantPermissionsButton.visibility = View.VISIBLE
            grantPermissions()
        }

        val mIntentFilter = IntentFilter()
        mIntentFilter.addAction(SERVER_STOPPED)
        localBroadcastReceiver.registerReceiver(serviceBroadcastReceiver, mIntentFilter)
    }

    override fun onDestroy() {
        localBroadcastReceiver.unregisterReceiver(serviceBroadcastReceiver)
        super.onDestroy()
    }

    private fun startCallService() {
        HttpService.startService(applicationContext)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onStartServiceClick(view: View) {
        grantPermissions()
    }

    private fun isPermissionsGranted(): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun grantPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (isPermissionsGranted()) {
                grantPermissionsButton.visibility = View.GONE
                startCallService()
            }
            return
        }
    }

    companion object {
        const val SERVER_STOPPED = "server_stopped"
    }
}
