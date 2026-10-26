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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import com.example.util.AccessibilityUtils
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MockupElement
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.data.RepeatUnit
import com.example.data.SoundPreset
import com.example.data.VibrationPreset
import com.example.engine.NotificationEngine
import com.example.ui.components.DateTimePicker
import com.example.ui.components.NotificationMockup
import com.example.ui.components.NotificationToolbox
import com.example.ui.theme.CrownGold
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.RoyalPurple
import com.example.ui.viewmodel.NotificationStudioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

@Composable
fun StudioTab(
    currentConfig: NotificationConfig,
    onConfigChange: (NotificationConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val tempVm = remember {
        NotificationStudioViewModel().apply { updateConfig(currentConfig) }
    }
    StudioTab(
        viewModel = tempVm,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioTab(
    viewModel: NotificationStudioViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentConfig by viewModel.config.collectAsState()
    val activeEditMode by viewModel.activeEditMode.collectAsState()

    val onConfigChange: (NotificationConfig) -> Unit = remember(viewModel) {
        { viewModel.updateConfig(it) }
    }
    val onActiveEditModeChange: (MockupElement?) -> Unit = remember(viewModel) {
        { viewModel.setActiveEditMode(it) }
    }

    var showScheduler by remember { mutableStateOf(false) }
    var showSensorySettings by remember { mutableStateOf(false) }
    var showSystemSettings by remember { mutableStateOf(false) }

    LaunchedEffect(activeEditMode) {
        if (activeEditMode == MockupElement.SENSORY) {
            showSensorySettings = true
            showScheduler = false
            showSystemSettings = false
        } else if (activeEditMode == MockupElement.HEADER) {
            showSystemSettings = true
            showScheduler = false
            showSensorySettings = false
        }
    }

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
        if (AccessibilityUtils.configRequiresAccessibility(currentConfig) && !AccessibilityUtils.isAccessibilityEnabled(context)) {
            AccessibilityUtils.promptEnableAccessibility(context)
            return
        }
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

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("studio_tab_column"),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. תצוגה מקדימה בראש העמוד (Live Preview right at the top!)
            item {
                NotificationMockup(
                    config = currentConfig,
                    editable = true,
                    onConfigChange = onConfigChange,
                    activeEditMode = activeEditMode,
                    onActiveEditModeChange = onActiveEditModeChange
                )
            }

            // 2. כל פריטי ההלבשה בשורה נגללת, נמוכה (Low horizontal scroll shelf)
            item {
                NotificationToolbox(
                    config = currentConfig,
                    activeEditMode = activeEditMode,
                    onConfigChange = onConfigChange,
                    onActiveEditModeChange = onActiveEditModeChange
                )
            }

            // 3. כפתורי שליטה קטנים: תזמון קטן, צליל ורטט, הגדרות מערכת
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // כפתור תזמון קטן
                    FilterChip(
                        selected = showScheduler,
                        onClick = {
                            showScheduler = !showScheduler
                            if (showScheduler) {
                                showSensorySettings = false
                                showSystemSettings = false
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (currentConfig.isRepeating) Icons.Default.Repeat else Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (currentConfig.scheduledTimeMillis != null) {
                                    if (currentConfig.isRepeating)
                                        "🔁 חוזרת (${currentConfig.repeatIntervalValue} ${currentConfig.repeatUnit.titleHebrew})"
                                    else
                                        "⏰ מתוזמן"
                                } else "⏰ תזמון",
                                fontSize = 11.sp,
                                fontWeight = if (currentConfig.scheduledTimeMillis != null) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // כפתור צליל ורטט
                    FilterChip(
                        selected = showSensorySettings,
                        onClick = {
                            showSensorySettings = !showSensorySettings
                            if (showSensorySettings) {
                                showScheduler = false
                                showSystemSettings = false
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(15.dp))
                        },
                        label = { Text("🔔 צליל ורטט", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )

                    // כפתור הגדרות מערכת
                    FilterChip(
                        selected = showSystemSettings,
                        onClick = {
                            showSystemSettings = !showSystemSettings
                            if (showSystemSettings) {
                                showScheduler = false
                                showSensorySettings = false
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(15.dp))
                        },
                        label = { Text("⚙️ מערכת", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. לוח תזמון - נפתח רק כשלוחצים על כפתור התזמון הקטן
            item {
                AnimatedVisibility(
                    visible = showScheduler,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, RoyalPurple)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "התראה חוזרת במחזוריות (Recurring)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "הפצה מחזורית קבועה",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = currentConfig.isRepeating,
                                    onCheckedChange = { repeating ->
                                        onConfigChange(currentConfig.copy(isRepeating = repeating))
                                    }
                                )
                            }

                            if (currentConfig.isRepeating) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "מרווח זמן לחזרה:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val next = (currentConfig.repeatIntervalValue - 5).coerceAtLeast(1)
                                                    onConfigChange(currentConfig.copy(repeatIntervalValue = next))
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Text("-", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Text(
                                                text = "${currentConfig.repeatIntervalValue}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp)
                                            )

                                            IconButton(
                                                onClick = {
                                                    val next = currentConfig.repeatIntervalValue + 5
                                                    onConfigChange(currentConfig.copy(repeatIntervalValue = next))
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Text("+", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        RepeatUnit.entries.forEach { unit ->
                                            FilterChip(
                                                selected = currentConfig.repeatUnit == unit,
                                                onClick = { onConfigChange(currentConfig.copy(repeatUnit = unit)) },
                                                label = { Text(unit.titleHebrew, fontSize = 10.sp) }
                                            )
                                        }
                                    }

                                    // Quick Presets
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        listOf(
                                            Triple("כל 10 שנ'", 10, RepeatUnit.SECONDS),
                                            Triple("כל 30 שנ'", 30, RepeatUnit.SECONDS),
                                            Triple("כל 1 דק'", 1, RepeatUnit.MINUTES),
                                            Triple("כל 5 דק'", 5, RepeatUnit.MINUTES),
                                            Triple("כל שעה", 1, RepeatUnit.HOURS)
                                        ).forEach { (label, value, unit) ->
                                            FilterChip(
                                                selected = currentConfig.repeatIntervalValue == value && currentConfig.repeatUnit == unit,
                                                onClick = {
                                                    onConfigChange(currentConfig.copy(repeatIntervalValue = value, repeatUnit = unit))
                                                },
                                                label = { Text(label, fontSize = 9.sp) }
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                try {
                                                    executeWithPermissionCheck {
                                                        val intervalMillis = currentConfig.repeatIntervalValue * currentConfig.repeatUnit.secondsMultiplier * 1000L
                                                        val firstTrigger = System.currentTimeMillis() + intervalMillis.coerceAtLeast(3000L)
                                                        val newConfig = currentConfig.copy(isRepeating = true, scheduledTimeMillis = firstTrigger)
                                                        onConfigChange(newConfig)
                                                        NotificationEngine.scheduleNotification(context, newConfig, firstTrigger)
                                                        Toast.makeText(
                                                            context,
                                                            "התראה חוזרת הופעלה! תופץ כל ${currentConfig.repeatIntervalValue} ${currentConfig.repeatUnit.titleHebrew}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "שגיאה בהפעלת חזרות: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldActive),
                                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("הפעל חזרות", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        if (currentConfig.scheduledTimeMillis != null) {
                                            OutlinedButton(
                                                onClick = {
                                                    onConfigChange(currentConfig.copy(scheduledTimeMillis = null))
                                                    NotificationEngine.cancelScheduledAlarm(context, currentConfig.id)
                                                    Toast.makeText(context, "החזרות הופסקו", Toast.LENGTH_SHORT).show()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("עצור", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Single Schedule
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DateTimePicker(
                                        onDateTimeSelected = { timestamp ->
                                            try {
                                                executeWithPermissionCheck {
                                                    val newConfig = currentConfig.copy(scheduledTimeMillis = timestamp, isRepeating = false)
                                                    onConfigChange(newConfig)
                                                    NotificationEngine.scheduleNotification(context, newConfig, timestamp)
                                                    Toast.makeText(context, "ההתראה תוזמנה בהצלחה!", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "שגיאה בתזמון: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    )

                                    if (currentConfig.scheduledTimeMillis != null) {
                                        OutlinedButton(
                                            onClick = {
                                                onConfigChange(currentConfig.copy(scheduledTimeMillis = null))
                                                NotificationEngine.cancelScheduledAlarm(context, currentConfig.id)
                                                Toast.makeText(context, "התזמון בוטל", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("בטל", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. לוח צליל ורטט - נפתח רק כשלוחצים
            item {
                AnimatedVisibility(
                    visible = showSensorySettings,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, RoyalPurple.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("דפוס רטט:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                VibrationPreset.entries.forEach { vib ->
                                    FilterChip(
                                        selected = currentConfig.vibration == vib,
                                        onClick = { onConfigChange(currentConfig.copy(vibration = vib)) },
                                        label = { Text(vib.titleHebrew, fontSize = 10.sp) }
                                    )
                                }
                            }

                            Text("צליל התראה:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                SoundPreset.entries.forEach { snd ->
                                    FilterChip(
                                        selected = currentConfig.sound == snd,
                                        onClick = { onConfigChange(currentConfig.copy(sound = snd)) },
                                        label = { Text(snd.titleHebrew, fontSize = 10.sp) }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("התרע פעם אחת בלבד (Alert Once)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = currentConfig.alertOnce,
                                    onCheckedChange = { onConfigChange(currentConfig.copy(alertOnce = it)) }
                                )
                            }
                        }
                    }
                }
            }

            // 6. לוח הגדרות מערכת - נפתח רק כשלוחצים
            item {
                AnimatedVisibility(
                    visible = showSystemSettings,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("התראה קבועה (Ongoing / Sticky)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = currentConfig.ongoing,
                                    onCheckedChange = { onConfigChange(currentConfig.copy(ongoing = it)) }
                                )
                            }

                            Text("רמת עדיפות וחשיבות:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                NotificationPriority.entries.forEach { prio ->
                                    FilterChip(
                                        selected = currentConfig.priority == prio,
                                        onClick = { onConfigChange(currentConfig.copy(priority = prio)) },
                                        label = { Text(prio.titleHebrew, fontSize = 10.sp) }
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

        // כפתור צף לשליחה מיידית - רק אייקון של השלח ללא מלל, בצד שמאל למטה
        FloatingActionButton(
            onClick = {
                executeWithPermissionCheck {
                    NotificationEngine.postNotification(context, currentConfig)
                    Toast.makeText(context, "ההתראה #${currentConfig.id} נשלחה בהצלחה!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(AbsoluteAlignment.BottomLeft)
                .padding(16.dp)
                .testTag("studio_send_fab"),
            containerColor = RoyalPurple,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "שלח התראה",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
