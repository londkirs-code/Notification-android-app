package com.example.ui.tabs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.data.NotificationStyleType
import com.example.data.SoundPreset
import com.example.data.VibrationPreset
import com.example.engine.NotificationEngine
import com.example.ui.components.NotificationMockup
import com.example.ui.theme.CrownGold
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.RoyalPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioTab(
    currentConfig: NotificationConfig,
    onConfigChange: (NotificationConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scheduleSeconds by remember { mutableStateOf("10") }
    var isScheduling by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("studio_tab_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Live Preview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👁️ תצוגה מקדימה חיה של ההתראה",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalPurple.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ID: #${currentConfig.id}",
                                fontSize = 11.sp,
                                color = RoyalPurple,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    NotificationMockup(config = currentConfig)
                }
            }
        }

        // Section: Send, Update & Delete Actions Toolbar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            NotificationEngine.postNotification(context, currentConfig)
                            Toast.makeText(context, "ההתראה #${currentConfig.id} נוצרה/עודכנה בהצלחה!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("studio_send_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("שלח / עדכן", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            NotificationEngine.cancelNotification(context, currentConfig.id)
                            Toast.makeText(context, "התראה #${currentConfig.id} נמחקה מהווילון", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("מחק לפי ID", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = scheduleSeconds,
                        onValueChange = { scheduleSeconds = it },
                        label = { Text("שניות לתזמון") },
                        modifier = Modifier.width(110.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val secs = scheduleSeconds.toLongOrNull() ?: 10L
                            NotificationEngine.scheduleNotification(context, currentConfig, secs)
                            Toast.makeText(context, "ההתראה תעלה אוטומטית בעוד $secs שניות...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldActive)
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("תזמן התראה בעוד ${scheduleSeconds}ש'", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 1: Basic Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "1. פרטי תוכן ומזהה ייחודי (ID)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = currentConfig.title,
                            onValueChange = { onConfigChange(currentConfig.copy(title = it)) },
                            label = { Text("כותרת ההתראה (Title)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_title"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = currentConfig.id.toString(),
                            onValueChange = { onConfigChange(currentConfig.copy(id = it.toIntOrNull() ?: currentConfig.id)) },
                            label = { Text("מזהה (ID)") },
                            modifier = Modifier
                                .width(90.dp)
                                .testTag("input_id"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = currentConfig.content,
                        onValueChange = { onConfigChange(currentConfig.copy(content = it)) },
                        label = { Text("גוף ההתראה (Content / Text)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_content")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = currentConfig.subText,
                            onValueChange = { onConfigChange(currentConfig.copy(subText = it)) },
                            label = { Text("תת-כותרת (SubText)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = currentConfig.summaryText,
                            onValueChange = { onConfigChange(currentConfig.copy(summaryText = it)) },
                            label = { Text("סיכום (Summary)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Section 2: Style Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "2. סגנון ההתראה (Notification Style)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NotificationStyleType.entries.forEach { style ->
                            FilterChip(
                                selected = currentConfig.styleType == style,
                                onClick = { onConfigChange(currentConfig.copy(styleType = style)) },
                                label = { Text(style.titleHebrew, fontSize = 11.sp) }
                            )
                        }
                    }

                    when (currentConfig.styleType) {
                        NotificationStyleType.BIG_TEXT -> {
                            OutlinedTextField(
                                value = currentConfig.bigText,
                                onValueChange = { onConfigChange(currentConfig.copy(bigText = it)) },
                                label = { Text("טקסט מורחב (BigText)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                        }
                        NotificationStyleType.PROGRESS -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("התקדמות קבועה: ${currentConfig.progressCurrent}%")
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("טעינה מסתובבת", fontSize = 12.sp)
                                        Switch(
                                            checked = currentConfig.progressIndeterminate,
                                            onCheckedChange = { onConfigChange(currentConfig.copy(progressIndeterminate = it)) }
                                        )
                                    }
                                }
                                if (!currentConfig.progressIndeterminate) {
                                    Slider(
                                        value = currentConfig.progressCurrent.toFloat(),
                                        onValueChange = { onConfigChange(currentConfig.copy(progressCurrent = it.toInt())) },
                                        valueRange = 0f..100f
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        // Section 3: Interactive Action Buttons & Direct Reply
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "3. כפתורי פעולה ומענה ישיר (Actions)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = currentConfig.button1Enabled,
                            onCheckedChange = { onConfigChange(currentConfig.copy(button1Enabled = it)) }
                        )
                        OutlinedTextField(
                            value = currentConfig.button1Label,
                            onValueChange = { onConfigChange(currentConfig.copy(button1Label = it)) },
                            label = { Text("כפתור 1") },
                            modifier = Modifier.weight(1f),
                            enabled = currentConfig.button1Enabled,
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = currentConfig.button2Enabled,
                            onCheckedChange = { onConfigChange(currentConfig.copy(button2Enabled = it)) }
                        )
                        OutlinedTextField(
                            value = currentConfig.button2Label,
                            onValueChange = { onConfigChange(currentConfig.copy(button2Label = it)) },
                            label = { Text("כפתור 2") },
                            modifier = Modifier.weight(1f),
                            enabled = currentConfig.button2Enabled,
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = currentConfig.button3Enabled,
                            onCheckedChange = { onConfigChange(currentConfig.copy(button3Enabled = it)) }
                        )
                        OutlinedTextField(
                            value = currentConfig.button3Label,
                            onValueChange = { onConfigChange(currentConfig.copy(button3Label = it)) },
                            label = { Text("כפתור 3") },
                            modifier = Modifier.weight(1f),
                            enabled = currentConfig.button3Enabled,
                            singleLine = true
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = currentConfig.directReplyEnabled,
                                    onCheckedChange = { onConfigChange(currentConfig.copy(directReplyEnabled = it)) }
                                )
                                Text(
                                    text = "💬 שדה מענה ישיר (Direct Reply)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = RoyalPurple
                                )
                            }

                            if (currentConfig.directReplyEnabled) {
                                OutlinedTextField(
                                    value = currentConfig.directReplyLabel,
                                    onValueChange = { onConfigChange(currentConfig.copy(directReplyLabel = it)) },
                                    label = { Text("טקסט רמז בשדה ההקלדה") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Sensory & Persistence
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "4. עדיפות, צליל, רטט והתנהגות",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text("רמת עדיפות וחשיבות:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NotificationPriority.entries.forEach { prio ->
                            FilterChip(
                                selected = currentConfig.priority == prio,
                                onClick = { onConfigChange(currentConfig.copy(priority = prio)) },
                                label = { Text(prio.titleHebrew, fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("התראה קבועה (Ongoing / Sticky)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Switch(
                            checked = currentConfig.ongoing,
                            onCheckedChange = { onConfigChange(currentConfig.copy(ongoing = it)) }
                        )
                    }
                }
            }
        }
    }
}
