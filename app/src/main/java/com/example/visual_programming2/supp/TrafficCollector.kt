package com.example.visual_programming2.supp

import android.net.TrafficStats

object TrafficCollector {
    fun collectTrafficInfo(): String {
        val result = StringBuilder()

        val txBytes = TrafficStats.getTotalTxBytes()
        val rxBytes = TrafficStats.getTotalRxBytes()

        result.append("\nTRAFFIC\n")
        result.append("TX_BYTES=${txBytes}\n")
        result.append("RX_BYTES=${rxBytes}\n")
        result.append("TX_MB=${txBytes / 1024 / 1024}\n")
        result.append("RX_MB=${rxBytes / 1024 / 1024}\n")
        return result.toString()
    }
}