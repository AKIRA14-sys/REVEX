package com.akiratech.revex.data.crosshair

import com.akiratech.revex.data.model.CenterStyle
import com.akiratech.revex.data.model.CrosshairConfig
import com.akiratech.revex.data.model.CrosshairTier

object CrosshairGenerator {

    private val colorPalettes = listOf(
        0xFFFF2A2AL, // Crimson Red
        0xFF00FFCCL, // Cyan
        0xFF00FF66L, // Toxic Green
        0xFFFFD700L, // Gold
        0xFFFF00FFL, // Neon Pink
        0xFF0099FFL, // Electric Blue
        0xFFFFFFFFL, // Crisp White
        0xFFFF8800L  // Amber Orange
    )

    fun generateAllCrosshairs(): List<CrosshairConfig> {
        val list = mutableListOf<CrosshairConfig>()

        // 1. SHARINGAN TIER (Hero presets)
        list.addAll(getSharinganPresets())

        // 2. RED DOT TIER (20 presets)
        for (i in 1..20) {
            val color = colorPalettes[(i - 1) % colorPalettes.size]
            list.add(
                CrosshairConfig(
                    id = "red_dot_$i",
                    name = "Red Dot Spec $i",
                    tier = CrosshairTier.RED_DOT,
                    colorArgb = color,
                    sizeDp = 12f + (i % 6) * 2f,
                    thicknessDp = 2f + (i % 3) * 1f,
                    gapDp = 0f,
                    opacity = 1.0f,
                    centerStyle = CenterStyle.DOT,
                    centerColorArgb = color,
                    centerSizeDp = 4f + (i % 4) * 2f
                )
            )
        }

        // 3. REGULAR TIER (100 presets)
        for (i in 1..100) {
            val color = colorPalettes[(i - 1) % colorPalettes.size]
            list.add(
                CrosshairConfig(
                    id = "regular_$i",
                    name = "Classic Cross $i",
                    tier = CrosshairTier.REGULAR,
                    colorArgb = color,
                    sizeDp = 24f + (i % 10) * 2f,
                    thicknessDp = 2f + (i % 4) * 0.5f,
                    gapDp = 2f + (i % 5) * 2f,
                    opacity = 0.9f
                )
            )
        }

        // 4. PRO TIER (100 presets)
        for (i in 1..100) {
            val color = colorPalettes[(i * 3) % colorPalettes.size]
            val center = if (i % 2 == 0) CenterStyle.DOT else CenterStyle.NONE
            list.add(
                CrosshairConfig(
                    id = "pro_$i",
                    name = "Tactical Pro $i",
                    tier = CrosshairTier.PRO,
                    colorArgb = color,
                    sizeDp = 20f + (i % 8) * 2f,
                    thicknessDp = 2.5f + (i % 3) * 0.5f,
                    gapDp = 1f + (i % 4) * 1.5f,
                    opacity = 1.0f,
                    centerStyle = center,
                    centerColorArgb = 0xFFFFD700L,
                    centerSizeDp = 3f
                )
            )
        }

        // 5. PREMIUM TIER (100 presets)
        for (i in 1..100) {
            val color = colorPalettes[(i * 5) % colorPalettes.size]
            val rotation = (i % 4) * 45f
            list.add(
                CrosshairConfig(
                    id = "premium_$i",
                    name = "Apex Elite $i",
                    tier = CrosshairTier.PREMIUM,
                    colorArgb = color,
                    sizeDp = 28f + (i % 12) * 2f,
                    thicknessDp = 3f,
                    gapDp = 3f + (i % 6) * 1.5f,
                    opacity = 1.0f,
                    rotationDeg = rotation,
                    centerStyle = CenterStyle.CROSS,
                    centerColorArgb = color,
                    centerSizeDp = 4f
                )
            )
        }

        // 6. LEGENDARY TIER (100 presets)
        for (i in 1..100) {
            val color = colorPalettes[(i * 7) % colorPalettes.size]
            list.add(
                CrosshairConfig(
                    id = "legendary_$i",
                    name = "Mithras Legendary $i",
                    tier = CrosshairTier.LEGENDARY,
                    colorArgb = color,
                    sizeDp = 32f + (i % 10) * 2f,
                    thicknessDp = 3.5f,
                    gapDp = 4f + (i % 5) * 2f,
                    opacity = 1.0f,
                    rotationDeg = (i % 8) * 22.5f,
                    centerStyle = CenterStyle.DOT,
                    centerColorArgb = 0xFF00FFCCL,
                    centerSizeDp = 5f
                )
            )
        }

        // 7. GOD TIER (100 Sharingan-inspired presets with empty inner center)
        for (i in 1..100) {
            val color = colorPalettes[(i * 11) % colorPalettes.size]
            val sharinganType = ((i - 1) % 4) + 1
            val centerStyle = if (i % 2 == 0) CenterStyle.DOT else CenterStyle.CROSS
            list.add(
                CrosshairConfig(
                    id = "god_$i",
                    name = "Amaterasu God $i",
                    tier = CrosshairTier.GOD,
                    colorArgb = color,
                    sizeDp = 38f + (i % 8) * 2f,
                    thicknessDp = 4f,
                    gapDp = 5f + (i % 4) * 2f,
                    opacity = 1.0f,
                    rotationDeg = (i % 2) * 45f,
                    isSharingan = true,
                    sharinganType = sharinganType,
                    sharinganOpacity = 1.0f,
                    sharinganCenterEmpty = true,
                    centerStyle = centerStyle,
                    centerColorArgb = 0xFFFF2A2AL,
                    centerSizeDp = 5f
                )
            )
        }

        return list
    }

