package com.example.visual_programming2.data

data class DeviceData(
    val lat: Double,
    val lon: Double,
    val alt: Double,
    val timestamp: Long,
    val accuracy: Float,
    val cellInfo: String,
    val txBytes: Long,
    val rxBytes: Long,
    val topApps: String
)