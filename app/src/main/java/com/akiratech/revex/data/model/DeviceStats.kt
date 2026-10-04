package com.akiratech.revex.data.model

data class DeviceStats(
    val cpuUsagePercent: Float = 0f,
    val ramUsedBytes: Long = 0L,
    val ramTotalBytes: Long = 0L,
    val ramUsagePercent: Float = 0f,
    val batteryLevelPercent: Int = 0,
    val batteryTempCelsius: Float = 0f,
    val batteryStatus: String = "Normal",
    val storageUsedBytes: Long = 0L,
    val storageTotalBytes: Long = 0L,
    val storageUsagePercent: Float = 0f,
    val currentPingMs: Int = 0,
    val jitterMs: Int = 0,
    val fpsEstimate: Int = 60,
    val timestamp: Long = System.currentTimeMillis()
)
