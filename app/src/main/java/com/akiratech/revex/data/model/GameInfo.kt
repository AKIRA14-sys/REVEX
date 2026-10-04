package com.akiratech.revex.data.model

data class GameInfo(
    val packageName: String,
    val name: String,
    val isInstalled: Boolean = true,
    val isFavorite: Boolean = false,
    val isAutoBoostEnabled: Boolean = true,
    val category: String = "Game",
    val addedTimestamp: Long = System.currentTimeMillis()
)
