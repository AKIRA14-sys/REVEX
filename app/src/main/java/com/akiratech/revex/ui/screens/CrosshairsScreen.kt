package com.akiratech.revex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akiratech.revex.data.crosshair.CrosshairGenerator
import com.akiratech.revex.data.model.CenterStyle
import com.akiratech.revex.data.model.CrosshairConfig
import com.akiratech.revex.data.model.CrosshairTier
import com.akiratech.revex.ui.components.CrosshairCanvas

@Composable
fun CrosshairsScreen(
    activeConfig: CrosshairConfig,
    onSelectConfig: (CrosshairConfig) -> Unit,
    onSaveConfig: (CrosshairConfig) -> Unit
) {
    val allPresets = remember { CrosshairGenerator.generateAllCrosshairs() }
    var selectedTier by remember { mutableStateOf(CrosshairTier.SHARINGAN) }
    var currentConfig by remember { mutableStateOf(activeConfig) }

    val filteredPresets = remember(selectedTier) {
        allPresets.filter { it.tier == selectedTier }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(12.dp)
    ) {
        // Left Column: Tier Selection & Presets Grid
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Text(
                text = "500+ CROSSHAIR STUDIO",
                color = Color(0xFFFF2A2A),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tier Selector Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CrosshairTier.values()) { tier ->
                    val isSelected = tier == selectedTier
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTier = tier },
                        label = { Text(tier.label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFB3001B),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1A1A1A),
                            labelColor = Color.Gray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Presets Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPresets) { preset ->
                    val isCurrent = preset.id == currentConfig.id
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                currentConfig = preset
                                onSelectConfig(preset)
                            },
                        color = Color(0xFF141414),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = if (isCurrent) Color(0xFFFFD700) else Color(0xFF222222)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CrosshairCanvas(config = preset)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Column: Live Customization & Tuning Controls
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            color = Color(0xFF121212),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LIVE STUDIO & TUNING",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Live Preview Box
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Black, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CrosshairCanvas(config = currentConfig)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Customization Controls
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Size Slider
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Size", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(50.dp))
                        Slider(
                            value = currentConfig.sizeDp,
                            onValueChange = {
                                currentConfig = currentConfig.copy(sizeDp = it)
                                onSaveConfig(currentConfig)
                            },
                            valueRange = 12f..80f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFFF2A2A), activeTrackColor = Color(0xFFB3001B))
                        )
                    }

                    // Sharingan Specific Controls
                    if (currentConfig.isSharingan) {
                        // Sharingan Opacity Slider (10% - 100%)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Eye Alpha", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(50.dp))
                            Slider(
                                value = currentConfig.sharinganOpacity,
                                onValueChange = {
                                    currentConfig = currentConfig.copy(sharinganOpacity = it)
                                    onSaveConfig(currentConfig)
                                },
                                valueRange = 0.1f..1.0f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
                            )
                        }

                        // Center Style Selector
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Center Mark", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(70.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                CenterStyle.values().forEach { style ->
                                    FilterChip(
                                        selected = currentConfig.centerStyle == style,
                                        onClick = {
                                            currentConfig = currentConfig.copy(centerStyle = style)
                                            onSaveConfig(currentConfig)
                                        },
                                        label = { Text(style.name, fontSize = 9.sp) },
                                        modifier = Modifier.height(28.dp)
                                    )
                                }
                            }
                        }
                    }

                    // General Opacity
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Opacity", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(50.dp))
                        Slider(
                            value = currentConfig.opacity,
                            onValueChange = {
                                currentConfig = currentConfig.copy(opacity = it)
                                onSaveConfig(currentConfig)
                            },
                            valueRange = 0.2f..1.0f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.Gray)
                        )
                    }
                }

                Button(
                    onClick = { onSaveConfig(currentConfig) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3001B)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("EQUIP CROSSHAIR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
