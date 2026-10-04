package com.akiratech.revex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akiratech.revex.data.model.BoostConfig

@Composable
fun BoostScreen(
    boostConfig: BoostConfig,
    onConfigChange: (BoostConfig) -> Unit,
    onTriggerBoost: () -> Unit
) {
    var config by remember { mutableStateOf(boostConfig) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LAG BOOSTER ENGINE",
                    color = Color(0xFFFF2A2A),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "System Tuning & Performance Modes",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onTriggerBoost,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3001B)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("RUN MANUAL BOOST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                BoostToggleTile(
                    title = "Auto-Free Memory",
                    subtitle = "Clear background apps on game start",
                    checked = config.autoFreeRam,
                    onCheckedChange = {
                        config = config.copy(autoFreeRam = it)
                        onConfigChange(config)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                BoostToggleTile(
                    title = "Game Performance Mode (API 31+)",
                    subtitle = "Request high performance cpu profile",
                    checked = config.gamePerformanceMode,
                    onCheckedChange = {
                        config = config.copy(gamePerformanceMode = it)
                        onConfigChange(config)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                BoostToggleTile(
                    title = "Sustained Performance Mode",
                    subtitle = "Prevent throttling during extended play",
                    checked = config.sustainedPerformance,
                    onCheckedChange = {
                        config = config.copy(sustainedPerformance = it)
                        onConfigChange(config)
                    }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                BoostToggleTile(
                    title = "Do Not Disturb Toggle",
                    subtitle = "Suppress calls & notifications while gaming",
                    checked = config.enableDnd,
                    onCheckedChange = {
                        config = config.copy(enableDnd = it)
                        onConfigChange(config)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                BoostToggleTile(
                    title = "Floating Overlay HUD Widget",
                    subtitle = "Show dragable in-game stats & crosshair overlay",
                    checked = config.overlayHudEnabled,
                    onCheckedChange = {
                        config = config.copy(overlayHudEnabled = it)
                        onConfigChange(config)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                BoostToggleTile(
                    title = "Thermal Guard Shield",
                    subtitle = "Alert when device temp crosses 42°C",
                    checked = config.thermalGuardEnabled,
                    onCheckedChange = {
                        config = config.copy(thermalGuardEnabled = it)
                        onConfigChange(config)
                    }
                )
            }
        }
    }
}

@Composable
fun BoostToggleTile(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = Color(0xFF141414),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(subtitle, color = Color.Gray, fontSize = 9.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFB3001B),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF222222)
                )
            )
        }
    }
}
