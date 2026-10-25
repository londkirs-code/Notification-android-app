package com.example.engine

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.example.MainActivity
import com.example.R
import com.example.data.ActionType
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.data.NotificationRepository
import com.example.data.NotificationStyleType
import com.example.data.SoundPreset
import com.example.data.VibrationPreset
import com.example.receiver.NotificationActionReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object NotificationEngine {

    const val CHANNEL_URGENT = "queen_urgent"
    const val CHANNEL_HIGH = "queen_high"
    const val CHANNEL_DEFAULT = "queen_default"
    const val CHANNEL_LOW = "queen_low"
    const val CHANNEL_MEDIA = "queen_media"
    const val CHANNEL_CHAT = "queen_chat"

    private val progressJobs = mutableMapOf<Int, Job>()
    private val scheduledJobs = mutableMapOf<Int, Job>()

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_URGENT,
                    "התראות דחופות (Heads-Up)",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "התראות מתפרצות עם צליל, רטט והופעה בראש המסך"
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 200, 100, 200, 100, 400)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                },
                NotificationChannel(
                    CHANNEL_HIGH,
                    "התראות חשובות (צליל ורטט)",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "התראות עם צליל ורטט פעיל"
                    enableLights(true)
                    lightColor = Color.YELLOW
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_DEFAULT,
                    "התראות רגילות",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "התראות רגילות במגש ההתראות"
                },
                NotificationChannel(
                    CHANNEL_LOW,
                    "התראות שקטות",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "התראות ללא צליל וללא רטט"
                    enableVibration(false)
                },
                NotificationChannel(
                    CHANNEL_CHAT,
                    "שיחות והודעות (מענה ישיר)",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "ערוץ המיועד להודעות צ'אט וסגנון שיחה עם שדה מענה ישיר"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_MEDIA,
                    "מדיה והתקדמות חיה",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "ערוץ לנגן מוזיקה וסרגלי התקדמות של תהליכים ארוכים"
                    enableVibration(false)
                }
            )

            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    private fun resolveChannelId(config: NotificationConfig): String {
        return when (config.styleType) {
            NotificationStyleType.MESSAGING -> CHANNEL_CHAT
            NotificationStyleType.PROGRESS, NotificationStyleType.MEDIA -> CHANNEL_MEDIA
            else -> when (config.priority) {
                NotificationPriority.URGENT -> CHANNEL_URGENT
                NotificationPriority.HIGH -> CHANNEL_HIGH
                NotificationPriority.DEFAULT -> CHANNEL_DEFAULT
                NotificationPriority.LOW -> CHANNEL_LOW
                NotificationPriority.MIN -> CHANNEL_LOW
            }
        }
    }

    fun scheduleNotification(context: Context, config: NotificationConfig, triggerTimeMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, com.example.receiver.ScheduledNotificationReceiver::class.java).apply {
            putExtra("config", config)
        }
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            config.id,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }

    fun postNotification(context: Context, config: NotificationConfig): Int {
        val channelId = resolveChannelId(config)
        val managerCompat = NotificationManagerCompat.from(context)

        // Handle real download if it's preset 202
        if (config.id == 202) {
            DownloadEngine.startRealDownload(context, config)
        }

        // Handle real media playback if it's preset 205
        if (config.id == 205) {
            MediaManager.play(context)
        }

        val smallIconRes = when (config.iconKey) {
            "BELL" -> android.R.drawable.ic_popup_reminder
            "STAR" -> android.R.drawable.btn_star_big_on
            "SHIELD" -> android.R.drawable.ic_lock_lock
            "ALERT" -> android.R.drawable.stat_sys_warning
            "MEDIA" -> android.R.drawable.ic_media_play
            "CHAT" -> R.drawable.ic_action_reply
            "CHECK" -> R.drawable.ic_action_check
            else -> R.drawable.ic_stat_notify
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIconRes)
            .setContentTitle(config.title)
            .setContentText(config.content)
            .setSubText(config.subText.takeIf { it.isNotBlank() })
            .setPriority(config.priority.compatPriority)
            .setOngoing(config.ongoing)
            .setAutoCancel(config.autoCancel)
            .setOnlyAlertOnce(config.alertOnce)

        if (config.badgeNumber > 0) {
            builder.setNumber(config.badgeNumber)
        }

        if (config.timeoutAfterMs > 0) {
            builder.setTimeoutAfter(config.timeoutAfterMs)
        }

        if (!config.groupKey.isNullOrBlank()) {
            builder.setGroup(config.groupKey)
            if (config.isGroupSummary) {
                builder.setGroupSummary(true)
            }
        }

        applySensory(context, builder, config)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
            putExtra(NotificationActionReceiver.EXTRA_ACTION_TYPE, ActionType.CONTENT_CLICK.name)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            config.id * 10,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(contentPendingIntent)

        val dismissIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_DISMISSED
            putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            config.id * 10 + 9,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.setDeleteIntent(dismissPendingIntent)

        try {
            val largeIconBitmap: Bitmap? = when {
                !config.largeIconUri.isNullOrBlank() -> getBitmapFromUriOrPath(context, config.largeIconUri)
                config.largeIconDrawableRes != null && config.largeIconDrawableRes != 0 -> {
                    BitmapFactory.decodeResource(context.resources, config.largeIconDrawableRes)
                }
                !config.imageUri.isNullOrBlank() -> getBitmapFromUriOrPath(context, config.imageUri)
                config.imageDrawableRes != null && config.imageDrawableRes != 0 -> {
                    BitmapFactory.decodeResource(context.resources, config.imageDrawableRes)
                }
                else -> {
                    BitmapFactory.decodeResource(context.resources, R.drawable.app_icon_1792631589963)
                }
            }
            if (largeIconBitmap != null) {
                builder.setLargeIcon(largeIconBitmap)
            }
        } catch (_: Exception) {}

        applyStyle(context, builder, config)

        if (config.button1Enabled && config.button1Label.isNotBlank()) {
            val bIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_BUTTON
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
                putExtra(NotificationActionReceiver.EXTRA_BUTTON_INDEX, 1)
                putExtra(NotificationActionReceiver.EXTRA_BUTTON_LABEL, config.button1Label)
                putExtra("extra_button_action", config.button1Action.name)
                putExtra("extra_button_url", config.button1Url)
                putExtra("extra_button_package", config.button1Package)
            }
            val pIntent = PendingIntent.getBroadcast(
                context,
                config.id * 10 + 1,
                bIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(R.drawable.ic_action_check, config.button1Label, pIntent)
        }

        if (config.button2Enabled && config.button2Label.isNotBlank()) {
            val bIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_BUTTON
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
                putExtra(NotificationActionReceiver.EXTRA_BUTTON_INDEX, 2)
                putExtra(NotificationActionReceiver.EXTRA_BUTTON_LABEL, config.button2Label)
                putExtra("extra_button_action", config.button2Action.name)
                putExtra("extra_button_url", config.button2Url)
                putExtra("extra_button_package", config.button2Package)
            }
            val pIntent = PendingIntent.getBroadcast(
                context,
                config.id * 10 + 2,
                bIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(R.drawable.ic_action_reply, config.button2Label, pIntent)
        }

        if (config.button3Enabled && config.button3Label.isNotBlank()) {
            val bIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_BUTTON
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
                putExtra(NotificationActionReceiver.EXTRA_BUTTON_INDEX, 3)
                putExtra(NotificationActionReceiver.EXTRA_BUTTON_LABEL, config.button3Label)
                putExtra("extra_button_action", config.button3Action.name)
                putExtra("extra_button_url", config.button3Url)
                putExtra("extra_button_package", config.button3Package)
            }
            val pIntent = PendingIntent.getBroadcast(
                context,
                config.id * 10 + 3,
                bIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(R.drawable.ic_action_close, config.button3Label, pIntent)
        }

        if (config.directReplyEnabled && config.directReplyLabel.isNotBlank()) {
            val remoteInput = RemoteInput.Builder(config.directReplyKey)
                .setLabel(config.directReplyLabel)
                .build()

            val replyIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_REPLY
                putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
                putExtra(NotificationActionReceiver.EXTRA_REPLY_KEY, config.directReplyKey)
            }
            val replyPendingIntent = PendingIntent.getBroadcast(
                context,
                config.id * 10 + 4,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val replyAction = NotificationCompat.Action.Builder(
                R.drawable.ic_action_reply,
                config.directReplyButtonLabel,
                replyPendingIntent
            )
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()

            builder.addAction(replyAction)
        }

        try {
            managerCompat.notify(config.id, builder.build())
            NotificationRepository.registerActiveNotification(config.id)
        } catch (e: SecurityException) {
            NotificationRepository.logAction(
                notificationId = config.id,
                actionType = ActionType.CONTENT_CLICK,
                actionLabel = "שגיאת הרשאה",
                note = "חסרה הרשאת POST_NOTIFICATIONS"
            )
        }

        return config.id
    }

    private fun applyStyle(
        context: Context,
        builder: NotificationCompat.Builder,
        config: NotificationConfig
    ) {
        when (config.styleType) {
            NotificationStyleType.STANDARD -> {}
            NotificationStyleType.BIG_TEXT -> {
                val bigTextStyle = NotificationCompat.BigTextStyle()
                    .bigText(config.bigText)
                    .setBigContentTitle(config.title)
                    .setSummaryText(config.summaryText.takeIf { it.isNotBlank() })
                builder.setStyle(bigTextStyle)
            }
            NotificationStyleType.BIG_PICTURE -> {
                try {
                    val heroBitmap: Bitmap? = when {
                        !config.imageUri.isNullOrBlank() -> getBitmapFromUriOrPath(context, config.imageUri)
                        config.imageDrawableRes != null && config.imageDrawableRes != 0 -> {
                            BitmapFactory.decodeResource(context.resources, config.imageDrawableRes)
                        }
                        else -> {
                            BitmapFactory.decodeResource(context.resources, R.drawable.notification_hero_1792631605007)
                        }
                    }
                    if (heroBitmap != null) {
                        val bigPicStyle = NotificationCompat.BigPictureStyle()
                            .bigPicture(heroBitmap)
                            .setBigContentTitle(config.title)
                            .setSummaryText(config.summaryText.takeIf { it.isNotBlank() })
                        builder.setStyle(bigPicStyle)
                    }
                } catch (_: Exception) {}
            }
            NotificationStyleType.INBOX -> {
                val inboxStyle = NotificationCompat.InboxStyle()
                    .setBigContentTitle(config.title)
                    .setSummaryText(config.summaryText.takeIf { it.isNotBlank() })
                config.inboxLines.forEach { inboxStyle.addLine(it) }
                builder.setStyle(inboxStyle)
            }
            NotificationStyleType.MESSAGING -> {
                val queenPerson = Person.Builder().setName("מלך ההתראות").setKey("king_bot").build()
                val userPerson = Person.Builder().setName("משתמש").setKey("user_client").build()

                val messagingStyle = NotificationCompat.MessagingStyle(userPerson)
                    .setConversationTitle("ערוץ פקודות ותקשורת")
                    .addMessage("שלום! אני מוכן לקבל פקודות ישירות.", System.currentTimeMillis() - 15000, queenPerson)
                    .addMessage("ניתן להשיב לי ישירות מתוך תיבת ההזנה 👇", System.currentTimeMillis() - 5000, queenPerson)

                builder.setStyle(messagingStyle)
            }
            NotificationStyleType.PROGRESS -> {
                builder.setProgress(config.progressMax, config.progressCurrent, config.progressIndeterminate)
            }
            NotificationStyleType.MEDIA -> {
                val isPlaying = MediaManager.isPlaying.value
                val prevIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = NotificationActionReceiver.ACTION_MEDIA_CONTROL
                    putExtra(NotificationActionReceiver.EXTRA_MEDIA_ACTION, "PREV")
                    putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
                }
                val playIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = NotificationActionReceiver.ACTION_MEDIA_CONTROL
                    putExtra(NotificationActionReceiver.EXTRA_MEDIA_ACTION, "PLAY_PAUSE")
                    putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
                }
                val nextIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = NotificationActionReceiver.ACTION_MEDIA_CONTROL
                    putExtra(NotificationActionReceiver.EXTRA_MEDIA_ACTION, "NEXT")
                    putExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, config.id)
                }

                val pPrev = PendingIntent.getBroadcast(context, 701, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                val pPlay = PendingIntent.getBroadcast(context, 702, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                val pNext = PendingIntent.getBroadcast(context, 703, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                builder.addAction(R.drawable.ic_action_skip_prev, "הקודם", pPrev)
                builder.addAction(
                    if (isPlaying) R.drawable.ic_action_pause else R.drawable.ic_action_play,
                    if (isPlaying) "השהה" else "נגן",
                    pPlay
                )
                builder.addAction(R.drawable.ic_action_skip_next, "הבא", pNext)

                builder.setContentTitle(config.mediaTrackTitle)
                builder.setContentText(config.mediaArtist)
            }
            NotificationStyleType.CHRONOMETER -> {
                builder.setUsesChronometer(true)
                if (config.chronometerCountDown) {
                    builder.setChronometerCountDown(true)
                    builder.setWhen(System.currentTimeMillis() + (config.chronometerSeconds * 1000L))
                } else {
                    builder.setWhen(System.currentTimeMillis())
                }
            }
        }
    }

    private fun applySensory(
        context: Context,
        builder: NotificationCompat.Builder,
        config: NotificationConfig
    ) {
        val pattern = when (config.vibration) {
            VibrationPreset.DEFAULT -> longArrayOf(0, 250, 250, 250)
            VibrationPreset.NONE -> longArrayOf(0)
            VibrationPreset.SOS -> VibrationPreset.SOS.pattern
            VibrationPreset.HEAVY_DOUBLE -> VibrationPreset.HEAVY_DOUBLE.pattern
            VibrationPreset.HEARTBEAT -> VibrationPreset.HEARTBEAT.pattern
            VibrationPreset.CUSTOM -> parseCustomVibration(config.customVibrateString)
        }
        if (pattern != null && pattern.isNotEmpty()) {
            builder.setVibrate(pattern)
        }

        when (config.sound) {
            SoundPreset.DEFAULT -> builder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS)
            SoundPreset.ALARM -> builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            SoundPreset.RINGTONE -> builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            SoundPreset.SILENT -> builder.setSilent(true)
        }

        config.ledColorHex?.let { hex ->
            try {
                val color = Color.parseColor(hex)
                builder.setLights(color, 1000, 1000)
            } catch (_: Exception) {}
        }
    }

    private fun parseCustomVibration(input: String): LongArray {
        return try {
            input.split(",").map { it.trim().toLong() }.toLongArray()
        } catch (_: Exception) {
            longArrayOf(0, 250, 250, 250)
        }
    }

    fun cancelNotification(context: Context, id: Int) {
        scheduledJobs[id]?.cancel()
        scheduledJobs.remove(id)
        val manager = NotificationManagerCompat.from(context)
        manager.cancel(id)
        NotificationRepository.unregisterActiveNotification(id)
    }

    fun cancelAll(context: Context) {
        scheduledJobs.values.forEach { it.cancel() }
        scheduledJobs.clear()
        val manager = NotificationManagerCompat.from(context)
        manager.cancelAll()
        NotificationRepository.clearAllActive()
    }

    fun generateTermuxCommand(config: NotificationConfig): String {
        val sb = StringBuilder("termux-notification")
        sb.append(" --id ${config.id}")
        sb.append(" --title \"${config.title.replace("\"", "\\\"")}\"")
        sb.append(" --content \"${config.content.replace("\"", "\\\"")}\"")

        val prioStr = when (config.priority) {
            NotificationPriority.URGENT -> "max"
            NotificationPriority.HIGH -> "high"
            NotificationPriority.DEFAULT -> "default"
            NotificationPriority.LOW -> "low"
            NotificationPriority.MIN -> "min"
        }
        sb.append(" --prio $prioStr")

        if (config.ongoing) sb.append(" --ongoing")
        if (config.alertOnce) sb.append(" --alert-once")

        val vibrateStr = when (config.vibration) {
            VibrationPreset.DEFAULT -> "0,250,250,250"
            VibrationPreset.SOS -> "0,150,100,150,100,150,250,400,150,400,150,400,250,150,100,150,100,150"
            VibrationPreset.HEAVY_DOUBLE -> "0,400,150,600"
            VibrationPreset.HEARTBEAT -> "0,120,120,240,400,120,120,240"
            VibrationPreset.CUSTOM -> config.customVibrateString.replace(" ", "")
            VibrationPreset.NONE -> null
        }
        if (vibrateStr != null) {
            sb.append(" --vibrate \"$vibrateStr\"")
        }

        if (config.sound == SoundPreset.SILENT) {
            sb.append(" --sound false")
        }

        if (config.ledColorHex != null) {
            sb.append(" --led-color \"${config.ledColorHex.removePrefix("#")}\"")
        }

        if (config.button1Enabled && config.button1Label.isNotBlank()) {
            sb.append(" --button1 \"${config.button1Label}\" --button1-action \"echo Button1 clicked\"")
        }
        if (config.button2Enabled && config.button2Label.isNotBlank()) {
            sb.append(" --button2 \"${config.button2Label}\" --button2-action \"echo Button2 clicked\"")
        }
        if (config.button3Enabled && config.button3Label.isNotBlank()) {
            sb.append(" --button3 \"${config.button3Label}\" --button3-action \"echo Button3 clicked\"")
        }

        if (!config.groupKey.isNullOrBlank()) {
            sb.append(" --group \"${config.groupKey}\"")
        }

        return sb.toString()
    }

    fun getBitmapFromUriOrPath(context: Context, pathOrUri: String?): Bitmap? {
        if (pathOrUri.isNullOrBlank()) return null
        return try {
            if (pathOrUri.startsWith("/")) {
                BitmapFactory.decodeFile(pathOrUri)
            } else {
                val uri = Uri.parse(pathOrUri)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (_: Exception) {
            null
        }
    }
}
