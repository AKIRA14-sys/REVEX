package com.akiratech.revex.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.akiratech.revex.R
import com.akiratech.revex.RevexApplication
import com.akiratech.revex.booster.LagBoosterManager
import com.akiratech.revex.data.crosshair.CrosshairGenerator
import com.akiratech.revex.data.model.CenterStyle
import com.akiratech.revex.data.model.CrosshairConfig
import com.akiratech.revex.data.model.CrosshairTier
import com.akiratech.revex.data.model.DeviceStats
import com.akiratech.revex.data.pref.RevexPreferences
import com.akiratech.revex.monitor.DeviceMonitorManager
import com.akiratech.revex.ui.components.CrosshairCanvas
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OverlayService : LifecycleService() {

    private lateinit var windowManager: WindowManager
    private lateinit var prefs: RevexPreferences
    private lateinit var boosterManager: LagBoosterManager
    private lateinit var monitorManager: DeviceMonitorManager

    private var bubbleView: ComposeView? = null
    private var crosshairView: ComposeView? = null
    private var bubbleParams: WindowManager.LayoutParams? = null

    companion object {
        const val ACTION_START_OVERLAY = "ACTION_START_OVERLAY"
        const val ACTION_STOP_OVERLAY = "ACTION_STOP_OVERLAY"

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_START_OVERLAY
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_STOP_OVERLAY
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        prefs = RevexPreferences(this)
        boosterManager = LagBoosterManager(this)
        monitorManager = DeviceMonitorManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START_OVERLAY -> {
                startForegroundWithNotification()
                setupCrosshairOverlay()
                setupBubbleOverlay()
            }
            ACTION_STOP_OVERLAY -> {
                removeOverlays()
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startForegroundWithNotification() {
        val notification: Notification = NotificationCompat.Builder(this, RevexApplication.OVERLAY_CHANNEL_ID)
            .setContentTitle("REVEX Game Booster Active")
            .setContentText("Overlay HUD and Crosshair running in background")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1001, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1001, notification)
        }
    }

    private fun setupCrosshairOverlay() {
        if (crosshairView != null) return

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val crosshairParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        val savedStateOwner = OverlaySavedStateRegistryOwner(lifecycle)

        crosshairView = ComposeView(this).apply {
            ViewTreeLifecycleOwner.set(this, this@OverlayService)
            ViewTreeSavedStateRegistryOwner.set(this, savedStateOwner)
            setContent {
                val activeId = remember { mutableStateOf(prefs.activeCrosshairId) }
                val allPresets = remember { CrosshairGenerator.generateAllCrosshairs() }
                val defaultPreset = allPresets.find { it.id == activeId.value } ?: CrosshairGenerator.getSharinganPresets()[2]
                var config by remember { mutableStateOf(prefs.getActiveCrosshairConfig(defaultPreset)) }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CrosshairCanvas(config = config)
                }
            }
        }

        try {
            windowManager.addView(crosshairView, crosshairParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBubbleOverlay() {
        if (bubbleView != null) return

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 200
        }

        val savedStateOwner = OverlaySavedStateRegistryOwner(lifecycle)

        bubbleView = ComposeView(this).apply {
            ViewTreeLifecycleOwner.set(this, this@OverlayService)
            ViewTreeSavedStateRegistryOwner.set(this, savedStateOwner)
            setContent {
                var isExpanded by remember { mutableStateOf(false) }
                var deviceStats by remember { mutableStateOf(DeviceStats()) }
                var isBoosting by remember { mutableStateOf(false) }
                var boostMessage by remember { mutableStateOf("") }

                val allPresets = remember { CrosshairGenerator.generateAllCrosshairs() }
                var activeIndex by remember { mutableIntStateOf(0) }
                var currentConfig by remember { mutableStateOf(prefs.getActiveCrosshairConfig(allPresets[0])) }

                LaunchedEffect(Unit) {
                    monitorManager.observeDeviceStats(1000L).collectLatest { stats ->
                        deviceStats = stats
                    }
                }

                Box {
                    // Floating Bubble
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB3001B))
                            .clickable { isExpanded = !isExpanded }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    bubbleParams?.let { params ->
                                        params.x += dragAmount.x.toInt()
                                        params.y += dragAmount.y.toInt()
                                        try {
                                            windowManager.updateViewLayout(bubbleView, params)
                                        } catch (ignored: Exception) {
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "REVEX Bubble",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${deviceStats.currentPingMs}ms",
                                color = Color(0xFFFFD700),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Expanded Comprehensive Control Panel Widget
                    if (isExpanded) {
                        Surface(
                            modifier = Modifier
                                .padding(top = 60.dp)
                                .width(310.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF121212).copy(alpha = 0.95f),
                            tonalElevation = 8.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB3001B))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "REVEX GAMING HUD",
                                        color = Color(0xFFFF2A2A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Row {
                                        IconButton(
                                            onClick = {
                                                stop(this@OverlayService)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PowerSettingsNew,
                                                contentDescription = "Stop Service",
                                                tint = Color.Red
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = { isExpanded = false },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Realtime Mini Stats Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("RAM", color = Color.Gray, fontSize = 9.sp)
                                        Text("${deviceStats.ramUsagePercent.toInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("PING", color = Color.Gray, fontSize = 9.sp)
                                        Text("${deviceStats.currentPingMs} ms", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("TEMP", color = Color.Gray, fontSize = 9.sp)
                                        Text("${deviceStats.batteryTempCelsius}°C", color = if (deviceStats.batteryTempCelsius > 40) Color.Red else Color.Green, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Quick Crosshair Switcher
                                Text("QUICK CROSSHAIR SWITCHER", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (activeIndex > 0) activeIndex-- else activeIndex = allPresets.size - 1
                                            currentConfig = allPresets[activeIndex]
                                            prefs.saveCrosshairConfig(currentConfig)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = Color.White)
                                    }

                                    Text(
                                        text = currentConfig.name,
                                        color = Color(0xFFFFD700),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    IconButton(
                                        onClick = {
                                            activeIndex = (activeIndex + 1) % allPresets.size
                                            currentConfig = allPresets[activeIndex]
                                            prefs.saveCrosshairConfig(currentConfig)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White)
                                    }
                                }

                                // Customization controls
                                Slider(
                                    value = currentConfig.sizeDp,
                                    onValueChange = {
                                        currentConfig = currentConfig.copy(sizeDp = it)
                                        prefs.saveCrosshairConfig(currentConfig)
                                    },
                                    valueRange = 12f..80f,
                                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFF2A2A), activeTrackColor = Color(0xFFB3001B))
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                if (boostMessage.isNotEmpty()) {
                                    Text(
                                        text = boostMessage,
                                        color = Color(0xFF00FFCC),
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }

                                // Instant Boost Button
                                Button(
                                    onClick = {
                                        lifecycleScope.launch {
                                            isBoosting = true
                                            val res = boosterManager.executeBoost()
                                            boostMessage = "Freed ${res.ramFreedMb}MB RAM | Ping: ${res.optimizedPingMs}ms"
                                            isBoosting = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3001B)),
                                    enabled = !isBoosting
                                ) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBoosting) "BOOSTING..." else "INSTANT LAG BOOST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        try {
            windowManager.addView(bubbleView, bubbleParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeOverlays() {
        bubbleView?.let {
            try { windowManager.removeView(it) } catch (ignored: Exception) {}
            bubbleView = null
        }
        crosshairView?.let {
            try { windowManager.removeView(it) } catch (ignored: Exception) {}
            crosshairView = null
        }
    }

    override fun onDestroy() {
        removeOverlays()
        super.onDestroy()
    }

    private class OverlaySavedStateRegistryOwner(
        override val lifecycle: androidx.lifecycle.Lifecycle
    ) : SavedStateRegistryOwner {
        private val controller = SavedStateRegistryController.create(this)
        override val savedStateRegistry: SavedStateRegistry = controller.savedStateRegistry

        init {
            controller.performRestore(null)
        }
    }
}
