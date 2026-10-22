package com.example.engine

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.data.NotificationConfig
import com.example.data.NotificationRepository
import com.example.data.NotificationStyleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MediaManager {
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow("SoundHelix Synthwave Stream #1")
    val currentTrack: StateFlow<String> = _currentTrack.asStateFlow()

    fun play(context: Context, trackUrl: String = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", trackTitle: String = "SoundHelix Synthwave #1") {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, Uri.parse(trackUrl))
                    prepareAsync()
                    setOnPreparedListener {
                        start()
                        _isPlaying.value = true
                        NotificationRepository.setMediaPlaying(true)
                        updateMediaNotification(context)
                    }
                    setOnCompletionListener {
                        _isPlaying.value = false
                        NotificationRepository.setMediaPlaying(false)
                        updateMediaNotification(context)
                    }
                }
            } else {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    _isPlaying.value = false
                    NotificationRepository.setMediaPlaying(false)
                } else {
                    mediaPlayer?.start()
                    _isPlaying.value = true
                    NotificationRepository.setMediaPlaying(true)
                }
                updateMediaNotification(context)
            }
            _currentTrack.value = trackTitle
        } catch (e: Exception) {
            Log.e("MediaManager", "Error playing media", e)
        }
    }

    fun pause(context: Context) {
        try {
            mediaPlayer?.pause()
            _isPlaying.value = false
            NotificationRepository.setMediaPlaying(false)
            updateMediaNotification(context)
        } catch (_: Exception) {}
    }

    fun stop(context: Context) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            _isPlaying.value = false
            NotificationRepository.setMediaPlaying(false)
            updateMediaNotification(context)
        } catch (_: Exception) {}
    }

    private fun updateMediaNotification(context: Context) {
        val config = NotificationConfig(
            id = 205,
            title = "נגן מדיה אמיתי",
            content = _currentTrack.value,
            subText = if (_isPlaying.value) "מנגן כעת" else "מושהה",
            styleType = NotificationStyleType.MEDIA,
            mediaTrackTitle = _currentTrack.value,
            mediaArtist = "SoundHelix Audio Stream",
            ongoing = _isPlaying.value
        )
        NotificationEngine.postNotification(context, config)
    }
}
