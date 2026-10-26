package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.NotificationConfig
import com.example.engine.NotificationEngine

class ScheduledNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val config = intent.getSerializableExtra("config") as? NotificationConfig
        
        config?.let {
            NotificationEngine.postNotification(context, it)

            // If recurring, calculate and schedule next trigger time
            if (it.isRepeating && it.repeatIntervalValue > 0) {
                val intervalMillis = it.repeatIntervalValue * it.repeatUnit.secondsMultiplier * 1000L
                val nextTriggerTime = System.currentTimeMillis() + intervalMillis.coerceAtLeast(3000L)
                val nextConfig = it.copy(scheduledTimeMillis = nextTriggerTime)
                NotificationEngine.scheduleNotification(context, nextConfig, nextTriggerTime)
            }
        }
    }
}
