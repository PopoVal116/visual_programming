package com.example.visual_programming2.supp

data class PhoneData(
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
    val time: Long?,
    val accuracy: Float?,
    val cell_info: String,
    val traffic_rx: Long,
    val traffic_tx: Long
)