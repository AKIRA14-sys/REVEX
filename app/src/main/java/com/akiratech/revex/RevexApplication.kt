package com.akiratech.revex

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class RevexApplication : Application() {

    companion object {
        const val OVERLAY_CHANNEL_ID = "revex_overlay_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "REVEX Game Booster Overlay"
            val descriptionText = "Persistent overlay notification for live game HUD and Crosshair"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(OVERLAY_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
