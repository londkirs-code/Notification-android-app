package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.ActionType
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.data.NotificationRepository
import com.example.data.NotificationStyleType
import com.example.engine.NotificationEngine
import com.example.receiver.NotificationActionReceiver
import com.example.ui.tabs.ActiveManagerTab
import com.example.ui.tabs.HistoryLogTab
import com.example.ui.tabs.PresetsTab
import com.example.ui.tabs.StudioTab
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalPurple
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.NotificationStudioViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize all notification channels
        NotificationEngine.initChannels(this)

        handleNotificationIntent(intent)

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    MainScreen()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        val notifId = intent.getIntExtra(NotificationActionReceiver.EXTRA_NOTIFICATION_ID, -1)
        val actionType = intent.getStringExtra(NotificationActionReceiver.EXTRA_ACTION_TYPE)
        if (notifId != -1 && actionType != null) {
            NotificationRepository.logAction(
                notificationId = notifId,
                actionType = ActionType.CONTENT_CLICK,
                actionLabel = "פתיחה מההתראה",
                note = "המשתמש הקיש על גוף ההתראה ונכנס לאפליקציה"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val studioViewModel: NotificationStudioViewModel = viewModel()

    val logs by NotificationRepository.logs.collectAsState()
    val activeIds by NotificationRepository.activeNotificationIds.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(RoyalPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "מלך ההתראות",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "NotifyKing Studio",
                                fontSize = 10.sp,
                                color = CrownGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    if (activeIds.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                NotificationEngine.cancelAll(context)
                                Toast.makeText(context, "כל ההתראות נוקו!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("clear_all_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "נקה את כל ההתראות",
                                tint = CoralAlert
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Studio Builder
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Build else Icons.Outlined.Build,
                            contentDescription = "מעבדת סטודיו"
                        )
                    },
                    label = { Text("סטודיו", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalPurple,
                        indicatorColor = RoyalPurple.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.testTag("tab_studio")
                )

                // Tab 1: Presets
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.AutoAwesome else Icons.Default.Notifications,
                            contentDescription = "תבניות מוכנות"
                        )
                    },
                    label = { Text("תבניות", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalPurple,
                        indicatorColor = RoyalPurple.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.testTag("tab_presets")
                )

                // Tab 2: Logs
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (logs.isNotEmpty()) {
                                    Badge(
                                        containerColor = EmeraldActive,
                                        contentColor = Color.White
                                    ) {
                                        Text("${logs.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.History else Icons.Outlined.History,
                                contentDescription = "יומן פעולות"
                            )
                        }
                    },
                    label = { Text("יומן תגובות", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalPurple,
                        indicatorColor = RoyalPurple.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.testTag("tab_history")
                )

                // Tab 3: Active Manager & Channels
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (activeIds.isNotEmpty()) {
                                    Badge(
                                        containerColor = CrownGold,
                                        contentColor = Color.Black
                                    ) {
                                        Text("${activeIds.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Default.Settings else Icons.Outlined.Settings,
                                contentDescription = "ניהול"
                            )
                        }
                    },
                    label = { Text("ניהול", fontSize = 12.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalPurple,
                        indicatorColor = RoyalPurple.copy(alpha = 0.18f)
                    ),
                    modifier = Modifier.testTag("tab_manager")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> StudioTab(viewModel = studioViewModel)
                1 -> PresetsTab(
                    onLoadInStudio = { presetConfig ->
                        studioViewModel.loadPreset(presetConfig)
                        selectedTab = 0
                    }
                )
                2 -> HistoryLogTab()
                3 -> ActiveManagerTab()
            }
        }
    }
}
