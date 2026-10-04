package com.akiratech.revex

import com.akiratech.revex.data.crosshair.CrosshairGenerator
import com.akiratech.revex.data.model.CenterStyle
import com.akiratech.revex.data.model.CrosshairTier
import org.junit.Assert.*
import org.junit.Test

class CrosshairGeneratorTest {

    @Test
    fun testCrosshairGeneratorCount() {
        val crosshairs = CrosshairGenerator.generateAllCrosshairs()
        assertTrue("Expected 500+ crosshairs generated", crosshairs.size >= 500)
    }

    @Test
    fun testSharinganCrosshairRules() {
        val sharinganPresets = CrosshairGenerator.getSharinganPresets()
        assertEquals(4, sharinganPresets.size)

        for (config in sharinganPresets) {
            assertTrue("Sharingan config must set isSharingan true", config.isSharingan)
            assertTrue("Sharingan config must enforce transparent/empty center", config.sharinganCenterEmpty)
            assertTrue("Sharingan opacity must be > 0", config.sharinganOpacity in 0.1f..1.0f)
            assertEquals("Sharingan config must belong to SHARINGAN tier", CrosshairTier.SHARINGAN, config.tier)
        }
    }

    @Test
    fun testGodTierSharinganRules() {
        val crosshairs = CrosshairGenerator.generateAllCrosshairs()
        val godCrosshairs = crosshairs.filter { it.tier == CrosshairTier.GOD }
        assertEquals(100, godCrosshairs.size)

        for (config in godCrosshairs) {
            assertTrue("God tier crosshairs must set isSharingan true", config.isSharingan)
            assertTrue("God tier crosshairs must enforce transparent/empty center", config.sharinganCenterEmpty)
            assertTrue("God tier center mark must be DOT or CROSS", config.centerStyle == CenterStyle.DOT || config.centerStyle == CenterStyle.CROSS)
        }
    }

    @Test
    fun testPerGameLibraryGeneration() {
        val gameCrosshairs = CrosshairGenerator.generateCrosshairsForGame("com.pubg.krmobile")
        assertEquals("Expected 100 crosshairs for game", 100, gameCrosshairs.size)
        assertTrue("Game package name must be attached", gameCrosshairs.all { it.gamePackageName == "com.pubg.krmobile" })
    }

    @Test
    fun testTiersDistribution() {
        val crosshairs = CrosshairGenerator.generateAllCrosshairs()
        val byTier = crosshairs.groupBy { it.tier }

        assertTrue(byTier.containsKey(CrosshairTier.SHARINGAN))
        assertTrue(byTier.containsKey(CrosshairTier.RED_DOT))
        assertTrue(byTier.containsKey(CrosshairTier.REGULAR))
        assertTrue(byTier.containsKey(CrosshairTier.PRO))
        assertTrue(byTier.containsKey(CrosshairTier.PREMIUM))
        assertTrue(byTier.containsKey(CrosshairTier.LEGENDARY))
        assertTrue(byTier.containsKey(CrosshairTier.GOD))

        assertEquals(100, byTier[CrosshairTier.REGULAR]?.size)
        assertEquals(100, byTier[CrosshairTier.PRO]?.size)
        assertEquals(100, byTier[CrosshairTier.PREMIUM]?.size)
        assertEquals(100, byTier[CrosshairTier.LEGENDARY]?.size)
        assertEquals(100, byTier[CrosshairTier.GOD]?.size)
    }
}
