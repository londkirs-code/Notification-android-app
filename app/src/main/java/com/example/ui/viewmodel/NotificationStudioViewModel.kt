package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.MockupElement
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.data.NotificationStyleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationStudioViewModel : ViewModel() {

    private val _config = MutableStateFlow(
        NotificationConfig(
            id = 101,
            title = "מלך ההתראות",
            content = "התראה פעילה עם כפתורי פעולה ומענה ישיר",
            subText = "NotifyKing",
            summaryText = "מרכז שליטה",
            priority = NotificationPriority.HIGH,
            styleType = NotificationStyleType.STANDARD,
            button1Enabled = true,
            button1Label = "אישור וביצוע",
            button2Enabled = true,
            button2Label = "פרטים נוספים",
            button3Enabled = true,
            button3Label = "דחייה",
            directReplyEnabled = true,
            directReplyLabel = "הקלד תשובה ישירה כאן...",
            directReplyButtonLabel = "השב"
        )
    )
    val config: StateFlow<NotificationConfig> = _config.asStateFlow()

    private val _activeEditMode = MutableStateFlow<MockupElement?>(null)
    val activeEditMode: StateFlow<MockupElement?> = _activeEditMode.asStateFlow()

    fun updateConfig(newConfig: NotificationConfig) {
        _config.value = newConfig
    }

    fun loadPreset(presetConfig: NotificationConfig) {
        _config.value = presetConfig
        _activeEditMode.value = null
    }

    fun setActiveEditMode(mode: MockupElement?) {
        _activeEditMode.value = mode
    }
}