    fun getSharinganPresets(): List<CrosshairConfig> {
        return listOf(
            CrosshairConfig(
                id = "sharingan_1_tomoe",
                name = "Single Tomoe Sharingan",
                tier = CrosshairTier.SHARINGAN,
                colorArgb = 0xFFFF2A2AL,
                sizeDp = 40f,
                thicknessDp = 3f,
                gapDp = 8f,
                opacity = 1.0f,
                isSharingan = true,
                sharinganType = 1,
                sharinganOpacity = 1.0f,
                sharinganCenterEmpty = true,
                centerStyle = CenterStyle.DOT,
                centerColorArgb = 0xFFFFD700L,
                centerSizeDp = 4f
            ),
            CrosshairConfig(
                id = "sharingan_2_tomoe",
                name = "Double Tomoe Sharingan",
                tier = CrosshairTier.SHARINGAN,
                colorArgb = 0xFFFF2A2AL,
                sizeDp = 44f,
                thicknessDp = 3.5f,
                gapDp = 8f,
                opacity = 1.0f,
                isSharingan = true,
                sharinganType = 2,
                sharinganOpacity = 1.0f,
                sharinganCenterEmpty = true,
                centerStyle = CenterStyle.DOT,
                centerColorArgb = 0xFFFF2A2AL,
                centerSizeDp = 4f
            ),
            CrosshairConfig(
                id = "sharingan_3_tomoe",
                name = "Three Tomoe Sharingan",
                tier = CrosshairTier.SHARINGAN,
                colorArgb = 0xFFFF2A2AL,
                sizeDp = 48f,
                thicknessDp = 4f,
                gapDp = 10f,
                opacity = 1.0f,
                isSharingan = true,
                sharinganType = 3,
                sharinganOpacity = 1.0f,
                sharinganCenterEmpty = true,
                centerStyle = CenterStyle.DOT,
                centerColorArgb = 0xFFFFD700L,
                centerSizeDp = 4f
            ),
            CrosshairConfig(
                id = "sharingan_mangekyo",
                name = "Mangekyō Sharingan",
                tier = CrosshairTier.SHARINGAN,
                colorArgb = 0xFFFF2A2AL,
                sizeDp = 52f,
                thicknessDp = 4.5f,
                gapDp = 12f,
                opacity = 1.0f,
                isSharingan = true,
                sharinganType = 4,
                sharinganOpacity = 1.0f,
                sharinganCenterEmpty = true,
                centerStyle = CenterStyle.CROSS,
                centerColorArgb = 0xFFFFD700L,
                centerSizeDp = 5f
            )
        )
    }

    fun generateCrosshairsForGame(packageName: String): List<CrosshairConfig> {
        val list = mutableListOf<CrosshairConfig>()
        for (i in 1..100) {
            val color = colorPalettes[(i * 3) % colorPalettes.size]
            val isSharingan = i <= 20
            val centerStyle = if (i % 2 == 0) CenterStyle.DOT else CenterStyle.CROSS
            list.add(
                CrosshairConfig(
                    id = "game_${packageName.replace('.', '_')}_$i",
                    name = "Curated $i for $packageName",
                    tier = if (isSharingan) CrosshairTier.SHARINGAN else CrosshairTier.PRO,
                    colorArgb = color,
                    sizeDp = 24f + (i % 10) * 2f,
                    thicknessDp = 3f,
                    gapDp = 4f,
                    opacity = 1.0f,
                    isSharingan = isSharingan,
                    sharinganType = ((i - 1) % 4) + 1,
                    sharinganOpacity = 1.0f,
                    sharinganCenterEmpty = isSharingan,
                    centerStyle = centerStyle,
                    centerColorArgb = 0xFFFFD700L,
                    centerSizeDp = 4f,
                    gamePackageName = packageName
                )
            )
        }
        return list
    }
}
