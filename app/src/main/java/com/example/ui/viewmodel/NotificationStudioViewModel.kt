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
            content = "התראה מעוצבת אישית - הוסף כפתורים ותכנים מארגז הכלים",
            subText = "NotifyKing",
            summaryText = "מרכז שליטה",
            priority = NotificationPriority.HIGH,
            styleType = NotificationStyleType.STANDARD,
            button1Enabled = false,
            button2Enabled = false,
            button3Enabled = false,
            directReplyEnabled = false
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
