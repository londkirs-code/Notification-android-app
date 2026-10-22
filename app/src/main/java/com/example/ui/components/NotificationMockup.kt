package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MockupElement
import com.example.data.NotificationConfig
import com.example.data.NotificationStyleType
import com.example.ui.theme.CrownGold
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.RoyalPurple

fun getIconVector(iconKey: String): ImageVector {
    return when (iconKey) {
        "BELL" -> Icons.Default.Notifications
        "STAR" -> Icons.Default.Star
        "SHIELD" -> Icons.Default.Shield
        "CHAT" -> Icons.Default.Chat
        "MEDIA" -> Icons.Default.MusicNote
        "ALERT" -> Icons.Default.Warning
        "CHECK" -> Icons.Default.Check
        "FIRE" -> Icons.Default.Whatshot
        "HEART" -> Icons.Default.Favorite
        "ROCKET" -> Icons.Default.RocketLaunch
        else -> Icons.Default.Notifications
    }
}

@Composable
fun NotificationMockup(
    config: NotificationConfig,
    modifier: Modifier = Modifier,
    selectedElement: MockupElement? = null,
    onElementClick: (MockupElement) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("notification_mockup_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Small App Icon + App Name / SubText + Time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (selectedElement == MockupElement.HEADER) {
                            Modifier.border(BorderStroke(1.5.dp, RoyalPurple), RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onElementClick(MockupElement.HEADER) }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon (Clickable separately to open icon picker)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(RoyalPurple)
                        .then(
                            if (selectedElement == MockupElement.ICON) {
                                Modifier.border(BorderStroke(2.dp, CrownGold), CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onElementClick(MockupElement.ICON) },
                    contentAlignment = Alignment.Center
                ) {
                    if (config.iconKey == "CROWN") {
                        Text("👑", fontSize = 13.sp)
                    } else {
                        Icon(
                            imageVector = getIconVector(config.iconKey),
                            contentDescription = "בחר אייקון",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (config.subText.isNotBlank()) config.subText else "NotifyKing",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = " • עכשיו",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.weight(1f))

                if (config.ongoing) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = CrownGold.copy(alpha = 0.25f),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = "📌 קבוע",
                            fontSize = 10.sp,
                            color = CrownGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RoyalPurple.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "#${config.id}",
                        fontSize = 11.sp,
                        color = RoyalPurple,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body: Title + Text + Large Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (selectedElement == MockupElement.CONTENT) {
                            Modifier.border(BorderStroke(1.5.dp, RoyalPurple), RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onElementClick(MockupElement.CONTENT) }
                    .padding(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = config.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "ערוך טקסט",
                            tint = RoyalPurple.copy(alpha = 0.6f),
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = config.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = if (config.styleType == NotificationStyleType.BIG_TEXT) 6 else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (selectedElement == MockupElement.IMAGE) {
                                Modifier.border(BorderStroke(2.dp, CrownGold), RoundedCornerShape(8.dp))
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onElementClick(MockupElement.IMAGE) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon_1792631589963),
                        contentDescription = "תמונה גדולה",
                        modifier = Modifier.size(44.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // BigPicture Preview
            if (config.styleType == NotificationStyleType.BIG_PICTURE) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (selectedElement == MockupElement.IMAGE) {
                                Modifier.border(BorderStroke(2.dp, CrownGold), RoundedCornerShape(10.dp))
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onElementClick(MockupElement.IMAGE) }
                ) {
                    Image(
                        painter = painterResource(id = config.imageDrawableRes ?: R.drawable.notification_hero_1792631605007),
                        contentDescription = "תמונת נושא",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // BigText Preview
            if (config.styleType == NotificationStyleType.BIG_TEXT && config.bigText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (selectedElement == MockupElement.CONTENT) RoyalPurple else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onElementClick(MockupElement.CONTENT) }
                ) {
                    Text(
                        text = config.bigText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Inbox Lines Preview
            if (config.styleType == NotificationStyleType.INBOX) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onElementClick(MockupElement.CONTENT) }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        config.inboxLines.take(4).forEach { line ->
                            Text(
                                text = line,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Progress Bar Preview
            if (config.styleType == NotificationStyleType.PROGRESS) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (selectedElement == MockupElement.PROGRESS) {
                                Modifier.border(BorderStroke(1.5.dp, EmeraldActive), RoundedCornerShape(8.dp))
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onElementClick(MockupElement.PROGRESS) }
                        .padding(6.dp)
                ) {
                    if (config.progressIndeterminate) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = RoyalPurple
                        )
                    } else {
                        LinearProgressIndicator(
                            progress = { config.progressCurrent / config.progressMax.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = EmeraldActive
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (config.progressIndeterminate) "טוען... (הקש לעריכה)" else "${config.progressCurrent}% הושלמו (x${"%.1f".format(config.progressSpeedFactor)})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${config.progressCurrent} מתוך ${config.progressMax}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Media Controls Preview
            if (config.styleType == NotificationStyleType.MEDIA) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (selectedElement == MockupElement.MEDIA) {
                                Modifier.border(BorderStroke(1.5.dp, RoyalPurple), RoundedCornerShape(8.dp))
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onElementClick(MockupElement.MEDIA) }
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "הקודם",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RoyalPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "נגן",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "הבא",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Direct Reply Field Preview
            if (config.directReplyEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        if (selectedElement == MockupElement.DIRECT_REPLY) 1.5.dp else 1.dp,
                        if (selectedElement == MockupElement.DIRECT_REPLY) RoyalPurple else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onElementClick(MockupElement.DIRECT_REPLY) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = RoyalPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = config.directReplyLabel,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalPurple
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = config.directReplyButtonLabel,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons Row (Interactive & Clickable)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Button 1
                if (config.button1Enabled && config.button1Label.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onElementClick(MockupElement.BUTTON_1) },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .then(
                                if (selectedElement == MockupElement.BUTTON_1) {
                                    Modifier.border(BorderStroke(2.dp, RoyalPurple), RoundedCornerShape(8.dp))
                                } else {
                                    Modifier
                                }
                            ),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(config.button1Label, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clickable { onElementClick(MockupElement.BUTTON_1) }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("+ כפתור 1", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Button 2
                if (config.button2Enabled && config.button2Label.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onElementClick(MockupElement.BUTTON_2) },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .then(
                                if (selectedElement == MockupElement.BUTTON_2) {
                                    Modifier.border(BorderStroke(2.dp, RoyalPurple), RoundedCornerShape(8.dp))
                                } else {
                                    Modifier
                                }
                            ),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(config.button2Label, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clickable { onElementClick(MockupElement.BUTTON_2) }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("+ כפתור 2", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                // Button 3
                if (config.button3Enabled && config.button3Label.isNotBlank()) {
                    OutlinedButton(
                        onClick = { onElementClick(MockupElement.BUTTON_3) },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .then(
                                if (selectedElement == MockupElement.BUTTON_3) {
                                    Modifier.border(BorderStroke(2.dp, RoyalPurple), RoundedCornerShape(8.dp))
                                } else {
                                    Modifier
                                }
                            ),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(config.button3Label, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clickable { onElementClick(MockupElement.BUTTON_3) }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("+ כפתור 3", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
