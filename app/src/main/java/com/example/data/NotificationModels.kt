package com.example.data

import android.app.Notification
import androidx.core.app.NotificationCompat

enum class NotificationPriority(val titleHebrew: String, val compatPriority: Int, val channelImportance: Int) {
    URGENT("דחוף ביותר (Heads-Up מתפרץ)", NotificationCompat.PRIORITY_MAX, 4), // IMPORTANCE_HIGH
    HIGH("גבוה (קול ורטט)", NotificationCompat.PRIORITY_HIGH, 4),
    DEFAULT("ברירת מחדל", NotificationCompat.PRIORITY_DEFAULT, 3), // IMPORTANCE_DEFAULT
    LOW("נמוך (שקט, ללא קול)", NotificationCompat.PRIORITY_LOW, 2), // IMPORTANCE_LOW
    MIN("מינימלי (ממוזער בתחתית)", NotificationCompat.PRIORITY_MIN, 1) // IMPORTANCE_MIN
}

enum class NotificationStyleType(val titleHebrew: String) {
    STANDARD("רגיל / טקסט בסיסי"),
    BIG_TEXT("טקסט מורחב (BigTextStyle)"),
    BIG_PICTURE("תמונה גדולה (BigPictureStyle)"),
    INBOX("רשימת שורות (InboxStyle)"),
    MESSAGING("שיחת צ'אט (MessagingStyle)"),
    PROGRESS("סרגל התקדמות (Progress)"),
    MEDIA("נגן מדיה (MediaStyle)"),
    CHRONOMETER("שעון עצר רץ (Chronometer)")
}

enum class VibrationPreset(val titleHebrew: String, val pattern: LongArray?) {
    DEFAULT("רטט ברירת מחדל", longArrayOf(0, 250, 250, 250)),
    NONE("ללא רטט", null),
    SOS("קוד מורס SOS (... --- ...)", longArrayOf(0, 150, 100, 150, 100, 150, 250, 400, 150, 400, 150, 400, 250, 150, 100, 150, 100, 150)),
    HEAVY_DOUBLE("פעימה כפולה עוצמתית", longArrayOf(0, 400, 150, 600)),
    HEARTBEAT("פעימות לב", longArrayOf(0, 120, 120, 240, 400, 120, 120, 240)),
    CUSTOM("מותאם אישית (מילישניות)", null)
}

enum class SoundPreset(val titleHebrew: String) {
    DEFAULT("צליל התראה סטנדרטי"),
    ALARM("צליל שעון מעורר / אזעקה"),
    RINGTONE("צלצול שיחה"),
    SILENT("ללא צליל (שקט)")
}

enum class ActionType(val labelHebrew: String) {
    BUTTON_1("כפתור 1"),
    BUTTON_2("כפתור 2"),
    BUTTON_3("כפתור 3"),
    DIRECT_REPLY("מענה ישיר (שדה הזנה)"),
    CONTENT_CLICK("לחיצה על ההתראה"),
    DISMISSED("הסרת ההתראה"),
    MEDIA_PLAY_PAUSE("הפעל / השהה מדיה"),
    MEDIA_NEXT("מדיה: הבא"),
    MEDIA_PREV("מדיה: הקודם"),
    PROGRESS_CANCEL("ביטול התקדמות")
}

data class NotificationLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val notificationId: Int,
    val actionType: ActionType,
    val actionLabel: String,
    val userReplyText: String? = null,
    val note: String = ""
)

data class NotificationConfig(
    val id: Int = 101,
    val title: String = "מלכת ההתראות",
    val content: String = "התראה אינטראקטיבית פעילה עם אפשרויות מתקדמות",
    val subText: String = "NotifyQueen",
    val summaryText: String = "סטטוס מערכת",
    val channelId: String = "queen_general",
    val channelName: String = "התראות כלליות",
    val priority: NotificationPriority = NotificationPriority.HIGH,
    val styleType: NotificationStyleType = NotificationStyleType.STANDARD,
    val bigText: String = "זהו טקסט מורחב ומפורט מאוד שנחשף כאשר גוררים את ההתראה כלפי מטה. ניתן לכתוב כאן פסקאות שלמות, נתונים טכניים, או סיכומים מעמיקים.",
    val inboxLines: List<String> = listOf("• פקודה 1 בוצעה בהצלחה", "• נתקבל מענה ישיר מהמשתמש", "• שרת מקומי מסונכרן", "• סוללה תקינה 98%"),
    val imageDrawableRes: Int? = null,
    // Buttons
    val button1Enabled: Boolean = true,
    val button1Label: String = "אישור וביצוע",
    val button2Enabled: Boolean = true,
    val button2Label: String = "פרטים נוספים",
    val button3Enabled: Boolean = false,
    val button3Label: String = "דחייה",
    // Direct Reply RemoteInput
    val directReplyEnabled: Boolean = true,
    val directReplyLabel: String = "הקלד תשובה מהירה כאן...",
    val directReplyButtonLabel: String = "השב",
    val directReplyKey: String = "key_user_reply",
    // Progress
    val progressMax: Int = 100,
    val progressCurrent: Int = 65,
    val progressIndeterminate: Boolean = false,
    val progressSimulateLive: Boolean = false,
    // Media
    val mediaPlaying: Boolean = false,
    val mediaTrackTitle: String = "Crown Symphony - Synthwave",
    val mediaArtist: String = "Notification Queen Studio",
    // Sensory
    val ongoing: Boolean = false,
    val autoCancel: Boolean = true,
    val alertOnce: Boolean = false,
    val vibration: VibrationPreset = VibrationPreset.DEFAULT,
    val customVibrateString: String = "0, 200, 100, 300",
    val sound: SoundPreset = SoundPreset.DEFAULT,
    val ledColorHex: String? = "#F59E0B",
    val groupKey: String? = null,
    val isGroupSummary: Boolean = false,
    // Chronometer
    val chronometerCountDown: Boolean = false,
    val chronometerSeconds: Long = 120,
    val timeoutAfterMs: Long = 0,
    val badgeNumber: Int = 1
)
