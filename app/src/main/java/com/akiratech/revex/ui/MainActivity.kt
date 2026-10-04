package com.akiratech.revex.ui

import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crosshair
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.akiratech.revex.booster.BoostResult
import com.akiratech.revex.booster.LagBoosterManager
import com.akiratech.revex.data.crosshair.CrosshairGenerator
import com.akiratech.revex.data.model.BoostConfig
import com.akiratech.revex.data.model.CrosshairConfig
import com.akiratech.revex.data.model.DeviceStats
import com.akiratech.revex.data.model.GameInfo
import com.akiratech.revex.data.pref.RevexPreferences
import com.akiratech.revex.monitor.DeviceMonitorManager
import com.akiratech.revex.service.OverlayService
import com.akiratech.revex.ui.screens.*
import com.akiratech.revex.ui.theme.REVEXTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var prefs: RevexPreferences
    private lateinit var boosterManager: LagBoosterManager
    private lateinit var monitorManager: DeviceMonitorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()

        prefs = RevexPreferences(this)
        boosterManager = LagBoosterManager(this)
        monitorManager = DeviceMonitorManager(this)

        setContent {
            REVEXTheme {
                MainAppContainer()
            }
        }
    }

    @Composable
    private fun MainAppContainer() {
        val pagerState = rememberPagerState(pageCount = { 5 })
        val coroutineScope = rememberCoroutineScope()

        var deviceStats by remember { mutableStateOf(DeviceStats()) }
        var isBoosting by remember { mutableStateOf(false) }
        var boostProgress by remember { mutableFloatStateOf(0f) }
        var boostResult by remember { mutableStateOf<BoostResult?>(null) }
        var boostConfig by remember { mutableStateOf(prefs.getBoostConfig()) }

        val defaultCrosshair = remember { CrosshairGenerator.getSharinganPresets()[2] }
        var activeCrosshairConfig by remember { mutableStateOf(prefs.getActiveCrosshairConfig(defaultCrosshair)) }

        var gamesList by remember { mutableStateOf(getDetectedGamesList()) }
        var selectedGame by remember { mutableStateOf(gamesList.firstOrNull()) }
        var showPermissionOnboarding by remember { mutableStateOf(!hasAllPermissions()) }

        // Observe device stats in background
        LaunchedEffect(Unit) {
            monitorManager.observeDeviceStats(1000L).collectLatest { stats ->
                deviceStats = stats
            }
        }

        // Auto-start Overlay Service if permission granted and enabled
        LaunchedEffect(boostConfig.overlayHudEnabled) {
            if (boostConfig.overlayHudEnabled && Settings.canDrawOverlays(this@MainActivity)) {
                OverlayService.start(this@MainActivity)
            }
        }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    selectedPage = pagerState.currentPage,
                    pageCount = 5,
                    onSelectPage = { page ->
                        coroutineScope.launch { pagerState.animateScrollToPage(page) }
                    }
                )
            },
            containerColor = Color(0xFF0A0A0A)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> HomeScreen(
                            stats = deviceStats,
                            isBoosting = isBoosting,
                            boostProgress = boostProgress,
                            boostResult = boostResult,
                            selectedGame = selectedGame,
                            gamesList = gamesList,
                            onSelectGame = { selectedGame = it },
                            onExecuteBoostAndLaunch = {
                                coroutineScope.launch {
                                    isBoosting = true
                                    boostProgress = 0f
                                    for (i in 1..10) {
                                        delay(150L) // 1.5 second progress fill
                                        boostProgress = i / 10f
                                    }
                                    val targetPkg = selectedGame?.packageName
                                    val res = boosterManager.executeBoost(targetPkg)
                                    boostResult = res
                                    isBoosting = false

                                    if (boostConfig.overlayHudEnabled && Settings.canDrawOverlays(this@MainActivity)) {
                                        OverlayService.start(this@MainActivity)
                                    }

                                    if (targetPkg != null) {
                                        prefs.recordGameLaunch()
                                        val launchIntent = packageManager.getLaunchIntentForPackage(targetPkg)
                                        if (launchIntent != null) {
                                            startActivity(launchIntent)
                                        }
                                    }
                                }
                            }
                        )
                        1 -> GamesScreen(
                            gamesList = gamesList,
                            onLaunchGame = { game ->
                                selectedGame = game
                                prefs.recordGameLaunch()
                                coroutineScope.launch { boosterManager.executeBoost(game.packageName) }
                                val launchIntent = packageManager.getLaunchIntentForPackage(game.packageName)
                                if (launchIntent != null) {
                                    startActivity(launchIntent)
                                }
                            },
                            onAddCustomGame = {
                                gamesList = getDetectedGamesList()
                            }
                        )
                        2 -> BoostScreen(
                            boostConfig = boostConfig,
                            onConfigChange = { updated ->
                                boostConfig = updated
                                prefs.saveBoostConfig(updated)
                            },
                            onTriggerBoost = {
                                coroutineScope.launch {
                                    isBoosting = true
                                    val res = boosterManager.executeBoost()
                                    boostResult = res
                                    isBoosting = false
                                }
                            }
                        )
                        3 -> CrosshairsScreen(
                            activeConfig = activeCrosshairConfig,
                            onSelectConfig = { selected ->
                                activeCrosshairConfig = selected
                                prefs.saveCrosshairConfig(selected)
                            },
                            onSaveConfig = { updated ->
                                activeCrosshairConfig = updated
                                prefs.saveCrosshairConfig(updated)
                                if (boostConfig.overlayHudEnabled && Settings.canDrawOverlays(this@MainActivity)) {
                                    OverlayService.start(this@MainActivity)
                                }
                            }
                        )
                        4 -> DeviceScreen(
                            stats = deviceStats,
                            totalRamFreedMb = prefs.getTotalRamFreedMb(),
                            totalGamesLaunched = prefs.getLaunchedGamesCount()
                        )
                    }
                }

                if (showPermissionOnboarding) {
                    PermissionOnboardingDialog(
                        onDismiss = { showPermissionOnboarding = false },
                        onRequestOverlay = { requestOverlayPermission() },
                        onRequestUsage = { requestUsageStatsPermission() },
                        onRequestDnd = { requestDndPermission() }
                    )
                }
            }
        }
    }

    @Composable
    private fun BottomNavigationBar(
        selectedPage: Int,
        pageCount: Int,
        onSelectPage: (Int) -> Unit
    ) {
        val navItems = listOf(
            NavEntry("HOME", Icons.Default.Home),
            NavEntry("GAMES", Icons.Default.Games),
            NavEntry("BOOST", Icons.Default.FlashOn),
            NavEntry("CROSSHAIRS", Icons.Default.Crosshair),
            NavEntry("DEVICE", Icons.Default.PhoneAndroid)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            color = Color(0xFF101010),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REVEX",
                    color = Color(0xFFFF2A2A),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 2.sp
                )

                // Page Indicator Dots + Tabs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = selectedPage == index
                        Surface(
                            modifier = Modifier
                                .clickable { onSelectPage(index) }
                                .padding(horizontal = 2.dp, vertical = 2.dp),
                            color = if (isSelected) Color(0xFFB3001B) else Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) Color.White else Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.title,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Page Dots
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 0 until pageCount) {
                        Box(
                            modifier = Modifier
                                .size(if (i == selectedPage) 8.dp else 5.dp)
                                .clip(CircleShape)
                                .background(if (i == selectedPage) Color(0xFFFFD700) else Color.Gray)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun PermissionOnboardingDialog(
        onDismiss: () -> Unit,
        onRequestOverlay: () -> Unit,
        onRequestUsage: () -> Unit,
        onRequestDnd: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            color = Color.Transparent
        ) {
            Box(contentAlignment = Alignment.Center) {
                Card(
                    modifier = Modifier.width(420.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB3001B))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GRANT PERMISSIONS FOR MAX PERFORMANCE",
                            color = Color(0xFFFF2A2A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "REVEX needs background permissions to overlay floating crosshairs, auto-detect games, and apply lag boost.",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onRequestOverlay,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("1. Allow Floating Crosshair & Overlay", color = Color.White, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onRequestUsage,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("2. Allow Game Usage Access", color = Color.White, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onRequestDnd,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("3. Allow Do Not Disturb Toggle", color = Color.White, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3001B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CONTINUE TO REVEX", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    private data class NavEntry(
        val title: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
    )

    private fun getDetectedGamesList(): List<GameInfo> {
        val list = mutableListOf<GameInfo>()
        val pm = packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val knownGamePackages = setOf(
            "com.tencent.ig", "com.pubg.krmobile", "com.pubg.newstate",
            "com.garena.game.kgtw", "com.dts.freefireth", "com.dts.freefiremax",
            "com.activision.callofduty.shooter", "com.mojang.minecraftpe",
            "com.epicgames.fortnite", "com.ea.gp.fifamobile", "com.mobile.legends"
        )

        for (app in installedApps) {
            val pkg = app.packageName
            val isCategoryGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                app.category == android.content.pm.ApplicationInfo.CATEGORY_GAME
            } else {
                (app.flags and android.content.pm.ApplicationInfo.FLAG_IS_GAME) != 0
            }

            val isKnownGame = knownGamePackages.contains(pkg)
            val isCustomGame = prefs.getCustomGames().contains(pkg)

            if (isCategoryGame || isKnownGame || isCustomGame) {
                val label = pm.getApplicationLabel(app).toString()
                list.add(GameInfo(packageName = pkg, name = label))
            }
        }

        // Add default gaming options if list empty for demo
        if (list.isEmpty()) {
            list.add(GameInfo(packageName = "com.pubg.krmobile", name = "PUBG MOBILE"))
            list.add(GameInfo(packageName = "com.dts.freefireth", name = "Free Fire"))
            list.add(GameInfo(packageName = "com.activision.callofduty.shooter", name = "Call of Duty: Mobile"))
        }

        return list
    }

    private fun hasAllPermissions(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    private fun requestDndPermission() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }
}
