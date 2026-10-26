package com.example.ui.tabs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.engine.MediaManager
import com.example.util.AccessibilityUtils
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.data.NotificationStyleType
import com.example.data.SoundPreset
import com.example.data.VibrationPreset
import com.example.engine.NotificationEngine
import com.example.ui.components.NotificationMockup
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.RoyalPurple

data class PresetItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val config: NotificationConfig
)

@Composable
fun PresetsTab(
    onLoadInStudio: (NotificationConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val presets = remember {
        listOf(
            PresetItem(
                title = "מענה ישיר מההתראה (Direct Reply)",
                description = "התראה בסגנון צ'אט הכוללת שדה הקלדה מובנה בתוך ההתראה ומענה מיידי",
                icon = Icons.Default.Chat,
                accentColor = RoyalPurple,
                config = NotificationConfig(
                    id = 201,
                    title = "אלכס (מפתח מערכת)",
                    content = "האם לאשר את פריסת העדכון החדש לשרת הייצור?",
                    subText = "הודעה ישירה",
                    summaryText = "צ'אט מבצעי",
                    priority = NotificationPriority.HIGH,
                    styleType = NotificationStyleType.MESSAGING,
                    button1Enabled = true,
                    button1Label = "אישור",
                    button2Enabled = true,
                    button2Label = "דחה",
                    button3Enabled = false,
                    directReplyEnabled = true,
                    directReplyLabel = "הקלד כאן תשובה ישירה...",
                    directReplyButtonLabel = "השב",
                    vibration = VibrationPreset.DEFAULT
                )
            ),
            PresetItem(
                title = "הורדת קובץ חיה עם התקדמות (0%-100%)",
                description = "מדמה הורדה חיה שמתקדמת בזמן אמת עם כפתור 'בטל הורדה' ומסיימת בהודעת הצלחה",
                icon = Icons.Default.Download,
                accentColor = EmeraldActive,
                config = NotificationConfig(
                    id = 202,
                    title = "מוריד קובץ עדכון מערכת",
                    content = "קובץ Firmware_v4.8.bin מתקדם...",
                    subText = "הורדה ברקע",
                    priority = NotificationPriority.LOW,
                    styleType = NotificationStyleType.PROGRESS,
                    progressMax = 100,
                    progressCurrent = 0,
                    progressSimulateLive = true,
                    ongoing = true,
                    button1Enabled = false,
                    button2Enabled = false,
                    button3Enabled = false,
                    directReplyEnabled = false
                )
            ),
            PresetItem(
                title = "התראת חירום מתפרצת (Heads-Up)",
                description = "עדיפות עליונה MAX, באנר מתפרץ בראש המסך, צליל אזעקה, נורית אדומה ורטט SOS",
                icon = Icons.Default.Warning,
                accentColor = CoralAlert,
                config = NotificationConfig(
                    id = 203,
                    title = "🚨 התרעת אבטחה דחופה!",
                    content = "זוהה ניסיון גישה בלתי מורשה למערכת בשעה 18:24. נא לפעול מיידית.",
                    subText = "מרכז אבטחה",
                    priority = NotificationPriority.URGENT,
                    styleType = NotificationStyleType.BIG_TEXT,
                    bigText = "מיקום ניסיון הגישה: שרת DB-Production-01. כתובת IP חריגה: 198.51.100.44. פעולה מומלצת: חסימת פורט ואימות דו-שלבי מיידי.",
                    sound = SoundPreset.ALARM,
                    vibration = VibrationPreset.SOS,
                    ledColorHex = "#EF4444",
                    button1Enabled = true,
                    button1Label = "נעל גישה עכשיו",
                    button2Enabled = true,
                    button2Label = "התעלם (בטוח)",
                    button3Enabled = false,
                    directReplyEnabled = false
                )
            ),
            PresetItem(
                title = "מלך ההתראות המלא (3 כפתורים + מענה)",
                description = "כל היכולות ביחד: 3 כפתורי פעולה שונים, שדה מענה ישיר, טקסט מורחב וקביעות",
                icon = Icons.Default.Send,
                accentColor = CrownGold,
                config = NotificationConfig(
                    id = 204,
                    title = "👑 משימה דחופה ממלך ההתראות",
                    content = "יש לבחור אחת מ-3 הפעולות או להקליד מענה חופשי בתיבה.",
                    subText = "NotifyKing המלא",
                    summaryText = "מרכז שליטה",
                    priority = NotificationPriority.HIGH,
                    styleType = NotificationStyleType.BIG_TEXT,
                    bigText = "מערכת ההתראות תומכת בעד 3 לחצני פעולה בו זמנית, לצד שדה RemoteInput להזנת טקסט חופשי. כל לחיצה מתועדת מיידית ביומן הפעולות!",
                    button1Enabled = true,
                    button1Label = "בצע פעולה 1",
                    button2Enabled = true,
                    button2Label = "העבר לפעולה 2",
                    button3Enabled = true,
                    button3Label = "סגור פעולה 3",
                    directReplyEnabled = true,
                    directReplyLabel = "הקלד פקודה ישירה...",
                    directReplyButtonLabel = "שלח",
                    ongoing = false,
                    autoCancel = true
                )
            ),
            PresetItem(
                title = "נגן מוזיקה ומדיה אינטראקטיבי",
                description = "פקדי שמע מלאים: נגן, השהה, קדימה, אחורה - מתעדכנים בלחיצה בזמן אמת",
                icon = Icons.Default.MusicNote,
                accentColor = CyanAccent,
                config = NotificationConfig(
                    id = 205,
                    title = MediaManager.currentTrackTitle.value,
                    content = MediaManager.currentArtist.value,
                    subText = "מנגן כעת",
                    priority = NotificationPriority.LOW,
                    styleType = NotificationStyleType.MEDIA,
                    mediaTrackTitle = MediaManager.currentTrackTitle.value,
                    mediaArtist = MediaManager.currentArtist.value,
                    ongoing = true,
                    button1Enabled = false,
                    button2Enabled = false,
                    button3Enabled = false,
                    directReplyEnabled = false
                )
            ),
            PresetItem(
                title = "כרזת תמונה גדולה (BigPictureStyle)",
                description = "מציגה תמונה פנורמית עשירה עם כפתורי שמירה ושיתוף",
                icon = Icons.Default.Image,
                accentColor = RoyalPurple,
                config = NotificationConfig(
                    id = 206,
                    title = "📸 תמונת היום: עיר הסייבר",
                    content = "הרחב את ההתראה כדי לצפות בתמונה המלאה ובפרטי הצילום.",
                    subText = "גלריה חכמה",
                    summaryText = "צילום עירוני",
                    priority = NotificationPriority.DEFAULT,
                    styleType = NotificationStyleType.BIG_PICTURE,
                    imageDrawableRes = R.drawable.notification_hero_1792631605007,
                    button1Enabled = true,
                    button1Label = "שמור לגלריה",
                    button2Enabled = true,
                    button2Label = "שתף קישור",
                    button3Enabled = false,
                    directReplyEnabled = false
                )
            ),
            PresetItem(
                title = "רשימת אינבוקס מרובת שורות (InboxStyle)",
                description = "הצגת פריטי רשימה מרובים בצורה מסודרת ונקייה בתוך כרטיס ההתראה",
                icon = Icons.Default.Inbox,
                accentColor = CrownGold,
                config = NotificationConfig(
                    id = 207,
                    title = "📋 דוח פקודות יומי",
                    content = "4 משימות מתוזמנות בוצעו בהצלחה",
                    subText = "יומן מערכת",
                    summaryText = "סיכום שבועי",
                    priority = NotificationPriority.DEFAULT,
                    styleType = NotificationStyleType.INBOX,
                    inboxLines = listOf(
                        "✔ גיבוי מסד נתונים הושלם (120MB)",
                        "✔ 42 התראות נשלחו למשתמשים",
                        "✔ תקינות שרתים: 100% ללא תקלות",
                        "• משימת ניקוי הבאה: 02:00 בלילה"
                    ),
                    button1Enabled = true,
                    button1Label = "פתח דוח",
                    button2Enabled = true,
                    button2Label = "הורד קובץ",
                    button3Enabled = false,
                    directReplyEnabled = false
                )
            ),
            PresetItem(
                title = "שעון עצר רץ חי (Chronometer)",
                description = "מציג זמן שרץ בזמן אמת (סופר כלפי מעלה או לאחור) ישירות בתוך ההתראה",
                icon = Icons.Default.Timer,
                accentColor = EmeraldActive,
                config = NotificationConfig(
                    id = 208,
                    title = "⏱️ מודד זמן פעילות",
                    content = "המדידה מתבצעת כעת ברקע...",
                    subText = "שעון פעיל",
                    priority = NotificationPriority.LOW,
                    styleType = NotificationStyleType.CHRONOMETER,
                    ongoing = true,
                    button1Enabled = true,
                    button1Label = "עצור שעון",
                    button2Enabled = false,
                    button3Enabled = false,
                    directReplyEnabled = false
                )
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("presets_tab_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(RoyalPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "מעבדת תבניות ההתראה המהירות",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "בחר כל סוג התראה לבדיקה מיידית או הרחב כדי לראות תצוגה מקדימה ופקודת Termux",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(presets, key = { it.config.id }) { preset ->
            PresetCard(
                preset = preset,
                onSend = {
                    if (AccessibilityUtils.configRequiresAccessibility(preset.config) && !AccessibilityUtils.isAccessibilityEnabled(context)) {
                        AccessibilityUtils.promptEnableAccessibility(context, preset.title)
                    } else {
                        NotificationEngine.postNotification(context, preset.config)
                        Toast.makeText(context, "ההתראה נשלחה! בדוק את וילון ההתראות", Toast.LENGTH_SHORT).show()
                    }
                },
                onCopyTermux = {
                    val cmd = NotificationEngine.generateTermuxCommand(preset.config)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Termux Command", cmd))
                    Toast.makeText(context, "פקודת Termux הועתקה ללוח!", Toast.LENGTH_SHORT).show()
                },
                onEdit = {
                    onLoadInStudio(preset.config)
                    if (AccessibilityUtils.configRequiresAccessibility(preset.config) && !AccessibilityUtils.isAccessibilityEnabled(context)) {
                        AccessibilityUtils.promptEnableAccessibility(context, preset.title)
                    }
                }
            )
        }

        item {
            // Bundle group sender
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CyanAccent.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📦", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "צרור התראות מאוחד (Group Bundle)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "שולח 3 התראות נפרדות + כרטיס סיכום כדי שאנדרואיד יאגד אותן לתיקייה אחת",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val groupKey = "queen_bundle_group"
                            NotificationEngine.postNotification(
                                context,
                                NotificationConfig(id = 301, title = "הודעה 1: כרטיס אשראי חויב", content = "₪145.00 בסופרמרקט", groupKey = groupKey)
                            )
                            NotificationEngine.postNotification(
                                context,
                                NotificationConfig(id = 302, title = "הודעה 2: משלוח יצא לדרך", content = "חבילה מגיעה היום עד 20:00", groupKey = groupKey)
                            )
                            NotificationEngine.postNotification(
                                context,
                                NotificationConfig(id = 303, title = "הודעה 3: תזכורת פגישה", content = "צוות פיתוח בעוד 15 דקות", groupKey = groupKey)
                            )
                            NotificationEngine.postNotification(
                                context,
                                NotificationConfig(id = 300, title = "3 התראות חדשות", content = "התראות מקובצות יחד", groupKey = groupKey, isGroupSummary = true)
                            )
                            Toast.makeText(context, "נשלח צרור של 3 התראות מקובצות!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("שלח צרור התראות מקובץ עכשיו")
                    }
                }
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: PresetItem,
    onSend: () -> Unit,
    onCopyTermux: () -> Unit,
    onEdit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(preset.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = preset.icon,
                        contentDescription = null,
                        tint = preset.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preset.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = preset.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "הסתר תצוגה מקדימה" else "הצג תצוגה מקדימה",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "תצוגה מקדימה מדומה:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    NotificationMockup(config = preset.config)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Code snippet preview
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = NotificationEngine.generateTermuxCommand(preset.config),
                                fontSize = 10.sp,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSend,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(40.dp)
                        .testTag("send_preset_${preset.config.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = preset.accentColor),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "שלח עכשיו",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onCopyTermux,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Termux",
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ערוך",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
