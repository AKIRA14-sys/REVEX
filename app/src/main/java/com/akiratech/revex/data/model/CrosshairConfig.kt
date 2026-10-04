package com.akiratech.revex.data.model

enum class CrosshairTier(val label: String) {
    REGULAR("Regular"),
    PRO("Pro"),
    PREMIUM("Premium"),
    LEGENDARY("Legendary"),
    GOD("God"),
    RED_DOT("Red Dot"),
    SHARINGAN("Sharingan")
}

enum class CenterStyle {
    NONE,
    DOT,
    CROSS
}

data class CrosshairConfig(
    val id: String,
    val name: String,
    val tier: CrosshairTier = CrosshairTier.REGULAR,
    val colorArgb: Long = 0xFFFF2A2AL, // Bright Crimson / Red default
    val sizeDp: Float = 36f,
    val thicknessDp: Float = 3f,
    val gapDp: Float = 6f,
    val opacity: Float = 1.0f, // 0.0 - 1.0
    val rotationDeg: Float = 0f,
    // Sharingan & Center Spec:
    val isSharingan: Boolean = false,
    val sharinganType: Int = 1, // 1: 1-Tomoe, 2: 2-Tomoe, 3: 3-Tomoe, 4: Mangekyo
    val sharinganOpacity: Float = 1.0f, // 0.1 - 1.0
    val sharinganCenterEmpty: Boolean = true, // Force inner center empty/transparent
    val centerStyle: CenterStyle = CenterStyle.NONE, // Selectable center mark
    val centerColorArgb: Long = 0xFFFFD700L, // Gold default for center mark
    val centerSizeDp: Float = 4f,
    val gamePackageName: String? = null,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)
