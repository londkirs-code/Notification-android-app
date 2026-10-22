package com.example.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.R
import com.example.data.ActionType
import com.example.data.NotificationConfig
import com.example.data.NotificationRepository
import com.example.data.NotificationStyleType
import com.example.engine.MediaManager
import com.example.engine.NotificationEngine

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_BUTTON = "com.example.ACTION_BUTTON"
        const val ACTION_REPLY = "com.example.ACTION_REPLY"
        const val ACTION_DISMISSED = "com.example.ACTION_DISMISSED"
        const val ACTION_MEDIA_CONTROL = "com.example.ACTION_MEDIA_CONTROL"
        const val ACTION_PROGRESS_CONTROL = "com.example.ACTION_PROGRESS_CONTROL"

        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_BUTTON_INDEX = "extra_button_index"
        const val EXTRA_BUTTON_LABEL = "extra_button_label"
        const val EXTRA_REPLY_KEY = "extra_reply_key"
        const val EXTRA_ACTION_TYPE = "extra_action_type"
        const val EXTRA_MEDIA_ACTION = "extra_media_action"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 101)

        when (intent.action) {
            ACTION_REPLY -> {
                val replyKey = intent.getStringExtra(EXTRA_REPLY_KEY) ?: "key_user_reply"
                val bundle = RemoteInput.getResultsFromIntent(intent)
                val replyText = bundle?.getCharSequence(replyKey)?.toString() ?: ""

                NotificationRepository.logAction(
                    notificationId = notificationId,
                    actionType = ActionType.DIRECT_REPLY,
                    actionLabel = "מענה ישיר מההתראה",
                    userReplyText = replyText,
                    note = "התקבל קלט טקסטואלי ישירות מווילון ההתראות"
                )

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val repliedNotification = NotificationCompat.Builder(context, NotificationEngine.CHANNEL_CHAT)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setContentTitle("מלכת ההתראות (התקבלה תשובה)")
                    .setContentText("💬 תשובתך: \"$replyText\"")
                    .setSubText("עובד בהצלחה")
                    .setAutoCancel(true)
                    .setTimeoutAfter(8000)
                    .build()

                manager.notify(notificationId, repliedNotification)
            }

            ACTION_BUTTON -> {
                val buttonIndex = intent.getIntExtra(EXTRA_BUTTON_INDEX, 1)
                val buttonLabel = intent.getStringExtra(EXTRA_BUTTON_LABEL) ?: "כפתור $buttonIndex"
                val buttonActionStr = intent.getStringExtra("extra_button_action") ?: "TOAST"
                val buttonUrl = intent.getStringExtra("extra_button_url")?.takeIf { it.isNotBlank() } ?: "https://www.google.com"
                val actionType = when (buttonIndex) {
                    1 -> ActionType.BUTTON_1
                    2 -> ActionType.BUTTON_2
                    else -> ActionType.BUTTON_3
                }

                NotificationRepository.logAction(
                    notificationId = notificationId,
                    actionType = actionType,
                    actionLabel = buttonLabel,
                    note = "נלחץ כפתור פעולה מס' $buttonIndex (פעולה: $buttonActionStr)"
                )

                try {
                    val accessService = com.example.service.QueenAccessibilityService.getInstance()
                    when (buttonActionStr) {
                        "TOAST" -> {
                            android.widget.Toast.makeText(context, "בוצעה פעולה מהתראה: $buttonLabel", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        "OPEN_SETTINGS" -> {
                            val settingsIntent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(settingsIntent)
                        }
                        "OPEN_WIFI" -> {
                            val wifiIntent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(wifiIntent)
                        }
                        "OPEN_BLUETOOTH" -> {
                            val btIntent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(btIntent)
                        }
                        "OPEN_BROWSER" -> {
                            val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(buttonUrl)).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(browserIntent)
                        }
                        "OPEN_APP" -> {
                            val packageName = intent.getStringExtra("extra_button_package")?.takeIf { it.isNotBlank() } ?: "com.whatsapp"
                            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                            if (launchIntent != null) {
                                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(launchIntent)
                            } else {
                                android.widget.Toast.makeText(context, "האפליקציה אינה מותקנת או ששם החבילה שגוי: $packageName", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                        "PLAY_MEDIA" -> {
                            com.example.engine.MediaManager.play(context)
                        }
                        "ACCESSIBILITY_HOME" -> {
                            accessService?.performGlobalActionSafe(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME)
                        }
                        "ACCESSIBILITY_BACK" -> {
                            accessService?.performGlobalActionSafe(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
                        }
                        "ACCESSIBILITY_RECENTS" -> {
                            accessService?.performGlobalActionSafe(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_RECENTS)
                        }
                        "ACCESSIBILITY_NOTIFICATIONS" -> {
                            accessService?.performGlobalActionSafe(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
                        }
                        "ACCESSIBILITY_QUICK_SETTINGS" -> {
                            accessService?.performGlobalActionSafe(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
                        }
                        "ACCESSIBILITY_POWER_DIALOG" -> {
                            accessService?.performGlobalActionSafe(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
                        }
                        "ACCESSIBILITY_LOCK_SCREEN" -> {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                accessService?.performGlobalActionSafe(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
                            }
                        }
                        "ACCESSIBILITY_SCREENSHOT" -> {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                accessService?.performGlobalActionSafe(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("Receiver", "Failed to execute button action", e)
                }
            }

            ACTION_DISMISSED -> {
                NotificationRepository.unregisterActiveNotification(notificationId)
                NotificationRepository.logAction(
                    notificationId = notificationId,
                    actionType = ActionType.DISMISSED,
                    actionLabel = "ההתראה נסגרה/הוסרה",
                    note = "המשתמש החליק וסגר את ההתראה"
                )
            }

            ACTION_MEDIA_CONTROL -> {
                val mediaAction = intent.getStringExtra(EXTRA_MEDIA_ACTION) ?: "PLAY_PAUSE"
                when (mediaAction) {
                    "PLAY_PAUSE" -> {
                        MediaManager.play(context)
                        val isPlaying = MediaManager.isPlaying.value
                        NotificationRepository.logAction(
                            notificationId = notificationId,
                            actionType = ActionType.MEDIA_PLAY_PAUSE,
                            actionLabel = if (isPlaying) "ניגון מדיה" else "השהיית מדיה",
                            note = if (isPlaying) "סטרים שמע פעיל" else "מושהה"
                        )
                    }
                    "NEXT" -> {
                        MediaManager.play(context, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "SoundHelix Synthwave #2")
                        NotificationRepository.logAction(
                            notificationId = notificationId,
                            actionType = ActionType.MEDIA_NEXT,
                            actionLabel = "הרצועה הבאה",
                            note = "העברה לשיר הבא"
                        )
                    }
                    "PREV" -> {
                        MediaManager.play(context, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "SoundHelix Synthwave #3")
                        NotificationRepository.logAction(
                            notificationId = notificationId,
                            actionType = ActionType.MEDIA_PREV,
                            actionLabel = "הרצועה הקודמת",
                            note = "חזרה לשיר הקודם"
                        )
                    }
                }
            }

            ACTION_PROGRESS_CONTROL -> {
                NotificationEngine.cancelNotification(context, notificationId)
                NotificationRepository.logAction(
                    notificationId = notificationId,
                    actionType = ActionType.PROGRESS_CANCEL,
                    actionLabel = "ביטול הורדה",
                    note = "ההורדה בוטלה ע״י המשתמש"
                )
            }
        }
    }
}
