package com.example.ui.tabs

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ButtonActionType
import com.example.data.MockupElement
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioTab(
    currentConfig: NotificationConfig,
    onConfigChange: (NotificationConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedElement by remember { mutableStateOf<MockupElement?>(MockupElement.CONTENT) }

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
                            NotificationEngine.postNotification(context, currentConfig)
                            Toast.makeText(context, "ההתראה #${currentConfig.id} נשלחה בהצלחה למכשיר!", Toast.LENGTH_SHORT).show()
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
                            onConfigChange(currentConfig.copy(scheduledTimeMillis = timestamp))
                            NotificationEngine.scheduleNotification(context, currentConfig.copy(scheduledTimeMillis = timestamp), timestamp)
                            Toast.makeText(context, "ההתראה תוצג במועד שנקבע!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Section: Visual Interactive Preview
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
                                text = "תצוגה מקדימה חיה ואינטראקטיבית",
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
                                text = "הקש על פריט לעריכה",
                                fontSize = 11.sp,
                                color = CrownGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // The interactive mockup!
                    NotificationMockup(
                        config = currentConfig,
                        selectedElement = selectedElement,
                        onElementClick = { element ->
                            selectedElement = element
                            // If a disabled button or feature was clicked, enable it automatically!
                            when (element) {
                                MockupElement.BUTTON_1 -> if (!currentConfig.button1Enabled) onConfigChange(currentConfig.copy(button1Enabled = true))
                                MockupElement.BUTTON_2 -> if (!currentConfig.button2Enabled) onConfigChange(currentConfig.copy(button2Enabled = true))
                                MockupElement.BUTTON_3 -> if (!currentConfig.button3Enabled) onConfigChange(currentConfig.copy(button3Enabled = true))
                                MockupElement.DIRECT_REPLY -> if (!currentConfig.directReplyEnabled) onConfigChange(currentConfig.copy(directReplyEnabled = true))
                                MockupElement.PROGRESS -> if (currentConfig.styleType != NotificationStyleType.PROGRESS) onConfigChange(currentConfig.copy(styleType = NotificationStyleType.PROGRESS))
                                MockupElement.IMAGE -> if (currentConfig.styleType != NotificationStyleType.BIG_PICTURE) onConfigChange(currentConfig.copy(styleType = NotificationStyleType.BIG_PICTURE))
                                MockupElement.MEDIA -> if (currentConfig.styleType != NotificationStyleType.MEDIA) onConfigChange(currentConfig.copy(styleType = NotificationStyleType.MEDIA))
                                else -> {}
                            }
                        }
                    )
                }
            }
        }

        // Section: Quick Add / Switch Elements Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "רכיבי ההתראה (הקש להוספה או עריכה):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedElement == MockupElement.ICON,
                        onClick = { selectedElement = MockupElement.ICON },
                        label = { Text("👑 אייקון ותג", fontSize = 11.sp) }
                    )

                    FilterChip(
                        selected = selectedElement == MockupElement.CONTENT,
                        onClick = { selectedElement = MockupElement.CONTENT },
                        label = { Text("📝 כותרת וטקסט", fontSize = 11.sp) }
                    )

                    FilterChip(
                        selected = selectedElement == MockupElement.BUTTON_1 || selectedElement == MockupElement.BUTTON_2 || selectedElement == MockupElement.BUTTON_3,
                        onClick = {
                            selectedElement = if (!currentConfig.button1Enabled) {
                                onConfigChange(currentConfig.copy(button1Enabled = true))
                                MockupElement.BUTTON_1
                            } else if (!currentConfig.button2Enabled) {
                                onConfigChange(currentConfig.copy(button2Enabled = true))
                                MockupElement.BUTTON_2
                            } else {
                                MockupElement.BUTTON_1
                            }
                        },
                        label = {
                            val count = (if (currentConfig.button1Enabled) 1 else 0) +
                                    (if (currentConfig.button2Enabled) 1 else 0) +
                                    (if (currentConfig.button3Enabled) 1 else 0)
                            Text("🔘 כפתורים ($count/3)", fontSize = 11.sp)
                        }
                    )

                    FilterChip(
                        selected = selectedElement == MockupElement.DIRECT_REPLY,
                        onClick = {
                            if (!currentConfig.directReplyEnabled) {
                                onConfigChange(currentConfig.copy(directReplyEnabled = true))
                            }
                            selectedElement = MockupElement.DIRECT_REPLY
                        },
                        label = {
                            Text(
                                if (currentConfig.directReplyEnabled) "💬 מענה ישיר ✓" else "+ מענה ישיר",
                                fontSize = 11.sp
                            )
                        }
                    )

                    FilterChip(
                        selected = selectedElement == MockupElement.PROGRESS,
                        onClick = {
                            if (currentConfig.styleType != NotificationStyleType.PROGRESS) {
                                onConfigChange(currentConfig.copy(styleType = NotificationStyleType.PROGRESS))
                            }
                            selectedElement = MockupElement.PROGRESS
                        },
                        label = {
                            Text(
                                if (currentConfig.styleType == NotificationStyleType.PROGRESS) "📊 התקדמות ✓" else "+ התקדמות",
                                fontSize = 11.sp
                            )
                        }
                    )

                    FilterChip(
                        selected = selectedElement == MockupElement.IMAGE,
                        onClick = {
                            if (currentConfig.styleType != NotificationStyleType.BIG_PICTURE) {
                                onConfigChange(currentConfig.copy(styleType = NotificationStyleType.BIG_PICTURE))
                            }
                            selectedElement = MockupElement.IMAGE
                        },
                        label = {
                            Text(
                                if (currentConfig.styleType == NotificationStyleType.BIG_PICTURE) "🖼️ תמונה גדולה ✓" else "+ תמונה",
                                fontSize = 11.sp
                            )
                        }
                    )

                    FilterChip(
                        selected = selectedElement == MockupElement.MEDIA,
                        onClick = {
                            if (currentConfig.styleType != NotificationStyleType.MEDIA) {
                                onConfigChange(currentConfig.copy(styleType = NotificationStyleType.MEDIA))
                            }
                            selectedElement = MockupElement.MEDIA
                        },
                        label = {
                            Text(
                                if (currentConfig.styleType == NotificationStyleType.MEDIA) "🎵 נגן מדיה ✓" else "+ נגן מדיה",
                                fontSize = 11.sp
                            )
                        }
                    )

                    FilterChip(
                        selected = selectedElement == MockupElement.SENSORY,
                        onClick = { selectedElement = MockupElement.SENSORY },
                        label = { Text("🔔 צליל ורטט", fontSize = 11.sp) }
                    )

                    FilterChip(
                        selected = selectedElement == MockupElement.HEADER,
                        onClick = { selectedElement = MockupElement.HEADER },
                        label = { Text("⚙️ הגדרות מערכת", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Section: Active Element Inspector Card (Direct WYSIWYG Editing)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.5.dp, RoyalPurple.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Header of Inspector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "עורך רכיב: ${selectedElement?.titleHebrew ?: "בחר רכיב"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = RoyalPurple
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RoyalPurple.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "שינויים מוצגים מיידית למעלה 👆",
                                fontSize = 10.sp,
                                color = RoyalPurple,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Content based on selected element
                    when (selectedElement) {
                        MockupElement.ICON -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("בחר אייקון עבור ההתראה מתוך המאגר:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                                val iconsList = listOf(
                                    "CROWN" to "כתר מלוכה 👑",
                                    "BELL" to "פעמון התראות 🔔",
                                    "STAR" to "כוכב מועדף ⭐",
                                    "SHIELD" to "מגן אבטחה 🛡️",
                                    "CHAT" to "הודעה / שיחה 💬",
                                    "MEDIA" to "נגן מדיה 🎵",
                                    "ALERT" to "התרעת אזהרה ⚠️",
                                    "CHECK" to "ביצוע תקין ✔️",
                                    "FIRE" to "אש ודחיפות 🔥",
                                    "HEART" to "בריאות ולב ❤️",
                                    "ROCKET" to "מהירות וטיל 🚀"
                                )

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    iconsList.forEach { (key, label) ->
                                        FilterChip(
                                            selected = currentConfig.iconKey == key,
                                            onClick = { onConfigChange(currentConfig.copy(iconKey = key)) },
                                            label = { Text(label, fontSize = 11.sp) }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("מספר תג (Badge count): ${currentConfig.badgeNumber}", fontSize = 12.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { onConfigChange(currentConfig.copy(badgeNumber = (currentConfig.badgeNumber - 1).coerceAtLeast(0))) },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                        ) {
                                            Text("-")
                                        }
                                        OutlinedButton(
                                            onClick = { onConfigChange(currentConfig.copy(badgeNumber = currentConfig.badgeNumber + 1)) },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                        ) {
                                            Text("+")
                                        }
                                    }
                                }
                            }
                        }

                        MockupElement.CONTENT -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = currentConfig.title,
                                    onValueChange = { onConfigChange(currentConfig.copy(title = it)) },
                                    label = { Text("כותרת ההתראה (Title)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = currentConfig.content,
                                    onValueChange = { onConfigChange(currentConfig.copy(content = it)) },
                                    label = { Text("תוכן ראשי (Content Text)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = currentConfig.bigText,
                                    onValueChange = { onConfigChange(currentConfig.copy(bigText = it)) },
                                    label = { Text("טקסט מורחב שנחשף בגרירה (BigText)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2
                                )
                            }
                        }

                        MockupElement.HEADER -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = currentConfig.subText,
                                        onValueChange = { onConfigChange(currentConfig.copy(subText = it)) },
                                        label = { Text("תת-כותרת (SubText / שם שירות)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = currentConfig.id.toString(),
                                        onValueChange = { onConfigChange(currentConfig.copy(id = it.toIntOrNull() ?: currentConfig.id)) },
                                        label = { Text("מזהה (ID)") },
                                        modifier = Modifier.width(90.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("התראה קבועה (Sticky / Ongoing)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("מונעת מהמשתמש להחליק ולסגור אותה", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(
                                        checked = currentConfig.ongoing,
                                        onCheckedChange = { onConfigChange(currentConfig.copy(ongoing = it)) }
                                    )
                                }

                                Text("עדיפות וחשיבות:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    NotificationPriority.entries.forEach { prio ->
                                        FilterChip(
                                            selected = currentConfig.priority == prio,
                                            onClick = { onConfigChange(currentConfig.copy(priority = prio)) },
                                            label = { Text(prio.titleHebrew, fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }
                        }

                        MockupElement.BUTTON_1, MockupElement.BUTTON_2, MockupElement.BUTTON_3 -> {
                            val buttonIndex = when (selectedElement) {
                                MockupElement.BUTTON_1 -> 1
                                MockupElement.BUTTON_2 -> 2
                                else -> 3
                            }
                            val isEnabled = when (buttonIndex) {
                                1 -> currentConfig.button1Enabled
                                2 -> currentConfig.button2Enabled
                                else -> currentConfig.button3Enabled
                            }
                            val label = when (buttonIndex) {
                                1 -> currentConfig.button1Label
                                2 -> currentConfig.button2Label
                                else -> currentConfig.button3Label
                            }
                            val action = when (buttonIndex) {
                                1 -> currentConfig.button1Action
                                2 -> currentConfig.button2Action
                                else -> currentConfig.button3Action
                            }
                            val url = when (buttonIndex) {
                                1 -> currentConfig.button1Url
                                2 -> currentConfig.button2Url
                                else -> currentConfig.button3Url
                            }
                            val pkg = when (buttonIndex) {
                                1 -> currentConfig.button1Package
                                2 -> currentConfig.button2Package
                                else -> currentConfig.button3Package
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("הפעל כפתור מס' $buttonIndex", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Switch(
                                        checked = isEnabled,
                                        onCheckedChange = { checked ->
                                            when (buttonIndex) {
                                                1 -> onConfigChange(currentConfig.copy(button1Enabled = checked))
                                                2 -> onConfigChange(currentConfig.copy(button2Enabled = checked))
                                                else -> onConfigChange(currentConfig.copy(button3Enabled = checked))
                                            }
                                        }
                                    )
                                }

                                if (isEnabled) {
                                    OutlinedTextField(
                                        value = label,
                                        onValueChange = { newLabel ->
                                            when (buttonIndex) {
                                                1 -> onConfigChange(currentConfig.copy(button1Label = newLabel))
                                                2 -> onConfigChange(currentConfig.copy(button2Label = newLabel))
                                                else -> onConfigChange(currentConfig.copy(button3Label = newLabel))
                                            }
                                        },
                                        label = { Text("טקסט הכפתור (Label)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Text("פעולה שתתבצע בלחיצה מתוך ההתראה:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        ButtonActionType.entries.forEach { actionType ->
                                            FilterChip(
                                                selected = action == actionType,
                                                onClick = {
                                                    when (buttonIndex) {
                                                        1 -> onConfigChange(currentConfig.copy(button1Action = actionType))
                                                        2 -> onConfigChange(currentConfig.copy(button2Action = actionType))
                                                        else -> onConfigChange(currentConfig.copy(button3Action = actionType))
                                                    }
                                                },
                                                label = { Text(actionType.titleHebrew, fontSize = 10.sp) }
                                            )
                                        }
                                    }

                                    if (action == ButtonActionType.OPEN_BROWSER) {
                                        OutlinedTextField(
                                            value = url,
                                            onValueChange = { newUrl ->
                                                when (buttonIndex) {
                                                    1 -> onConfigChange(currentConfig.copy(button1Url = newUrl))
                                                    2 -> onConfigChange(currentConfig.copy(button2Url = newUrl))
                                                    else -> onConfigChange(currentConfig.copy(button3Url = newUrl))
                                                }
                                            },
                                            label = { Text("הקלד כתובת אתר (URL)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }

                                    if (action == ButtonActionType.OPEN_APP) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            OutlinedTextField(
                                                value = pkg,
                                                onValueChange = { newPkg ->
                                                    when (buttonIndex) {
                                                        1 -> onConfigChange(currentConfig.copy(button1Package = newPkg))
                                                        2 -> onConfigChange(currentConfig.copy(button2Package = newPkg))
                                                        else -> onConfigChange(currentConfig.copy(button3Package = newPkg))
                                                    }
                                                },
                                                label = { Text("שם חבילה (Package Name)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                            Text("קיצורי דרך מהירים לאפליקציות פופולריות:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                listOf(
                                                    "וואטסאפ" to "com.whatsapp",
                                                    "יוטיוב" to "com.google.android.youtube",
                                                    "כרום" to "com.android.chrome",
                                                    "טלגרם" to "org.telegram.messenger",
                                                    "מצלמה" to "com.google.android.GoogleCamera"
                                                ).forEach { (appName, appPkg) ->
                                                    FilterChip(
                                                        selected = pkg == appPkg,
                                                        onClick = {
                                                            when (buttonIndex) {
                                                                1 -> onConfigChange(currentConfig.copy(button1Package = appPkg))
                                                                2 -> onConfigChange(currentConfig.copy(button2Package = appPkg))
                                                                else -> onConfigChange(currentConfig.copy(button3Package = appPkg))
                                                            }
                                                        },
                                                        label = { Text(appName, fontSize = 9.sp) }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        MockupElement.PROGRESS -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("מצב סרגל התקדמות פעיל", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Switch(
                                        checked = currentConfig.styleType == NotificationStyleType.PROGRESS,
                                        onCheckedChange = { checked ->
                                            onConfigChange(
                                                currentConfig.copy(
                                                    styleType = if (checked) NotificationStyleType.PROGRESS else NotificationStyleType.STANDARD
                                                )
                                            )
                                        }
                                    )
                                }

                                if (currentConfig.styleType == NotificationStyleType.PROGRESS) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("אחוז התקדמות: ${currentConfig.progressCurrent}%", fontSize = 12.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("מסתובב ללא יעד", fontSize = 11.sp)
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

                                    Text("מהירות סימולציה: x${"%.1f".format(currentConfig.progressSpeedFactor)}", fontSize = 12.sp)
                                    Slider(
                                        value = currentConfig.progressSpeedFactor,
                                        onValueChange = { onConfigChange(currentConfig.copy(progressSpeedFactor = it)) },
                                        valueRange = 0.1f..5.0f
                                    )
                                }
                            }
                        }

                        MockupElement.DIRECT_REPLY -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("הפעל תיבת מענה ישיר (RemoteInput)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Switch(
                                        checked = currentConfig.directReplyEnabled,
                                        onCheckedChange = { onConfigChange(currentConfig.copy(directReplyEnabled = it)) }
                                    )
                                }

                                if (currentConfig.directReplyEnabled) {
                                    OutlinedTextField(
                                        value = currentConfig.directReplyLabel,
                                        onValueChange = { onConfigChange(currentConfig.copy(directReplyLabel = it)) },
                                        label = { Text("טקסט רמז בתיבת ההקלדה") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = currentConfig.directReplyButtonLabel,
                                        onValueChange = { onConfigChange(currentConfig.copy(directReplyButtonLabel = it)) },
                                        label = { Text("תווית כפתור השליחה") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        MockupElement.MEDIA -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("מצב נגן מדיה (MediaStyle)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Switch(
                                        checked = currentConfig.styleType == NotificationStyleType.MEDIA,
                                        onCheckedChange = { checked ->
                                            onConfigChange(
                                                currentConfig.copy(
                                                    styleType = if (checked) NotificationStyleType.MEDIA else NotificationStyleType.STANDARD
                                                )
                                            )
                                        }
                                    )
                                }

                                if (currentConfig.styleType == NotificationStyleType.MEDIA) {
                                    OutlinedTextField(
                                        value = currentConfig.mediaTrackTitle,
                                        onValueChange = { onConfigChange(currentConfig.copy(mediaTrackTitle = it)) },
                                        label = { Text("שם השיר / רצועה") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = currentConfig.mediaArtist,
                                        onValueChange = { onConfigChange(currentConfig.copy(mediaArtist = it)) },
                                        label = { Text("שם האמן / יוצר") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        MockupElement.IMAGE -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("סגנון תמונה גדולה (BigPictureStyle):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("הצג תמונת נושא רחבה", fontSize = 13.sp)
                                    Switch(
                                        checked = currentConfig.styleType == NotificationStyleType.BIG_PICTURE,
                                        onCheckedChange = { checked ->
                                            onConfigChange(
                                                currentConfig.copy(
                                                    styleType = if (checked) NotificationStyleType.BIG_PICTURE else NotificationStyleType.STANDARD
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        MockupElement.SENSORY -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("דפוס רטט:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    VibrationPreset.entries.forEach { vib ->
                                        FilterChip(
                                            selected = currentConfig.vibration == vib,
                                            onClick = { onConfigChange(currentConfig.copy(vibration = vib)) },
                                            label = { Text(vib.titleHebrew, fontSize = 10.sp) }
                                        )
                                    }
                                }

                                Text("צליל התראה:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                    Text("התרע פעם אחת בלבד (Only Alert Once)", fontSize = 12.sp)
                                    Switch(
                                        checked = currentConfig.alertOnce,
                                        onCheckedChange = { onConfigChange(currentConfig.copy(alertOnce = it)) }
                                    )
                                }
                            }
                        }

                        null -> {
                            Text("הקש על כל רכיב בתצוגה המקדימה למעלה כדי לפתוח את ההגדרות שלו", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
