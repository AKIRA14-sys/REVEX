package com.akiratech.revex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akiratech.revex.booster.BoostResult
import com.akiratech.revex.data.model.DeviceStats
import com.akiratech.revex.data.model.GameInfo
import com.akiratech.revex.ui.components.SharinganPlayButton

@Composable
fun HomeScreen(
    stats: DeviceStats,
    isBoosting: Boolean,
    boostProgress: Float,
    boostResult: BoostResult?,
    selectedGame: GameInfo?,
    gamesList: List<GameInfo>,
    onSelectGame: (GameInfo) -> Unit,
    onExecuteBoostAndLaunch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Live System Telemetry
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "SYSTEM TELEMETRY",
                color = Color(0xFFFF2A2A),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )

            MetricCard(
                icon = Icons.Default.Speed,
                label = "PING / JITTER",
                value = "${stats.currentPingMs} ms",
                subtext = "${stats.jitterMs} ms jitter",
                accentColor = Color(0xFFFFD700)
            )

            MetricCard(
                icon = Icons.Default.Memory,
                label = "RAM USAGE",
                value = "${stats.ramUsagePercent.toInt()}%",
                subtext = "${stats.ramUsedBytes / (1024 * 1024)} MB / ${stats.ramTotalBytes / (1024 * 1024)} MB",
                accentColor = Color(0xFF00FFCC)
            )

            MetricCard(
                icon = Icons.Default.Thermostat,
                label = "BATTERY TEMP",
                value = "${stats.batteryTempCelsius} °C",
                subtext = stats.batteryStatus,
                accentColor = if (stats.batteryTempCelsius > 40f) Color.Red else Color(0xFF00FF66)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Center Column: Interactive Animated Sharingan Button & Boost Banner with thin Progress Bar
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SharinganPlayButton(
                size = 160.dp,
                isBoosting = isBoosting,
                onClick = onExecuteBoostAndLaunch
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isBoosting) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "OPTIMIZING GAME ENGINE...",
                        color = Color(0xFFFFD700),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { boostProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFFFF2A2A),
                        trackColor = Color(0xFF222222)
                    )
                }
            } else if (boostResult != null) {
                Surface(
                    color = Color(0xFF181818),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BOOSTED & LAUNCHED!",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Freed ${boostResult.ramFreedMb} MB RAM | Optimized Ping: ${boostResult.optimizedPingMs}ms",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "TAP PLAY TO BOOST & LAUNCH GAME",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Right Column: Selected Game Card with arrow selectors
        Column(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentGameIndex = gamesList.indexOf(selectedGame).coerceAtLeast(0)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                color = Color(0xFF141414),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB3001B))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SELECTED TARGET GAME",
                        color = Color(0xFFFF2A2A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (gamesList.isNotEmpty()) {
                                    val prevIndex = if (currentGameIndex > 0) currentGameIndex - 1 else gamesList.size - 1
                                    onSelectGame(gamesList[prevIndex])
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = Color.White)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFB3001B)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Games, contentDescription = null, tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = selectedGame?.name ?: "No Game",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }

                        IconButton(
                            onClick = {
                                if (gamesList.isNotEmpty()) {
                                    val nextIndex = (currentGameIndex + 1) % gamesList.size
                                    onSelectGame(gamesList[nextIndex])
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onExecuteBoostAndLaunch,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3001B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TAP PLAY TO BOOST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
