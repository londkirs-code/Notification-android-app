package com.example.ui.tabs

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.NotificationRepository
import com.example.engine.NotificationEngine
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.RoyalPurple

@Composable
fun ActiveManagerTab(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeIds by NotificationRepository.activeNotificationIds.collectAsState()

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "הרשאת התראות אושרה בהצלחה!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "ההרשאה נדחתה. לא ניתן להציג התראות.", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("active_manager_tab"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Accessibility Permission Status Card
        item {
            val accessibilityManager = remember(context) {
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
            }
            val isAccessibilityEnabled by remember(accessibilityManager) {
                mutableStateOf(accessibilityManager.isEnabled)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAccessibilityEnabled) EmeraldActive.copy(alpha = 0.12f) else CoralAlert.copy(alpha = 0.12f)
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isAccessibilityEnabled) EmeraldActive else CoralAlert),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAccessibilityEnabled) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAccessibilityEnabled) "הרשאת נגישות פעילה ומאושרת" else "נדרשת הרשאת נגישות לפעולות מתקדמות",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAccessibilityEnabled) "האפליקציה יכולה להפעיל פעולות מערכת גלובליות" else "יש להפעיל את הרשאת הנגישות בהגדרות המכשיר",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!isAccessibilityEnabled) {
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAlert)
                        ) {
                            Text("הפעל נגישות", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Permission Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasNotificationPermission) EmeraldActive.copy(alpha = 0.12f) else CoralAlert.copy(alpha = 0.12f)
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (hasNotificationPermission) EmeraldActive else CoralAlert),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hasNotificationPermission) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasNotificationPermission) "הרשאת התראות פעילה ומאושרת" else "חסרה הרשאת התראות במכשיר",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (hasNotificationPermission) "האפליקציה מורשית לשלוח את כל סוגי ההתראות" else "יש לאשר הרשאה כדי לאפשר הצגת התראות במכשיר",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralAlert)
                        ) {
                            Text("אשר עכשיו", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Active Notifications in Drawer
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
                        Column {
                            Text(
                                text = "התראות פעילות כעת (${activeIds.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "התראות שנוצרו ועדיין פתוחות בווילון",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (activeIds.isNotEmpty()) {
                            Button(
                                onClick = {
                                    NotificationEngine.cancelAll(context)
                                    Toast.makeText(context, "כל ההתראות נוקו!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("נקה הכל", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (activeIds.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "אין כרגע התראות פעילות שרשומות באפליקציה.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeIds.forEach { id ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "מזהה התראה #${id}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.weight(1f)
                                        )

                                        OutlinedButton(
                                            onClick = {
                                                NotificationEngine.cancelNotification(context, id)
                                                Toast.makeText(context, "התראה #$id בוטלה", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("בטל", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Notification Channels & System Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ערוצי התראות מערכתיים (Channels)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "מוגדרים לפי עדיפויות וחשיבות באנדרואיד",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "הגדרות מערכת", tint = RoyalPurple)
                        }
                    }

                    val channelsInfo = listOf(
                        "התראות דחופות (queen_urgent)" to "עדיפות עליונה, Heads-up מתפרץ ורטט מוגבר",
                        "התראות חשובות (queen_high)" to "צליל ורטט פעילים",
                        "התראות רגילות (queen_default)" to "צליל סטנדרטי במגש ההתראות",
                        "התראות שקטות (queen_low)" to "ללא צליל וללא רטט",
                        "שיחות והודעות (queen_chat)" to "מותאם למענה ישיר וסגנון MessagingStyle",
                        "מדיה והתקדמות (queen_media)" to "נגן מוזיקה וסרגלי התקדמות של תהליכים"
                    )

                    channelsInfo.forEach { (name, desc) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = RoyalPurple)
                                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Termux Reference & Cheat Sheet Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = CrownGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "מדריך פרמטרי termux-notification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "פקודת termux-notification באנדרואיד מאפשרת שליחת התראות מסקריפטים עם הדגלים הבאים:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val cheatsheet = listOf(
                        "--id <id>" to "מזהה ייחודי להחלפת ההתראה או ביטולה",
                        "--title <str>" to "כותרת ראשית של ההתראה",
                        "--content <str>" to "גוף הטקסט הראשי",
                        "--prio <high|max|default|low|min>" to "עדיפות ההתראה",
                        "--button1,2,3 <label>" to "תווית כפתור פעולה 1, 2 או 3",
                        "--button1,2,3-action <cmd>" to "פקודת Bash לביצוע בלחיצה",
                        "--ongoing" to "התראה קבועה שלא ניתנת להחלקה",
                        "--alert-once" to "השמעת צליל רק בעדכון הראשון",
                        "--vibrate <p1,p2...>" to "דפוס רטט במילישניות (לדוגמה: 0,200,100,200)",
                        "--led-color <rrggbb>" to "צבע נורית חיווי לד במכשירים תומכים",
                        "--sound <true|false>" to "הפעלה או ביטול צליל התראה",
                        "--group <key>" to "מפתח קיבוץ לאיגוד צרור התראות"
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            cheatsheet.forEach { (flag, explanation) ->
                                Row {
                                    Text(flag, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 11.sp, color = CrownGold, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("- $explanation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
