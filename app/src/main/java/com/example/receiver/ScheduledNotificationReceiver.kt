package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.NotificationConfig
import com.example.engine.NotificationEngine

class ScheduledNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val config = intent.getSerializableExtra("config") as? NotificationConfig
        
        config?.let {
            NotificationEngine.postNotification(context, it)
        }
    }
}
