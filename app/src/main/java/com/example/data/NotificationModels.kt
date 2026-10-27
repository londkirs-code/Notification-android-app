package com.example.data

enum class NotificationPriority(val titleHebrew: String, val compatPriority: Int, val channelImportance: Int) {
    URGENT("דחוף ביותר (Heads-Up מתפרץ)", androidx.core.app.NotificationCompat.PRIORITY_MAX, 4),
    HIGH("גבוה (קול ורטט)", androidx.core.app.NotificationCompat.PRIORITY_HIGH, 4),
    DEFAULT("ברירת מחדל", androidx.core.app.NotificationCompat.PRIORITY_DEFAULT, 3),
    LOW("נמוך (שקט, ללא קול)", androidx.core.app.NotificationCompat.PRIORITY_LOW, 2),
    MIN("מינימלי (ממוזער בתחתית)", androidx.core.app.NotificationCompat.PRIORITY_MIN, 1)
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

enum class RepeatUnit(val titleHebrew: String, val secondsMultiplier: Long) {
    SECONDS("שניות", 1L),
    MINUTES("דקות", 60L),
    HOURS("שעות", 3600L)
}

enum class ButtonActionType(val titleHebrew: String, val category: String) {
    TOAST("הצג הודעת Toast", "כללי"),
    OPEN_SETTINGS("פתח הגדרות מערכת", "מערכת"),
    OPEN_WIFI("פתח הגדרות Wi-Fi", "מערכת"),
    OPEN_BLUETOOTH("פתח הגדרות Bluetooth", "מערכת"),
    OPEN_BROWSER("פתח קישור אינטרנט (URL)", "אינטרנט"),
    OPEN_APP("פתח אפליקציה אחרת (Package)", "אפליקציות"),
    PLAY_MEDIA("הפעל נגן מדיה", "מדיה"),
    
    // Accessibility Actions
    ACCESSIBILITY_HOME("נגישות: מסך הבית", "נגישות"),
    ACCESSIBILITY_BACK("נגישות: כפתור חזרה", "נגישות"),
    ACCESSIBILITY_RECENTS("נגישות: אפליקציות פתוחות", "נגישות"),
    ACCESSIBILITY_NOTIFICATIONS("נגישות: פתיחת וילון התראות", "נגישות"),
    ACCESSIBILITY_QUICK_SETTINGS("נגישות: הגדרות מהירות", "נגישות"),
    ACCESSIBILITY_POWER_DIALOG("נגישות: תפריט כיבוי/הפעלה", "נגישות"),
    ACCESSIBILITY_LOCK_SCREEN("נגישות: נעילת מסך", "נגישות"),
    ACCESSIBILITY_SCREENSHOT("נגישות: צילום מסך", "נגישות");

    val requiresAccessibility: Boolean get() = category == "נגישות" || name.startsWith("ACCESSIBILITY_")
}

enum class MockupElement(val titleHebrew: String) {
    ICON("אייקון ותג התראה"),
    HEADER("כותרת עליונה וסטטוס"),
    CONTENT("כותרת וטקסט ההתראה"),
    IMAGE("תמונת נושא / אייקון גדול"),
    PROGRESS("סרגל התקדמות"),
    MEDIA("נגן מדיה"),
    DIRECT_REPLY("תיבת מענה ישיר"),
    BUTTON_1("כפתור פעולה 1"),
    BUTTON_2("כפתור פעולה 2"),
    BUTTON_3("כפתור פעולה 3"),
    SENSORY("צליל, רטט ועדיפות")
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
    val title: String = "מלך ההתראות",
    val content: String = "התראה אינטראקטיבית פעילה עם אפשרויות מתקדמות",
    val subText: String = "NotifyKing",
    val summaryText: String = "מרכז שליטה",
    val channelId: String = "queen_general",
    val channelName: String = "התראות כלליות",
    val priority: NotificationPriority = NotificationPriority.HIGH,
    val styleType: NotificationStyleType = NotificationStyleType.STANDARD,
    val bigText: String = "זהו טקסט מורחב ומפורט מאוד שנחשף כאשר גוררים את ההתראה כלפי מטה. ניתן לכתוב כאן פסקאות שלמות, נתונים טכניים, או סיכומים מעמיקים.",
    val inboxLines: List<String> = listOf("• פקודה 1 בוצעה בהצלחה", "• נתקבל מענה ישיר מהמשתמש", "• שרת מקומי מסונכרן", "• סוללה תקינה 98%"),
    val iconKey: String = "CROWN",
    val imageDrawableRes: Int? = null,
    val imageUri: String? = null,
    val largeIconDrawableRes: Int? = null,
    val largeIconUri: String? = null,
    val button1Enabled: Boolean = false,
    val button1Label: String = "אישור וביצוע",
    val button1Action: ButtonActionType = ButtonActionType.TOAST,
    val button1ToastText: String = "",
    val button1Url: String = "https://www.google.com",
    val button1Package: String = "com.whatsapp",
    val button2Enabled: Boolean = false,
    val button2Label: String = "פרטים נוספים",
    val button2Action: ButtonActionType = ButtonActionType.OPEN_SETTINGS,
    val button2ToastText: String = "",
    val button2Url: String = "https://github.com",
    val button2Package: String = "com.google.android.youtube",
    val button3Enabled: Boolean = false,
    val button3Label: String = "דחייה",
    val button3Action: ButtonActionType = ButtonActionType.ACCESSIBILITY_HOME,
    val button3ToastText: String = "",
    val button3Url: String = "",
    val button3Package: String = "",
    val directReplyEnabled: Boolean = false,
    val directReplyLabel: String = "הקלד תשובה מהירה כאן...",
    val directReplyButtonLabel: String = "השב",
    val directReplyKey: String = "key_user_reply",
    val progressMax: Int = 100,
    val progressCurrent: Int = 65,
    val progressIndeterminate: Boolean = false,
    val progressSimulateLive: Boolean = false,
    val mediaPlaying: Boolean = false,
    val mediaTrackTitle: String = "Crown Symphony - Synthwave",
    val mediaArtist: String = "Notification King Studio",
    val ongoing: Boolean = false,
    val autoCancel: Boolean = true,
    val alertOnce: Boolean = false,
    val vibration: VibrationPreset = VibrationPreset.DEFAULT,
    val customVibrateString: String = "0, 200, 100, 300",
    val sound: SoundPreset = SoundPreset.DEFAULT,
    val ledColorHex: String? = "#F59E0B",
    val groupKey: String? = null,
    val isGroupSummary: Boolean = false,
    val chronometerCountDown: Boolean = false,
    val chronometerSeconds: Long = 120,
    val timeoutAfterMs: Long = 0,
    val badgeNumber: Int = 1,
    val scheduledTimeMillis: Long? = null,
    val isRepeating: Boolean = false,
    val repeatIntervalValue: Int = 10,
    val repeatUnit: RepeatUnit = RepeatUnit.SECONDS,
    val progressSpeedFactor: Float = 1.0f
) : java.io.Serializable
