package com.akiratech.revex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akiratech.revex.data.model.DeviceStats

@Composable
fun DeviceScreen(
    stats: DeviceStats,
    totalRamFreedMb: Int,
    totalGamesLaunched: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "DEVICE MONITOR & HARDWARE STATS",
                color = Color(0xFFFF2A2A),
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
            Text(
                text = "Realtime System Diagnostics",
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                HardwareStatCard(
                    icon = Icons.Default.Memory,
                    title = "RAM MEMORY",
                    value = "${stats.ramUsagePercent.toInt()}%",
                    detail = "${stats.ramUsedBytes / (1024 * 1024)} MB used of ${stats.ramTotalBytes / (1024 * 1024)} MB",
                    accentColor = Color(0xFF00FFCC)
                )
                Spacer(modifier = Modifier.height(10.dp))

                HardwareStatCard(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "BATTERY HEALTH",
                    value = "${stats.batteryLevelPercent}%",
                    detail = "${stats.batteryStatus} | ${stats.batteryTempCelsius} °C",
                    accentColor = Color(0xFFFFD700)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                HardwareStatCard(
                    icon = Icons.Default.Storage,
                    title = "INTERNAL STORAGE",
                    value = "${stats.storageUsagePercent.toInt()}%",
                    detail = "${stats.storageUsedBytes / (1024 * 1024 * 1024)} GB used of ${stats.storageTotalBytes / (1024 * 1024 * 1024)} GB",
                    accentColor = Color(0xFF0099FF)
                )
                Spacer(modifier = Modifier.height(10.dp))

                HardwareStatCard(
                    icon = Icons.Default.PhoneAndroid,
                    title = "REVEX STATS",
                    value = "$totalGamesLaunched Games",
                    detail = "Total RAM Freed: $totalRamFreedMb MB",
                    accentColor = Color(0xFFFF2A2A)
                )
            }
        }
    }
}

@Composable
fun HardwareStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    detail: String,
    accentColor: Color
) {
    Surface(
        color = Color(0xFF141414),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(detail, color = Color.LightGray, fontSize = 10.sp)
            }
        }
    }
}
