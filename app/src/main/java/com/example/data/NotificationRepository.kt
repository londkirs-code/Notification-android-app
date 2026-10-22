package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NotificationRepository {
    private val _logs = MutableStateFlow<List<NotificationLog>>(emptyList())
    val logs: StateFlow<List<NotificationLog>> = _logs.asStateFlow()

    private val _activeNotificationIds = MutableStateFlow<Set<Int>>(emptySet())
    val activeNotificationIds: StateFlow<Set<Int>> = _activeNotificationIds.asStateFlow()

    private val _customPresets = MutableStateFlow<List<NotificationConfig>>(emptyList())
    val customPresets: StateFlow<List<NotificationConfig>> = _customPresets.asStateFlow()

    private val _mediaPlayingState = MutableStateFlow(false)
    val mediaPlayingState: StateFlow<Boolean> = _mediaPlayingState.asStateFlow()

    fun logAction(
        notificationId: Int,
        actionType: ActionType,
        actionLabel: String,
        userReplyText: String? = null,
        note: String = ""
    ) {
        val entry = NotificationLog(
            notificationId = notificationId,
            actionType = actionType,
            actionLabel = actionLabel,
            userReplyText = userReplyText,
            note = note
        )
        _logs.update { current -> listOf(entry) + current }
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun registerActiveNotification(id: Int) {
        _activeNotificationIds.update { it + id }
    }

    fun unregisterActiveNotification(id: Int) {
        _activeNotificationIds.update { it - id }
    }

    fun clearAllActive() {
        _activeNotificationIds.value = emptySet()
    }

    fun toggleMediaPlaying(): Boolean {
        _mediaPlayingState.update { !it }
        return _mediaPlayingState.value
    }

    fun setMediaPlaying(playing: Boolean) {
        _mediaPlayingState.value = playing
    }

    fun savePreset(config: NotificationConfig) {
        _customPresets.update { current ->
            val filtered = current.filter { it.id != config.id }
            listOf(config) + filtered
        }
    }

    fun deletePreset(id: Int) {
        _customPresets.update { current -> current.filter { it.id != id } }
    }
}
