package com.akiratech.revex

import com.akiratech.revex.data.model.BoostConfig
import com.akiratech.revex.data.model.CenterStyle
import com.akiratech.revex.data.model.CrosshairConfig
import com.akiratech.revex.data.model.CrosshairTier
import com.akiratech.revex.data.model.DeviceStats
import com.akiratech.revex.data.model.GameInfo
import org.junit.Assert.*
import org.junit.Test

class DataModelTest {

    @Test
    fun testGameInfoDefaults() {
        val game = GameInfo(packageName = "com.pubg.krmobile", name = "PUBG MOBILE")
        assertEquals("com.pubg.krmobile", game.packageName)
        assertEquals("PUBG MOBILE", game.name)
        assertTrue(game.isInstalled)
        assertTrue(game.isAutoBoostEnabled)
        assertFalse(game.isFavorite)
    }

    @Test
    fun testCrosshairConfigDefaults() {
        val config = CrosshairConfig(
            id = "test_id",
            name = "Test Crosshair"
        )
        assertEquals("test_id", config.id)
        assertEquals(CrosshairTier.REGULAR, config.tier)
        assertEquals(1.0f, config.opacity)
        assertEquals(CenterStyle.NONE, config.centerStyle)
        assertFalse(config.isSharingan)
    }

    @Test
    fun testBoostConfigDefaults() {
        val config = BoostConfig()
        assertTrue(config.autoFreeRam)
        assertTrue(config.gamePerformanceMode)
        assertTrue(config.sustainedPerformance)
        assertTrue(config.overlayHudEnabled)
        assertTrue(config.thermalGuardEnabled)
    }

    @Test
    fun testDeviceStatsDefaults() {
        val stats = DeviceStats()
        assertEquals(0f, stats.cpuUsagePercent)
        assertEquals(60, stats.fpsEstimate)
    }
}
