package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.util.AccessibilityUtils
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockupElement
import com.example.data.NotificationConfig
import com.example.data.NotificationStyleType
import com.example.data.SoundPreset
import com.example.data.VibrationPreset
import com.example.ui.theme.CrownGold
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.RoyalPurple

data class ToolboxItem(
    val element: MockupElement,
    val iconEmoji: String,
    val title: String,
    val isEquipped: (NotificationConfig) -> Boolean,
    val onEquip: (NotificationConfig) -> NotificationConfig,
    val onUnequip: (NotificationConfig) -> NotificationConfig
)

val allToolboxItems = listOf(
    ToolboxItem(
        element = MockupElement.ICON,
        iconEmoji = "👑",
        title = "אייקון ראשי",
        isEquipped = { true },
        onEquip = { it },
        onUnequip = { it.copy(iconKey = "CROWN") }
    ),
    ToolboxItem(
        element = MockupElement.CONTENT,
        iconEmoji = "✍️",
        title = "כותרת ותוכן",
        isEquipped = { it.title.isNotBlank() || it.content.isNotBlank() },
        onEquip = {
            it.copy(
                title = if (it.title.isBlank()) "כותרת חדשה" else it.title,
                content = if (it.content.isBlank()) "הודעה מותאמת אישית" else it.content
            )
        },
        onUnequip = { it.copy(title = "הודעה", content = "") }
    ),
    ToolboxItem(
        element = MockupElement.IMAGE,
        iconEmoji = "🖼️",
        title = "תמונת נושא",
        isEquipped = { it.styleType == NotificationStyleType.BIG_PICTURE },
        onEquip = { it.copy(styleType = NotificationStyleType.BIG_PICTURE) },
        onUnequip = { it.copy(styleType = NotificationStyleType.STANDARD, imageUri = null) }
    ),
    ToolboxItem(
        element = MockupElement.IMAGE,
        iconEmoji = "📸",
        title = "תמונת צד",
        isEquipped = { !it.largeIconUri.isNullOrBlank() || it.largeIconDrawableRes != null },
        onEquip = {
            if (it.largeIconDrawableRes == null && it.largeIconUri == null) {
                it.copy(largeIconDrawableRes = com.example.R.drawable.app_icon_1792631589963)
            } else it
        },
        onUnequip = { it.copy(largeIconUri = null, largeIconDrawableRes = null) }
    ),
    ToolboxItem(
        element = MockupElement.BUTTON_1,
        iconEmoji = "🔘",
        title = "כפתור 1",
        isEquipped = { it.button1Enabled },
        onEquip = {
            it.copy(
                button1Enabled = true,
                button1Label = if (it.button1Label.isBlank()) "אישור וביצוע" else it.button1Label
            )
        },
        onUnequip = { it.copy(button1Enabled = false) }
    ),
    ToolboxItem(
        element = MockupElement.BUTTON_2,
        iconEmoji = "🔘",
        title = "כפתור 2",
        isEquipped = { it.button2Enabled },
        onEquip = {
            it.copy(
                button2Enabled = true,
                button2Label = if (it.button2Label.isBlank()) "פרטים נוספים" else it.button2Label
            )
        },
        onUnequip = { it.copy(button2Enabled = false) }
    ),
    ToolboxItem(
        element = MockupElement.BUTTON_3,
        iconEmoji = "🔘",
        title = "כפתור 3",
        isEquipped = { it.button3Enabled },
        onEquip = {
            it.copy(
                button3Enabled = true,
                button3Label = if (it.button3Label.isBlank()) "דחייה" else it.button3Label
            )
        },
        onUnequip = { it.copy(button3Enabled = false) }
    ),
    ToolboxItem(
        element = MockupElement.DIRECT_REPLY,
        iconEmoji = "💬",
        title = "מענה ישיר",
        isEquipped = { it.directReplyEnabled },
        onEquip = {
            it.copy(
                directReplyEnabled = true,
                directReplyLabel = if (it.directReplyLabel.isBlank()) "הקלד תשובה ישירה..." else it.directReplyLabel
            )
        },
        onUnequip = { it.copy(directReplyEnabled = false) }
    ),
    ToolboxItem(
        element = MockupElement.PROGRESS,
        iconEmoji = "📊",
        title = "סרגל התקדמות",
        isEquipped = { it.styleType == NotificationStyleType.PROGRESS },
        onEquip = {
            it.copy(
                styleType = NotificationStyleType.PROGRESS,
                progressCurrent = 45,
                progressMax = 100
            )
        },
        onUnequip = { it.copy(styleType = NotificationStyleType.STANDARD) }
    ),
    ToolboxItem(
        element = MockupElement.CONTENT,
        iconEmoji = "📜",
        title = "טקסט מורחב",
        isEquipped = { it.styleType == NotificationStyleType.BIG_TEXT },
        onEquip = {
            it.copy(
                styleType = NotificationStyleType.BIG_TEXT,
                bigText = if (it.bigText.isBlank()) "טקסט מורחב שמוצג בעת פתיחת ההתראה בווילון..." else it.bigText
            )
        },
        onUnequip = { it.copy(styleType = NotificationStyleType.STANDARD) }
    ),
    ToolboxItem(
        element = MockupElement.MEDIA,
        iconEmoji = "🎵",
        title = "נגן מדיה",
        isEquipped = { it.styleType == NotificationStyleType.MEDIA },
        onEquip = {
            it.copy(
                styleType = NotificationStyleType.MEDIA,
                mediaTrackTitle = if (it.mediaTrackTitle.isBlank()) "שיר מוביל" else it.mediaTrackTitle,
                mediaArtist = if (it.mediaArtist.isBlank()) "אמן" else it.mediaArtist
            )
        },
        onUnequip = { it.copy(styleType = NotificationStyleType.STANDARD) }
    ),
    ToolboxItem(
        element = MockupElement.HEADER,
        iconEmoji = "📌",
        title = "קבועה (Sticky)",
        isEquipped = { it.ongoing },
        onEquip = { it.copy(ongoing = true) },
        onUnequip = { it.copy(ongoing = false) }
    ),
    ToolboxItem(
        element = MockupElement.SENSORY,
        iconEmoji = "🔔",
        title = "צליל ורטט",
        isEquipped = { it.sound != SoundPreset.SILENT || it.vibration != VibrationPreset.NONE },
        onEquip = { it },
        onUnequip = { it.copy(sound = SoundPreset.SILENT, vibration = VibrationPreset.NONE) }
    )
)

