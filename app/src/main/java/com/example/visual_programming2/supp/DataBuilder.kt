package com.example.visual_programming2.supp

import android.content.pm.PackageManager
import android.location.Location
import android.telephony.TelephonyManager
import android.net.TrafficStats
import com.example.visual_programming2.data.DeviceData
import com.example.visual_programming2.data.LocationRecord

object DataBuilder {

    fun buildAllData(
        location: Location,
        telephonyManager: TelephonyManager,
        packageManager: PackageManager
    ): DeviceData {
        val cellInfo = CellDataCollector.collectCellInfo(telephonyManager)
        val txBytes = TrafficStats.getTotalTxBytes()
        val rxBytes = TrafficStats.getTotalRxBytes()
        val topApps = AppsCollector.collectTopApps(packageManager)
        return DeviceData(
            lat = location.latitude,
            lon = location.longitude,
            alt = location.altitude,
            timestamp = location.time,
            accuracy = location.accuracy,
            cellInfo = cellInfo,
            txBytes = txBytes,
            rxBytes = rxBytes,
            topApps = topApps
        )
        }

    fun recordToString(record: DeviceData): String {
        return """
            LOC:${record.lat},${record.lon},${record.alt},${record.timestamp},${record.accuracy};
            CELL:${record.cellInfo};
            TRAFFIC:${record.txBytes},${record.rxBytes};
            TOP_APPS:${record.topApps}
        """.trimIndent()
    }
        fun toLocationRecord(deviceData: DeviceData): LocationRecord {
        return LocationRecord(
            lat = deviceData.lat,
            lon = deviceData.lon,
            alt = deviceData.alt,
            timestamp = deviceData.timestamp
        )
    }
}
