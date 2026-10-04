package com.akiratech.revex.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.akiratech.revex.data.model.BoostConfig
import com.akiratech.revex.data.model.CenterStyle
import com.akiratech.revex.data.model.CrosshairConfig
import com.akiratech.revex.data.model.CrosshairTier

class RevexPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "revex_settings"
        private const val KEY_ACTIVE_CROSSHAIR_ID = "active_crosshair_id"
        private const val KEY_ACTIVE_CROSSHAIR_TIER = "active_crosshair_tier"
        private const val KEY_CROSSHAIR_COLOR = "crosshair_color"
        private const val KEY_CROSSHAIR_SIZE = "crosshair_size"
        private const val KEY_CROSSHAIR_THICKNESS = "crosshair_thickness"
        private const val KEY_CROSSHAIR_GAP = "crosshair_gap"
        private const val KEY_CROSSHAIR_OPACITY = "crosshair_opacity"
        private const val KEY_CROSSHAIR_ROTATION = "crosshair_rotation"
        private const val KEY_SHARINGAN_OPACITY = "sharingan_opacity"
        private const val KEY_CENTER_STYLE = "center_style"
        private const val KEY_CENTER_COLOR = "center_color"
        private const val KEY_CENTER_SIZE = "center_size"

        private const val KEY_AUTO_RAM = "boost_auto_ram"
        private const val KEY_ENABLE_DND = "boost_enable_dnd"
        private const val KEY_PERF_MODE = "boost_perf_mode"
        private const val KEY_SUSTAINED_PERF = "boost_sustained_perf"
        private const val KEY_OVERLAY_HUD = "boost_overlay_hud"
        private const val KEY_BOOST_ON_LAUNCH = "boost_on_launch"
        private const val KEY_THERMAL_GUARD = "boost_thermal_guard"
        private const val KEY_TOUCH_BOOST = "boost_touch_boost"

        private const val KEY_LAUNCHED_GAMES_COUNT = "stats_launched_games_count"
        private const val KEY_TOTAL_RAM_FREED_MB = "stats_total_ram_freed_mb"
        private const val KEY_CUSTOM_GAMES_SET = "custom_games_set"
    }

    var activeCrosshairId: String
        get() = prefs.getString(KEY_ACTIVE_CROSSHAIR_ID, "sharingan_3_tomoe") ?: "sharingan_3_tomoe"
        set(value) = prefs.edit().putString(KEY_ACTIVE_CROSSHAIR_ID, value).apply()

    fun getBoostConfig(): BoostConfig {
        return BoostConfig(
            autoFreeRam = prefs.getBoolean(KEY_AUTO_RAM, true),
            enableDnd = prefs.getBoolean(KEY_ENABLE_DND, false),
            gamePerformanceMode = prefs.getBoolean(KEY_PERF_MODE, true),
            sustainedPerformance = prefs.getBoolean(KEY_SUSTAINED_PERF, true),
            overlayHudEnabled = prefs.getBoolean(KEY_OVERLAY_HUD, true),
            boostOnGameLaunch = prefs.getBoolean(KEY_BOOST_ON_LAUNCH, true),
            thermalGuardEnabled = prefs.getBoolean(KEY_THERMAL_GUARD, true),
            touchSensitivityBoost = prefs.getBoolean(KEY_TOUCH_BOOST, true)
        )
    }

    fun saveBoostConfig(config: BoostConfig) {
        prefs.edit()
            .putBoolean(KEY_AUTO_RAM, config.autoFreeRam)
            .putBoolean(KEY_ENABLE_DND, config.enableDnd)
            .putBoolean(KEY_PERF_MODE, config.gamePerformanceMode)
            .putBoolean(KEY_SUSTAINED_PERF, config.sustainedPerformance)
            .putBoolean(KEY_OVERLAY_HUD, config.overlayHudEnabled)
            .putBoolean(KEY_BOOST_ON_LAUNCH, config.boostOnGameLaunch)
            .putBoolean(KEY_THERMAL_GUARD, config.thermalGuardEnabled)
            .putBoolean(KEY_TOUCH_BOOST, config.touchSensitivityBoost)
            .apply()
    }

    fun getActiveCrosshairConfig(defaultConfig: CrosshairConfig): CrosshairConfig {
        val color = prefs.getLong(KEY_CROSSHAIR_COLOR, defaultConfig.colorArgb)
        val size = prefs.getFloat(KEY_CROSSHAIR_SIZE, defaultConfig.sizeDp)
        val thickness = prefs.getFloat(KEY_CROSSHAIR_THICKNESS, defaultConfig.thicknessDp)
        val gap = prefs.getFloat(KEY_CROSSHAIR_GAP, defaultConfig.gapDp)
        val opacity = prefs.getFloat(KEY_CROSSHAIR_OPACITY, defaultConfig.opacity)
        val rotation = prefs.getFloat(KEY_CROSSHAIR_ROTATION, defaultConfig.rotationDeg)
        val sharinganOpacity = prefs.getFloat(KEY_SHARINGAN_OPACITY, defaultConfig.sharinganOpacity)
        val centerStyleName = prefs.getString(KEY_CENTER_STYLE, defaultConfig.centerStyle.name) ?: defaultConfig.centerStyle.name
        val centerStyle = try { CenterStyle.valueOf(centerStyleName) } catch (e: Exception) { CenterStyle.NONE }
        val centerColor = prefs.getLong(KEY_CENTER_COLOR, defaultConfig.centerColorArgb)
        val centerSize = prefs.getFloat(KEY_CENTER_SIZE, defaultConfig.centerSizeDp)

        return defaultConfig.copy(
            colorArgb = color,
            sizeDp = size,
            thicknessDp = thickness,
            gapDp = gap,
            opacity = opacity,
            rotationDeg = rotation,
            sharinganOpacity = sharinganOpacity,
            centerStyle = centerStyle,
            centerColorArgb = centerColor,
            centerSizeDp = centerSize
        )
    }

    fun saveCrosshairConfig(config: CrosshairConfig) {
        prefs.edit()
            .putString(KEY_ACTIVE_CROSSHAIR_ID, config.id)
            .putLong(KEY_CROSSHAIR_COLOR, config.colorArgb)
            .putFloat(KEY_CROSSHAIR_SIZE, config.sizeDp)
            .putFloat(KEY_CROSSHAIR_THICKNESS, config.thicknessDp)
            .putFloat(KEY_CROSSHAIR_GAP, config.gapDp)
            .putFloat(KEY_CROSSHAIR_OPACITY, config.opacity)
            .putFloat(KEY_CROSSHAIR_ROTATION, config.rotationDeg)
            .putFloat(KEY_SHARINGAN_OPACITY, config.sharinganOpacity)
            .putString(KEY_CENTER_STYLE, config.centerStyle.name)
            .putLong(KEY_CENTER_COLOR, config.centerColorArgb)
            .putFloat(KEY_CENTER_SIZE, config.centerSizeDp)
            .apply()
    }

    fun recordGameLaunch() {
        val current = prefs.getInt(KEY_LAUNCHED_GAMES_COUNT, 0)
        prefs.edit().putInt(KEY_LAUNCHED_GAMES_COUNT, current + 1).apply()
    }

    fun getLaunchedGamesCount(): Int = prefs.getInt(KEY_LAUNCHED_GAMES_COUNT, 0)

    fun addRamFreedMb(mb: Int) {
        val current = prefs.getInt(KEY_TOTAL_RAM_FREED_MB, 0)
        prefs.edit().putInt(KEY_TOTAL_RAM_FREED_MB, current + mb).apply()
    }

    fun getTotalRamFreedMb(): Int = prefs.getInt(KEY_TOTAL_RAM_FREED_MB, 0)

    fun getCustomGames(): Set<String> {
        return prefs.getStringSet(KEY_CUSTOM_GAMES_SET, emptySet()) ?: emptySet()
    }

    fun addCustomGame(packageName: String) {
        val set = getCustomGames().toMutableSet()
        set.add(packageName)
        prefs.edit().putStringSet(KEY_CUSTOM_GAMES_SET, set).apply()
    }

    fun removeCustomGame(packageName: String) {
        val set = getCustomGames().toMutableSet()
        set.remove(packageName)
        prefs.edit().putStringSet(KEY_CUSTOM_GAMES_SET, set).apply()
    }
}
