package com.example.ui.tabs

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.data.NotificationStyleType
import com.example.data.SoundPreset
import com.example.data.VibrationPreset
import com.example.engine.NotificationEngine
import com.example.ui.components.DateTimePicker
import com.example.ui.components.NotificationMockup
import com.example.ui.theme.CrownGold
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.RoyalPurple

fun checkCanPostNotifications(context: android.content.Context): Boolean {
    val managerCompat = NotificationManagerCompat.from(context)
    if (!managerCompat.areNotificationsEnabled()) return false
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioTab(
    currentConfig: NotificationConfig,
    onConfigChange: (NotificationConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSensorySettings by remember { mutableStateOf(false) }
    var showSystemSettings by remember { mutableStateOf(false) }

    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            pendingAction?.invoke()
        } else {
            Toast.makeText(
                context,
                "שגיאה: חסרה הרשאת התראות! לא ניתן להציג או לתזמן התראה ללא אישור.",
                Toast.LENGTH_LONG
            ).show()
        }
        pendingAction = null
    }

    fun executeWithPermissionCheck(action: () -> Unit) {
        if (checkCanPostNotifications(context)) {
            action()
        } else {
            pendingAction = action
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Toast.makeText(
                    context,
                    "ההתראות חסומות בהגדרות המכשיר. יש להפעילן כדי להציג התראות.",
                    Toast.LENGTH_LONG
                ).show()
                try {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("studio_tab_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section: Send / Dismiss / Schedule Action Toolbar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            executeWithPermissionCheck {
                                NotificationEngine.postNotification(context, currentConfig)
                                Toast.makeText(context, "ההתראה #${currentConfig.id} נשלחה בהצלחה למכשיר!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("studio_send_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("שלח התראה עכשיו", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            NotificationEngine.cancelNotification(context, currentConfig.id)
                            Toast.makeText(context, "ההתראה #${currentConfig.id} הוסרה מהווילון", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("מחק מהווילון", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentConfig.scheduledTimeMillis != null)
                            "מתוזמן ל: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(currentConfig.scheduledTimeMillis!!))}"
                        else "תזמון עתידי: כבוי",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    DateTimePicker(
                        onDateTimeSelected = { timestamp ->
                            executeWithPermissionCheck {
                                onConfigChange(currentConfig.copy(scheduledTimeMillis = timestamp))
                                NotificationEngine.scheduleNotification(context, currentConfig.copy(scheduledTimeMillis = timestamp), timestamp)
                                Toast.makeText(context, "ההתראה תוזמנה בהצלחה ותוצג במועד שנקבע!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // Section: Live Interactive Preview (Direct in-place editing)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = RoyalPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "תצוגה מקדימה חיה - עריכה במקום",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CrownGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "הקש על פריט לעריכה ישירה ✍️",
                                fontSize = 11.sp,
                                color = CrownGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // The 100% interactive mockup with in-place textfields and bubbles
                    NotificationMockup(
                        config = currentConfig,
                        editable = true,
                        onConfigChange = onConfigChange
                    )
                }
            }
        }

        // Section: Quick Feature Toggles Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "תוספות ורכיבים מהירים להודעה:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Direct Reply toggle chip
                    FilterChip(
                        selected = currentConfig.directReplyEnabled,
                        onClick = { onConfigChange(currentConfig.copy(directReplyEnabled = !currentConfig.directReplyEnabled)) },
                        label = {
                            Text(
                                if (currentConfig.directReplyEnabled) "💬 מענה ישיר ✓" else "+ מענה ישיר",
                                fontSize = 11.sp
                            )
                        }
                    )

                    // Progress bar toggle chip
                    FilterChip(
                        selected = currentConfig.styleType == NotificationStyleType.PROGRESS,
                        onClick = {
                            onConfigChange(
                                currentConfig.copy(
                                    styleType = if (currentConfig.styleType == NotificationStyleType.PROGRESS)
                                        NotificationStyleType.STANDARD else NotificationStyleType.PROGRESS
                                )
                            )
                        },
                        label = {
                            Text(
                                if (currentConfig.styleType == NotificationStyleType.PROGRESS) "📊 סרגל התקדמות ✓" else "+ סרגל התקדמות",
                                fontSize = 11.sp
                            )
                        }
                    )

                    // Big Picture toggle chip
                    FilterChip(
                        selected = currentConfig.styleType == NotificationStyleType.BIG_PICTURE,
                        onClick = {
                            onConfigChange(
                                currentConfig.copy(
                                    styleType = if (currentConfig.styleType == NotificationStyleType.BIG_PICTURE)
                                        NotificationStyleType.STANDARD else NotificationStyleType.BIG_PICTURE
                                )
                            )
                        },
                        label = {
                            Text(
                                if (currentConfig.styleType == NotificationStyleType.BIG_PICTURE) "🖼️ תמונת נושא ✓" else "+ תמונת נושא",
                                fontSize = 11.sp
                            )
                        }
                    )

                    // Big Text toggle chip
                    FilterChip(
                        selected = currentConfig.styleType == NotificationStyleType.BIG_TEXT,
                        onClick = {
                            onConfigChange(
                                currentConfig.copy(
                                    styleType = if (currentConfig.styleType == NotificationStyleType.BIG_TEXT)
                                        NotificationStyleType.STANDARD else NotificationStyleType.BIG_TEXT
                                )
                            )
                        },
                        label = {
                            Text(
                                if (currentConfig.styleType == NotificationStyleType.BIG_TEXT) "📜 טקסט מורחב ✓" else "+ טקסט מורחב",
                                fontSize = 11.sp
                            )
                        }
                    )

                    // Media Player toggle chip
                    FilterChip(
                        selected = currentConfig.styleType == NotificationStyleType.MEDIA,
                        onClick = {
                            onConfigChange(
                                currentConfig.copy(
                                    styleType = if (currentConfig.styleType == NotificationStyleType.MEDIA)
                                        NotificationStyleType.STANDARD else NotificationStyleType.MEDIA
                                )
                            )
                        },
                        label = {
                            Text(
                                if (currentConfig.styleType == NotificationStyleType.MEDIA) "🎵 נגן מדיה ✓" else "+ נגן מדיה",
                                fontSize = 11.sp
                            )
                        }
                    )

                    // Sound & Vibration toggle chip
                    FilterChip(
                        selected = showSensorySettings,
                        onClick = {
                            showSensorySettings = !showSensorySettings
                            if (showSensorySettings) showSystemSettings = false
                        },
                        label = { Text("🔔 צליל ורטט", fontSize = 11.sp) }
                    )

                    // System / Priority settings chip
                    FilterChip(
                        selected = showSystemSettings,
                        onClick = {
                            showSystemSettings = !showSystemSettings
                            if (showSystemSettings) showSensorySettings = false
                        },
                        label = { Text("⚙️ הגדרות עדיפות ומערכת", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Section: Sensory Settings Card (Vibration & Sound) - Expands only when requested
        item {
            AnimatedVisibility(
                visible = showSensorySettings,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, RoyalPurple.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = RoyalPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("הגדרות צליל ורטט", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoyalPurple)
                        }

                        Text("דפוס רטט:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            VibrationPreset.entries.forEach { vib ->
                                FilterChip(
                                    selected = currentConfig.vibration == vib,
                                    onClick = { onConfigChange(currentConfig.copy(vibration = vib)) },
                                    label = { Text(vib.titleHebrew, fontSize = 11.sp) }
                                )
                            }
                        }

                        Text("צליל התראה:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            SoundPreset.entries.forEach { snd ->
                                FilterChip(
                                    selected = currentConfig.sound == snd,
                                    onClick = { onConfigChange(currentConfig.copy(sound = snd)) },
                                    label = { Text(snd.titleHebrew, fontSize = 11.sp) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("התרע פעם אחת בלבד (Only Alert Once)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("מבטל צליל ורטט חוזרים בעת עדכון התראה קיימת", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = currentConfig.alertOnce,
                                onCheckedChange = { onConfigChange(currentConfig.copy(alertOnce = it)) }
                            )
                        }
                    }
                }
            }
        }

        // Section: System / Priority Settings Card - Expands only when requested
        item {
            AnimatedVisibility(
                visible = showSystemSettings,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = RoyalPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("הגדרות עדיפות ומערכת", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoyalPurple)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("התראה קבועה (Ongoing / Sticky)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("המשתמש לא יכול להחליק ולמחוק אותה מווילון ההתראות", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = currentConfig.ongoing,
                                onCheckedChange = { onConfigChange(currentConfig.copy(ongoing = it)) }
                            )
                        }

                        Text("רמת עדיפות וחשיבות:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            NotificationPriority.entries.forEach { prio ->
                                FilterChip(
                                    selected = currentConfig.priority == prio,
                                    onClick = { onConfigChange(currentConfig.copy(priority = prio)) },
                                    label = { Text(prio.titleHebrew, fontSize = 11.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = currentConfig.id.toString(),
                            onValueChange = { onConfigChange(currentConfig.copy(id = it.toIntOrNull() ?: currentConfig.id)) },
                            label = { Text("מזהה התראה (Notification ID)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}