@Composable
fun NotificationToolbox(
    config: NotificationConfig,
    activeEditMode: MockupElement?,
    onConfigChange: (NotificationConfig) -> Unit,
    onActiveEditModeChange: (MockupElement?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeCount = remember(config) {
        allToolboxItems.count { it.isEquipped(config) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("notification_toolbox_card"),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Slim Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "👗 פריטי הלבשה (הקש להוספה ועריכה):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = EmeraldActive.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "$activeCount מולבשים ✓",
                    fontSize = 10.sp,
                    color = EmeraldActive,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Single Low Horizontal Scrollable Shelf of Dress-Up Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
        ) {
            items(allToolboxItems, key = { it.title }) { item ->
                val isEquipped = item.isEquipped(config)
                val isCurrentlyEditing = activeEditMode == item.element

                ToolboxChip(
                    item = item,
                    isEquipped = isEquipped,
                    isEditing = isCurrentlyEditing,
                    onChipClick = {
                        if (!isEquipped) {
                            val newConfig = item.onEquip(config)
                            onConfigChange(newConfig)
                            if (AccessibilityUtils.configRequiresAccessibility(newConfig) && !AccessibilityUtils.isAccessibilityEnabled(context)) {
                                AccessibilityUtils.promptEnableAccessibility(context, item.title)
                            }
                        }
                        onActiveEditModeChange(item.element)
                    },
                    onRemoveClick = {
                        val newConfig = item.onUnequip(config)
                        onConfigChange(newConfig)
                        if (activeEditMode == item.element) {
                            onActiveEditModeChange(null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ToolboxChip(
    item: ToolboxItem,
    isEquipped: Boolean,
    isEditing: Boolean,
    onChipClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isEditing -> CrownGold
        isEquipped -> RoyalPurple
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val borderWidth = when {
        isEditing -> 1.8.dp
        isEquipped -> 1.2.dp
        else -> 0.8.dp
    }

    val backgroundColor = when {
        isEditing -> RoyalPurple.copy(alpha = 0.2f)
        isEquipped -> RoyalPurple.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        border = BorderStroke(borderWidth, borderColor),
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onChipClick)
            .testTag("toolbox_chip_${item.title}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = item.iconEmoji, fontSize = 14.sp)

            Text(
                text = item.title,
                fontSize = 11.sp,
                fontWeight = if (isEquipped || isEditing) FontWeight.Bold else FontWeight.Medium,
                color = if (isEditing) RoyalPurple else if (isEquipped) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isEditing) {
                Text(text = "✍️", fontSize = 10.sp)
            } else if (isEquipped) {
                if (item.element != MockupElement.ICON && item.element != MockupElement.CONTENT) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                            .clickable(onClick = onRemoveClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "הסר",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                } else {
                    Text(text = "✓", fontSize = 11.sp, color = EmeraldActive, fontWeight = FontWeight.Bold)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "הוסף",
                    tint = RoyalPurple,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
