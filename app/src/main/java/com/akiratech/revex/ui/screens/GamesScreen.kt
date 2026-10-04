package com.akiratech.revex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Games
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akiratech.revex.data.model.GameInfo

@Composable
fun GamesScreen(
    gamesList: List<GameInfo>,
    onLaunchGame: (GameInfo) -> Unit,
    onAddCustomGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GAMES HUB",
                    color = Color(0xFFFF2A2A),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "Auto-Boosted Gaming Library",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onAddCustomGame,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF2A2A)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("ADD APP / GAME", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gamesList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No games detected. Tap 'ADD APP / GAME' to select installed apps.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(gamesList) { game ->
                    GameCard(game = game, onLaunch = { onLaunchGame(game) })
                }
            }
        }
    }
}

@Composable
fun GameCard(game: GameInfo, onLaunch: () -> Unit) {
    Surface(
        color = Color(0xFF141414),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262626))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFB3001B)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Games, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = game.packageName,
                    color = Color.Gray,
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onLaunch,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFB3001B))
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = "Launch", tint = Color.White)
            }
        }
    }
}
