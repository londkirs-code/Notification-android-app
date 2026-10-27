package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import android.widget.Toast
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.engine.MediaManager
import com.example.util.AccessibilityUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.ButtonActionType
import com.example.data.MockupElement
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.data.NotificationStyleType
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CrownGold
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.RoyalPurple
import com.example.util.ImageUtils

data class PresetImageItem(
    val titleHebrew: String,
    val drawableRes: Int
)

val availablePresetImages = listOf(
    PresetImageItem("כתר זהב מלכותי 👑", R.drawable.notification_hero_1792631605007),
    PresetImageItem("סייבר קינג ניאון ⚡", R.drawable.banner_neon_king_1792875263105),
    PresetImageItem("אוואטר כתר 3D 💎", R.drawable.avatar_cyber_crown_1792875275018),
    PresetImageItem("אייקון האפליקציה 📱", R.drawable.app_icon_1792631589963)
)

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

val availableIcons = listOf(
    "CROWN" to "👑",
    "BELL" to "🔔",
    "STAR" to "⭐",
    "SHIELD" to "🛡️",
    "CHAT" to "💬",
    "MEDIA" to "🎵",
    "ALERT" to "⚠️",
    "CHECK" to "✔️",
    "FIRE" to "🔥",
    "HEART" to "❤️",
    "ROCKET" to "🚀"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotificationMockup(
    config: NotificationConfig,
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    onConfigChange: ((NotificationConfig) -> Unit)? = null,
    selectedElement: MockupElement? = null,
    onElementClick: ((MockupElement) -> Unit)? = null,
    activeEditMode: MockupElement? = null,
    onActiveEditModeChange: ((MockupElement?) -> Unit)? = null
) {
    val context = LocalContext.current
    var internalActiveEditMode by remember { mutableStateOf<MockupElement?>(null) }
    val effectiveEditMode = activeEditMode ?: internalActiveEditMode

    val setEditMode: (MockupElement?) -> Unit = { mode ->
        internalActiveEditMode = mode
        onActiveEditModeChange?.invoke(mode)
        if (mode != null) onElementClick?.invoke(mode)
    }

    val isIconBubbleOpen = effectiveEditMode == MockupElement.ICON
    val isImageBubbleOpen = effectiveEditMode == MockupElement.IMAGE
    val isProgressOpen = effectiveEditMode == MockupElement.PROGRESS
    val isContentOpen = effectiveEditMode == MockupElement.CONTENT
    val isDirectReplyOpen = effectiveEditMode == MockupElement.DIRECT_REPLY
    val isMediaOpen = effectiveEditMode == MockupElement.MEDIA
    val activeEditingButton = when (effectiveEditMode) {
        MockupElement.BUTTON_1 -> 1
        MockupElement.BUTTON_2 -> 2
        MockupElement.BUTTON_3 -> 3
        else -> null
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = ImageUtils.savePickedImageLocally(context, uri)
            onConfigChange?.invoke(
                config.copy(
                    imageUri = savedPath,
                    imageDrawableRes = null,
                    styleType = if (config.styleType != NotificationStyleType.BIG_PICTURE &&
                        config.styleType != NotificationStyleType.PROGRESS &&
                        config.styleType != NotificationStyleType.MEDIA)
                        NotificationStyleType.BIG_PICTURE else config.styleType
                )
            )
        }
    }

    val audioFilesPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            val count = MediaManager.loadFromUris(context, uris)
            Toast.makeText(context, "נטענו $count שירים בהצלחה מהסייר!", Toast.LENGTH_SHORT).show()
            val current = MediaManager.playlist.value.firstOrNull()
            if (current != null) {
                onConfigChange?.invoke(
                    config.copy(
                        styleType = NotificationStyleType.MEDIA,
                        mediaTrackTitle = current.title,
                        mediaArtist = current.artist
                    )
                )
            }
        }
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { treeUri ->
        if (treeUri != null) {
            val count = MediaManager.loadFromFolder(context, treeUri)
            if (count > 0) {
                Toast.makeText(context, "נטענו $count שירים מהתיקייה!", Toast.LENGTH_SHORT).show()
                val current = MediaManager.playlist.value.firstOrNull()
                if (current != null) {
                    onConfigChange?.invoke(
                        config.copy(
                            styleType = NotificationStyleType.MEDIA,
                            mediaTrackTitle = current.title,
                            mediaArtist = current.artist
                        )
                    )
                }
            } else {
                Toast.makeText(context, "לא נמצאו קבצי שמע בתיקייה שנבחרה", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                .padding(10.dp)
        ) {
            // Header Row: App Icon + In-place Subtext + In-place Ongoing & Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Circle (Clicking opens an in-place scrollable bubble directly attached)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(RoyalPurple)
                        .then(
                            if (editable) {
                                Modifier
                                    .border(BorderStroke(1.5.dp, CrownGold), CircleShape)
                                    .clickable {
                                        setEditMode(if (effectiveEditMode == MockupElement.ICON) null else MockupElement.ICON)
                                    }
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (config.iconKey == "CROWN") {
                        Text("👑", fontSize = 15.sp)
                    } else {
                        Icon(
                            imageVector = getIconVector(config.iconKey),
                            contentDescription = "אייקון",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // In-place Subtext (App Name in header)
                if (editable && onConfigChange != null) {
                    BasicTextField(
                        value = config.subText,
                        onValueChange = { onConfigChange(config.copy(subText = it)) },
                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        cursorBrush = SolidColor(RoyalPurple),
                        singleLine = true,
                        modifier = Modifier.width(110.dp)
                    )
                } else {
                    Text(
                        text = if (config.subText.isNotBlank()) config.subText else "NotifyKing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = " • עכשיו",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Ongoing / Sticky Tag (In-place clickable toggle)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (config.ongoing) CrownGold.copy(alpha = 0.25f) else Color.Transparent,
                    border = if (!config.ongoing && editable) BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .then(
                            if (editable && onConfigChange != null) {
                                Modifier.clickable { onConfigChange(config.copy(ongoing = !config.ongoing)) }
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Text(
                        text = if (config.ongoing) "📌 קבוע" else if (editable) "+ קבוע" else "",
                        fontSize = 10.sp,
                        color = if (config.ongoing) CrownGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // ID Tag
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

            // IN-PLACE ICON BUBBLE (Opens directly when tapping the icon circle)
            AnimatedVisibility(
                visible = isIconBubbleOpen && editable,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.5.dp, CrownGold),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎨 בחר אייקון להודעה מתוך הרשימה:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(
                                onClick = { setEditMode(null) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "סגור", modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Scrollable bubble of icons
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(availableIcons) { (key, emoji) ->
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (config.iconKey == key) RoyalPurple else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .border(
                                            BorderStroke(
                                                if (config.iconKey == key) 2.dp else 1.dp,
                                                if (config.iconKey == key) CrownGold else MaterialTheme.colorScheme.outlineVariant
                                            ),
                                            CircleShape
                                        )
                                        .clickable {
                                            onConfigChange?.invoke(config.copy(iconKey = key))
                                            setEditMode(null)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body: In-place Editable Title + In-place Editable Content + Large Thumbnail
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (editable) {
                                Modifier.clickable {
                                    setEditMode(if (effectiveEditMode == MockupElement.CONTENT) null else MockupElement.CONTENT)
                                }
                            } else Modifier
                        )
                ) {
                    // IN-PLACE EDITABLE TITLE
                    if (editable && onConfigChange != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BasicTextField(
                                value = config.title,
                                onValueChange = { onConfigChange(config.copy(title = it)) },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(RoyalPurple),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "ערוך כותרת",
                                tint = RoyalPurple.copy(alpha = 0.5f),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    } else {
                        Text(
                            text = config.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // IN-PLACE EDITABLE CONTENT
                    if (editable && onConfigChange != null) {
                        BasicTextField(
                            value = config.content,
                            onValueChange = { onConfigChange(config.copy(content = it)) },
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
                            ),
                            cursorBrush = SolidColor(RoyalPurple),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = config.content,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            maxLines = if (config.styleType == NotificationStyleType.BIG_TEXT) 6 else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Large App Icon / Thumbnail
                val currentThumbnailModel: Any = when {
                    !config.largeIconUri.isNullOrBlank() -> config.largeIconUri
                    config.largeIconDrawableRes != null && config.largeIconDrawableRes != 0 -> config.largeIconDrawableRes
                    !config.imageUri.isNullOrBlank() -> config.imageUri
                    config.imageDrawableRes != null && config.imageDrawableRes != 0 -> config.imageDrawableRes
                    else -> R.drawable.app_icon_1792631589963
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (editable) {
                                Modifier
                                    .border(BorderStroke(1.5.dp, CrownGold), RoundedCornerShape(8.dp))
                                    .clickable {
                                        setEditMode(if (effectiveEditMode == MockupElement.IMAGE) null else MockupElement.IMAGE)
                                    }
                            } else {
                                Modifier
                            }
                        )
                ) {
                    val thumbnailRequest = remember(currentThumbnailModel) {
                        ImageRequest.Builder(context)
                            .data(currentThumbnailModel)
                            .size(128, 128)
                            .crossfade(false)
                            .build()
                    }
                    AsyncImage(
                        model = thumbnailRequest,
                        contentDescription = "תמונה גדולה",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Image Chooser Bubble (In-place image & photo gallery picker)
            AnimatedVisibility(
                visible = isImageBubbleOpen && editable,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.5.dp, CrownGold),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🖼️ בחר תמונה להודעה (מהמאגר או מהמכשיר):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple
                            )
                            IconButton(onClick = { setEditMode(null) }, modifier = Modifier.size(22.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "סגור", modifier = Modifier.size(15.dp))
                            }
                        }

                        // Upload button from device
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("העלה תמונה מהמכשיר 📸", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (!config.imageUri.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldActive.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("נבחרה תמונה מהמכשיר ✓", fontSize = 10.sp, color = EmeraldActive, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "הסר תמונה מהמכשיר",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable {
                                                    onConfigChange?.invoke(config.copy(imageUri = null))
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        Text("תמונות שמורות באפליקציה לבחירה מהירה:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                        // Horizontal list of bundled presets
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(availablePresetImages) { preset ->
                                val isSelected = config.imageUri.isNullOrBlank() && (config.imageDrawableRes == preset.drawableRes || (config.imageDrawableRes == null && preset.drawableRes == R.drawable.notification_hero_1792631605007))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) CrownGold else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .width(105.dp)
                                        .clickable {
                                            onConfigChange?.invoke(
                                                config.copy(
                                                    imageDrawableRes = preset.drawableRes,
                                                    imageUri = null,
                                                    styleType = if (config.styleType != NotificationStyleType.BIG_PICTURE &&
                                                        config.styleType != NotificationStyleType.PROGRESS &&
                                                        config.styleType != NotificationStyleType.MEDIA)
                                                        NotificationStyleType.BIG_PICTURE else config.styleType
                                                )
                                            )
                                        }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        val presetReq = remember(preset.drawableRes) {
                                            ImageRequest.Builder(context)
                                                .data(preset.drawableRes)
                                                .size(200, 110)
                                                .crossfade(false)
                                                .build()
                                        }
                                        AsyncImage(
                                            model = presetReq,
                                            contentDescription = preset.titleHebrew,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(55.dp)
                                                .clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = preset.titleHebrew,
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) RoyalPurple else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        // Style Toggle Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = config.styleType == NotificationStyleType.BIG_PICTURE,
                                onClick = {
                                    onConfigChange?.invoke(
                                        config.copy(
                                            styleType = if (config.styleType == NotificationStyleType.BIG_PICTURE)
                                                NotificationStyleType.STANDARD else NotificationStyleType.BIG_PICTURE
                                        )
                                    )
                                },
                                label = { Text("תמונת נושא רחבה (BigPicture)", fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }

            // BigPicture Hero Banner
            if (config.styleType == NotificationStyleType.BIG_PICTURE) {
                Spacer(modifier = Modifier.height(8.dp))
                val currentHeroModel: Any = when {
                    !config.imageUri.isNullOrBlank() -> config.imageUri
                    config.imageDrawableRes != null && config.imageDrawableRes != 0 -> config.imageDrawableRes
                    else -> R.drawable.notification_hero_1792631605007
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (editable) {
                                Modifier
                                    .border(BorderStroke(1.dp, CrownGold), RoundedCornerShape(10.dp))
                                    .clickable {
                                        setEditMode(if (effectiveEditMode == MockupElement.IMAGE) null else MockupElement.IMAGE)
                                    }
                            } else {
                                Modifier
                            }
                        )
                ) {
                    val heroReq = remember(currentHeroModel) {
                        ImageRequest.Builder(context)
                            .data(currentHeroModel)
                            .size(720, 360)
                            .crossfade(false)
                            .build()
                    }
                    AsyncImage(
                        model = heroReq,
                        contentDescription = "תמונת נושא רחבה",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // In-place Editable BigText
            if (config.styleType == NotificationStyleType.BIG_TEXT) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("טקסט מורחב (BigText):", fontSize = 10.sp, color = RoyalPurple, fontWeight = FontWeight.Bold)
                            if (editable) {
                                Text("הקלד כאן ישירות ✍️", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (editable && onConfigChange != null) {
                            BasicTextField(
                                value = config.bigText,
                                onValueChange = { onConfigChange(config.copy(bigText = it)) },
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                ),
                                cursorBrush = SolidColor(RoyalPurple),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                            )
                        } else {
                            Text(
                                text = config.bigText,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // IN-PLACE PROGRESS BAR SECTION
            if (config.styleType == NotificationStyleType.PROGRESS) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, EmeraldActive.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (editable) {
                                Modifier.clickable {
                                    setEditMode(if (effectiveEditMode == MockupElement.PROGRESS) null else MockupElement.PROGRESS)
                                }
                            } else {
                                Modifier
                            }
                        )
                        .padding(6.dp)
                ) {
                    Column {
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (config.progressIndeterminate) "טעינה מסתובבת..." else "${config.progressCurrent}% הושלמו (x${"%.1f".format(config.progressSpeedFactor)})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (editable) {
                                Text("הקש לכוון במקום ⚙️", fontSize = 10.sp, color = EmeraldActive)
                            }
                        }

                        // In-place Progress Slider & Controls
                        AnimatedVisibility(visible = isProgressOpen && editable) {
                            Column(modifier = Modifier.padding(top = 6.dp)) {
                                if (!config.progressIndeterminate) {
                                    Slider(
                                        value = config.progressCurrent.toFloat(),
                                        onValueChange = { onConfigChange?.invoke(config.copy(progressCurrent = it.toInt())) },
                                        valueRange = 0f..100f
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("טעינה מסתובבת", fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Switch(
                                            checked = config.progressIndeterminate,
                                            onCheckedChange = { onConfigChange?.invoke(config.copy(progressIndeterminate = it)) }
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf(0.5f, 1.0f, 2.0f, 5.0f).forEach { factor ->
                                            FilterChip(
                                                selected = config.progressSpeedFactor == factor,
                                                onClick = { onConfigChange?.invoke(config.copy(progressSpeedFactor = factor)) },
                                                label = { Text("x$factor", fontSize = 9.sp) }
                                            )
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        onConfigChange?.invoke(config.copy(styleType = NotificationStyleType.STANDARD))
                                        setEditMode(null)
                                    },
                                    modifier = Modifier.padding(top = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("הסר סרגל התקדמות", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // IN-PLACE DIRECT REPLY (RemoteInput)
            if (config.directReplyEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, RoyalPurple.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
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

                        if (editable && onConfigChange != null) {
                            BasicTextField(
                                value = config.directReplyLabel,
                                onValueChange = { onConfigChange(config.copy(directReplyLabel = it)) },
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                cursorBrush = SolidColor(RoyalPurple),
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Text(
                                text = config.directReplyLabel,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalPurple
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (editable && onConfigChange != null) {
                                    BasicTextField(
                                        value = config.directReplyButtonLabel,
                                        onValueChange = { onConfigChange(config.copy(directReplyButtonLabel = it)) },
                                        textStyle = TextStyle(
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        cursorBrush = SolidColor(Color.White)
                                    )
                                } else {
                                    Text(
                                        text = config.directReplyButtonLabel,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }

                        if (editable) {
                            IconButton(
                                onClick = { onConfigChange?.invoke(config.copy(directReplyEnabled = false)) },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "הסר מענה ישיר", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(13.dp))
                            }
                        }
                    }
                }
            }

            // Media Controls (In-place)
            if (config.styleType == NotificationStyleType.MEDIA) {
                val isPlayingMedia by MediaManager.isPlaying.collectAsState()
                val currentTrackTitle by MediaManager.currentTrackTitle.collectAsState()
                val currentArtist by MediaManager.currentArtist.collectAsState()
                val playlist by MediaManager.playlist.collectAsState()
                val currentIndex by MediaManager.currentIndex.collectAsState()
                val playlistSource by MediaManager.playlistSource.collectAsState()

                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
                        .clickable(enabled = editable) {
                            setEditMode(if (isMediaOpen) null else MockupElement.MEDIA)
                        }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = config.mediaTrackTitle.ifBlank { currentTrackTitle },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(" • ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = config.mediaArtist.ifBlank { currentArtist },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (editable) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Edit, contentDescription = "ערוך נגן", modifier = Modifier.size(11.dp), tint = RoyalPurple)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                MediaManager.prev(context, autoPlay = isPlayingMedia)
                                onConfigChange?.invoke(
                                    config.copy(
                                        mediaTrackTitle = MediaManager.currentTrackTitle.value,
                                        mediaArtist = MediaManager.currentArtist.value
                                    )
                                )
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "הקודם", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RoyalPurple)
                                .clickable {
                                    MediaManager.togglePlayPause(context)
                                    onConfigChange?.invoke(
                                        config.copy(
                                            mediaTrackTitle = MediaManager.currentTrackTitle.value,
                                            mediaArtist = MediaManager.currentArtist.value
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlayingMedia) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlayingMedia) "השהה" else "נגן",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = {
                                MediaManager.next(context, autoPlay = isPlayingMedia)
                                onConfigChange?.invoke(
                                    config.copy(
                                        mediaTrackTitle = MediaManager.currentTrackTitle.value,
                                        mediaArtist = MediaManager.currentArtist.value
                                    )
                                )
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = "הבא", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        }
                    }

                    // IN-PLACE MEDIA CONFIGURATION PANEL (Opens directly when tapping the media player or media tool)
                    AnimatedVisibility(visible = isMediaOpen && editable) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📁 בחירת שירים מהסייר המובנה:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple
                                )
                                IconButton(
                                    onClick = { setEditMode(null) },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "סגור", modifier = Modifier.size(14.dp))
                                }
                            }

                            // The two SAF file/folder picker buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { audioFilesPickerLauncher.launch(arrayOf("audio/*")) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.AudioFile, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("בחר שירים מהסייר", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { folderPickerLauncher.launch(null) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(15.dp), tint = CrownGold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("בחר תיקייה שלמה", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Current playlist info & source
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = playlistSource,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${playlist.size} שירים",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalPurple
                                    )
                                }
                            }

                            // Tracks horizontal list
                            if (playlist.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(playlist.size) { idx ->
                                        val trk = playlist[idx]
                                        val isCurrent = idx == currentIndex
                                        FilterChip(
                                            selected = isCurrent,
                                            onClick = {
                                                MediaManager.playTrackAtIndex(context, idx)
                                                onConfigChange?.invoke(
                                                    config.copy(
                                                        mediaTrackTitle = trk.title,
                                                        mediaArtist = trk.artist
                                                    )
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = "${idx + 1}. ${trk.title}",
                                                    fontSize = 10.sp,
                                                    maxLines = 1
                                                )
                                            },
                                            leadingIcon = {
                                                if (isCurrent && isPlayingMedia) {
                                                    Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(12.dp), tint = CrownGold)
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            // TextFields for custom overrides
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = config.mediaTrackTitle,
                                    onValueChange = { onConfigChange?.invoke(config.copy(mediaTrackTitle = it)) },
                                    label = { Text("שם השיר", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = config.mediaArtist,
                                    onValueChange = { onConfigChange?.invoke(config.copy(mediaArtist = it)) },
                                    label = { Text("שם האמן", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            // Action buttons: Reset to default + Remove Media Style
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        MediaManager.resetToDefaults(context)
                                        val def = MediaManager.playlist.value.first()
                                        onConfigChange?.invoke(
                                            config.copy(
                                                mediaTrackTitle = def.title,
                                                mediaArtist = def.artist
                                            )
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("איפוס לדמו", fontSize = 11.sp)
                                }

                                TextButton(
                                    onClick = {
                                        MediaManager.stop(context)
                                        onConfigChange?.invoke(config.copy(styleType = NotificationStyleType.STANDARD))
                                        setEditMode(null)
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("הסר סגנון מדיה", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // IN-PLACE ACTION BUTTONS ROW
            val hasAnyButton = (config.button1Enabled && config.button1Label.isNotBlank()) ||
                               (config.button2Enabled && config.button2Label.isNotBlank()) ||
                               (config.button3Enabled && config.button3Label.isNotBlank())

            if (hasAnyButton || editable) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Button 1
                    if (config.button1Enabled && config.button1Label.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                if (editable) {
                                    setEditMode(if (effectiveEditMode == MockupElement.BUTTON_1) null else MockupElement.BUTTON_1)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(config.button1Label, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (editable) {
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(9.dp), tint = RoyalPurple)
                            }
                        }
                    }

                    // Button 2
                    if (config.button2Enabled && config.button2Label.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                if (editable) {
                                    setEditMode(if (effectiveEditMode == MockupElement.BUTTON_2) null else MockupElement.BUTTON_2)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Text(config.button2Label, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (editable) {
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(9.dp), tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }

                    // Button 3
                    if (config.button3Enabled && config.button3Label.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                if (editable) {
                                    setEditMode(if (effectiveEditMode == MockupElement.BUTTON_3) null else MockupElement.BUTTON_3)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(config.button3Label, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (editable) {
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(9.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    // Compact Add Button slot when in edit mode and slots remaining
                    val enabledCount = (if (config.button1Enabled) 1 else 0) +
                                       (if (config.button2Enabled) 1 else 0) +
                                       (if (config.button3Enabled) 1 else 0)
                    if (editable && enabledCount < 3) {
                        val nextSlot = when {
                            !config.button1Enabled -> 1
                            !config.button2Enabled -> 2
                            else -> 3
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .then(if (enabledCount > 0) Modifier.weight(0.7f) else Modifier.fillMaxWidth())
                                .height(28.dp)
                                .clickable {
                                    when (nextSlot) {
                                        1 -> {
                                            onConfigChange?.invoke(config.copy(button1Enabled = true))
                                            setEditMode(MockupElement.BUTTON_1)
                                        }
                                        2 -> {
                                            onConfigChange?.invoke(config.copy(button2Enabled = true))
                                            setEditMode(MockupElement.BUTTON_2)
                                        }
                                        3 -> {
                                            onConfigChange?.invoke(config.copy(button3Enabled = true))
                                            setEditMode(MockupElement.BUTTON_3)
                                        }
                                    }
                                }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (enabledCount == 0) "+ הוסף כפתור פעולה" else "+ כפתור $nextSlot",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // MODAL DIALOG FOR BUTTON CONFIGURATION (Dismisses on outside tap, 1/3 size, categorized)
    if (activeEditingButton != null && editable) {
        val btnIdx = activeEditingButton
        val btnLabel = when (btnIdx) {
            1 -> config.button1Label
            2 -> config.button2Label
            else -> config.button3Label
        }
        val btnAction = when (btnIdx) {
            1 -> config.button1Action
            2 -> config.button2Action
            else -> config.button3Action
        }
        val btnToastText = when (btnIdx) {
            1 -> config.button1ToastText
            2 -> config.button2ToastText
            else -> config.button3ToastText
        }
        val btnUrl = when (btnIdx) {
            1 -> config.button1Url
            2 -> config.button2Url
            else -> config.button3Url
        }
        val btnPkg = when (btnIdx) {
            1 -> config.button1Package
            2 -> config.button2Package
            else -> config.button3Package
        }

        var selectedCategory by remember(btnIdx) {
            mutableStateOf(btnAction.category)
        }

        Dialog(
            onDismissRequest = { setEditMode(null) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 400.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Header: Title & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = RoyalPurple.copy(alpha = 0.15f),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$btnIdx",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalPurple
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "הגדרת כפתור פעולה $btnIdx",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            IconButton(
                                onClick = {
                                    when (btnIdx) {
                                        1 -> onConfigChange?.invoke(config.copy(button1Enabled = false))
                                        2 -> onConfigChange?.invoke(config.copy(button2Enabled = false))
                                        else -> onConfigChange?.invoke(config.copy(button3Enabled = false))
                                    }
                                    setEditMode(null)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "הסר כפתור", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { setEditMode(null) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "סגור חלון", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Button Label Input
                    OutlinedTextField(
                        value = btnLabel,
                        onValueChange = { newLabel ->
                            when (btnIdx) {
                                1 -> onConfigChange?.invoke(config.copy(button1Label = newLabel))
                                2 -> onConfigChange?.invoke(config.copy(button2Label = newLabel))
                                else -> onConfigChange?.invoke(config.copy(button3Label = newLabel))
                            }
                        },
                        label = { Text("טקסט הכפתור (Label)", fontSize = 11.sp) },
                        textStyle = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Category Tabs / Selector
                    Text(
                        text = "קטגוריית פעולה:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val categories = listOf("הכל", "כללי", "מערכת", "נגישות", "אפליקציות", "אינטרנט", "מדיה")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isCatSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCatSelected) RoyalPurple else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .height(26.dp)
                                    .clickable { selectedCategory = cat }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 10.sp,
                                        fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCatSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Filtered action chips (Compact & tight spacing!)
                    val displayedActions = remember(selectedCategory) {
                        if (selectedCategory == "הכל") ButtonActionType.entries
                        else ButtonActionType.entries.filter { it.category == selectedCategory }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        displayedActions.forEach { actionType ->
                            val isActionSelected = btnAction == actionType
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isActionSelected) RoyalPurple.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(
                                    width = if (isActionSelected) 1.5.dp else 1.dp,
                                    color = if (isActionSelected) RoyalPurple else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .height(26.dp)
                                    .clickable {
                                        when (btnIdx) {
                                            1 -> onConfigChange?.invoke(config.copy(button1Action = actionType))
                                            2 -> onConfigChange?.invoke(config.copy(button2Action = actionType))
                                            else -> onConfigChange?.invoke(config.copy(button3Action = actionType))
                                        }
                                        if (actionType.requiresAccessibility && !AccessibilityUtils.isAccessibilityEnabled(context)) {
                                            AccessibilityUtils.promptEnableAccessibility(context, actionType.titleHebrew)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isActionSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = RoyalPurple,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                    }
                                    Text(
                                        text = actionType.titleHebrew,
                                        fontSize = 10.sp,
                                        fontWeight = if (isActionSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isActionSelected) RoyalPurple else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Specific Action Parameter Editors:
                    // 1. TOAST Custom Text (Request 2)
                    if (btnAction == ButtonActionType.TOAST) {
                        OutlinedTextField(
                            value = btnToastText,
                            onValueChange = { newToastText ->
                                when (btnIdx) {
                                    1 -> onConfigChange?.invoke(config.copy(button1ToastText = newToastText))
                                    2 -> onConfigChange?.invoke(config.copy(button2ToastText = newToastText))
                                    else -> onConfigChange?.invoke(config.copy(button3ToastText = newToastText))
                                }
                            },
                            label = { Text("תוכן הודעת Toast (השאר ריק לברירת מחדל)", fontSize = 10.sp) },
                            placeholder = { Text("הודעה שתוצג למשתמש...", fontSize = 10.sp) },
                            textStyle = TextStyle(fontSize = 11.sp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // 2. OPEN_BROWSER URL
                    if (btnAction == ButtonActionType.OPEN_BROWSER) {
                        OutlinedTextField(
                            value = btnUrl,
                            onValueChange = { newUrl ->
                                when (btnIdx) {
                                    1 -> onConfigChange?.invoke(config.copy(button1Url = newUrl))
                                    2 -> onConfigChange?.invoke(config.copy(button2Url = newUrl))
                                    else -> onConfigChange?.invoke(config.copy(button3Url = newUrl))
                                }
                            },
                            label = { Text("כתובת אתר (URL)", fontSize = 10.sp) },
                            textStyle = TextStyle(fontSize = 11.sp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // 3. OPEN_APP Package
                    if (btnAction == ButtonActionType.OPEN_APP) {
                        OutlinedTextField(
                            value = btnPkg,
                            onValueChange = { newPkg ->
                                when (btnIdx) {
                                    1 -> onConfigChange?.invoke(config.copy(button1Package = newPkg))
                                    2 -> onConfigChange?.invoke(config.copy(button2Package = newPkg))
                                    else -> onConfigChange?.invoke(config.copy(button3Package = newPkg))
                                }
                            },
                            label = { Text("שם חבילה (Package Name)", fontSize = 10.sp) },
                            textStyle = TextStyle(fontSize = 11.sp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("קיצורים:", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            listOf(
                                "וואטסאפ" to "com.whatsapp",
                                "יוטיוב" to "com.google.android.youtube",
                                "כרום" to "com.android.chrome"
                            ).forEach { (appName, appPkg) ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (btnPkg == appPkg) RoyalPurple else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .height(20.dp)
                                        .clickable {
                                            when (btnIdx) {
                                                1 -> onConfigChange?.invoke(config.copy(button1Package = appPkg))
                                                2 -> onConfigChange?.invoke(config.copy(button2Package = appPkg))
                                                else -> onConfigChange?.invoke(config.copy(button3Package = appPkg))
                                            }
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = appName,
                                            fontSize = 9.sp,
                                            color = if (btnPkg == appPkg) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bottom Done Button
                    Button(
                        onClick = { setEditMode(null) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("סיום ועריכה ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
