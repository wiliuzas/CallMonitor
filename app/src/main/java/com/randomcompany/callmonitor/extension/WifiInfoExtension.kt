package com.randomcompany.callmonitor.extension

import android.net.wifi.WifiInfo

fun WifiInfo.getFormattedIpAddress(): String {
    return String.format("%d.%d.%d.%d", ipAddress and 0xff, ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff, ipAddress shr 24 and 0xff)
}

