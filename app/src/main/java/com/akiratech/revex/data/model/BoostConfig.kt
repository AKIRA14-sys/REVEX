package com.akiratech.revex.data.model

data class BoostConfig(
    val autoFreeRam: Boolean = true,
    val enableDnd: Boolean = false,
    val gamePerformanceMode: Boolean = true,
    val sustainedPerformance: Boolean = true,
    val overlayHudEnabled: Boolean = true,
    val boostOnGameLaunch: Boolean = true,
    val thermalGuardEnabled: Boolean = true,
    val touchSensitivityBoost: Boolean = true
)
