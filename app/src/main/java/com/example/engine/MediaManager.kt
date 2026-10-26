package com.example.engine

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import com.example.data.NotificationConfig
import com.example.data.NotificationRepository
import com.example.data.NotificationStyleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MediaManager {

    data class AudioTrack(
        val uri: Uri,
        val title: String,
        val artist: String = "שיר מקומי",
        val durationText: String = ""
    )

    private val defaultPlaylist = listOf(
        AudioTrack(
            uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
            title = "SoundHelix Synthwave #1",
            artist = "SoundHelix Stream"
        ),
        AudioTrack(
            uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"),
            title = "SoundHelix Synthwave #2",
            artist = "SoundHelix Stream"
        ),
        AudioTrack(
            uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"),
            title = "SoundHelix Synthwave #3",
            artist = "SoundHelix Stream"
        )
    )

    private var mediaPlayer: MediaPlayer? = null

    private val _playlist = MutableStateFlow<List<AudioTrack>>(defaultPlaylist)
    val playlist: StateFlow<List<AudioTrack>> = _playlist.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackTitle = MutableStateFlow(defaultPlaylist[0].title)
    val currentTrackTitle: StateFlow<String> = _currentTrackTitle.asStateFlow()

    private val _currentArtist = MutableStateFlow(defaultPlaylist[0].artist)
    val currentArtist: StateFlow<String> = _currentArtist.asStateFlow()

    private val _playlistSource = MutableStateFlow("רצועות דמו מובנות (3 שירים)")
    val playlistSource: StateFlow<String> = _playlistSource.asStateFlow()

    val currentTrack: StateFlow<String> = _currentTrackTitle

    fun loadFromUris(context: Context, uris: List<Uri>): Int {
        if (uris.isEmpty()) return 0
        val scanned = uris.mapNotNull { uri ->
            extractTrackInfo(context, uri)
        }
        if (scanned.isNotEmpty()) {
            stop(context)
            _playlist.value = scanned
            _currentIndex.value = 0
            _playlistSource.value = "נבחרו ${scanned.size} שירים מהסייר"
            updateCurrentTrackMetadata()
        }
        return scanned.size
    }

    fun loadFromFolder(context: Context, treeUri: Uri): Int {
        try {
            context.contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {}

        var folderName = "תיקייה מקומית"
        try {
            val docUri = DocumentsContract.buildDocumentUriUsingTree(
                treeUri,
                DocumentsContract.getTreeDocumentId(treeUri)
            )
            context.contentResolver.query(
                docUri,
                arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    if (idx != -1) {
                        folderName = cursor.getString(idx) ?: folderName
                    }
                }
            }
        } catch (_: Exception) {}

        val tracks = mutableListOf<AudioTrack>()
        try {
            val documentId = DocumentsContract.getTreeDocumentId(treeUri)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId)
            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
            )
            val subDirs = mutableListOf<String>()

            context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                val idCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)

                while (cursor.moveToNext()) {
                    val childId = cursor.getString(idCol)
                    val displayName = cursor.getString(nameCol) ?: "שיר"
                    val mimeType = cursor.getString(mimeCol) ?: ""

                    if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                        subDirs.add(childId)
                    } else if (isAudioFile(displayName, mimeType)) {
                        val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childId)
                        val title = displayName.substringBeforeLast(".")
                        tracks.add(AudioTrack(uri = fileUri, title = title, artist = folderName))
                    }
                }
            }

            // 1-level deep subfolder scanning
            for (subDirId in subDirs) {
                try {
                    val subChildrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, subDirId)
                    context.contentResolver.query(subChildrenUri, projection, null, null, null)?.use { subCursor ->
                        val subIdCol = subCursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                        val subNameCol = subCursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                        val subMimeCol = subCursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)

                        while (subCursor.moveToNext()) {
                            val childId = subCursor.getString(subIdCol)
                            val displayName = subCursor.getString(subNameCol) ?: "שיר"
                            val mimeType = subCursor.getString(subMimeCol) ?: ""

                            if (isAudioFile(displayName, mimeType)) {
                                val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childId)
                                val title = displayName.substringBeforeLast(".")
                                tracks.add(AudioTrack(uri = fileUri, title = title, artist = folderName))
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.e("MediaManager", "Failed scanning folder tree", e)
        }

        if (tracks.isNotEmpty()) {
            stop(context)
            _playlist.value = tracks
            _currentIndex.value = 0
            _playlistSource.value = "תיקייה: $folderName (${tracks.size} שירים)"
            updateCurrentTrackMetadata()
        }
        return tracks.size
    }

    fun resetToDefaults(context: Context) {
        stop(context)
        _playlist.value = defaultPlaylist
        _currentIndex.value = 0
        _playlistSource.value = "רצועות דמו מובנות (3 שירים)"
        updateCurrentTrackMetadata()
    }

    private fun isAudioFile(displayName: String, mimeType: String): Boolean {
        return mimeType.startsWith("audio/") ||
            displayName.endsWith(".mp3", ignoreCase = true) ||
            displayName.endsWith(".m4a", ignoreCase = true) ||
            displayName.endsWith(".wav", ignoreCase = true) ||
            displayName.endsWith(".ogg", ignoreCase = true) ||
            displayName.endsWith(".flac", ignoreCase = true) ||
            displayName.endsWith(".aac", ignoreCase = true) ||
            displayName.endsWith(".opus", ignoreCase = true)
    }

    private fun extractTrackInfo(context: Context, uri: Uri): AudioTrack? {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {}

        var displayName = "קובץ שמע"
        try {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        displayName = cursor.getString(nameIndex) ?: displayName
                    }
                }
            }
        } catch (_: Exception) {}

        var title = displayName.substringBeforeLast(".")
        var artist = "מכשיר מקומי"

        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val metaTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val metaArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            if (!metaTitle.isNullOrBlank()) {
                title = metaTitle
            }
            if (!metaArtist.isNullOrBlank()) {
                artist = metaArtist
            }
            retriever.release()
        } catch (_: Exception) {}

        return AudioTrack(uri = uri, title = title, artist = artist)
    }

    private fun updateCurrentTrackMetadata() {
        val tracks = _playlist.value
        val idx = _currentIndex.value.coerceIn(0, (tracks.size - 1).coerceAtLeast(0))
        if (tracks.isNotEmpty()) {
            _currentTrackTitle.value = tracks[idx].title
            _currentArtist.value = tracks[idx].artist
        }
    }

    fun togglePlayPause(context: Context) {
        if (mediaPlayer == null) {
            playTrackAtIndex(context, _currentIndex.value)
        } else {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                _isPlaying.value = false
                NotificationRepository.setMediaPlaying(false)
                updateMediaNotification(context)
            } else {
                mediaPlayer?.start()
                _isPlaying.value = true
                NotificationRepository.setMediaPlaying(true)
                updateMediaNotification(context)
            }
        }
    }

    fun playTrackAtIndex(context: Context, index: Int) {
        val tracks = _playlist.value
        if (tracks.isEmpty()) return
        val targetIndex = index.coerceIn(0, tracks.size - 1)
        _currentIndex.value = targetIndex
        updateCurrentTrackMetadata()

        val track = tracks[targetIndex]
        try {
            mediaPlayer?.reset()
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            }
            mediaPlayer?.apply {
                setDataSource(context, track.uri)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    _isPlaying.value = true
                    NotificationRepository.setMediaPlaying(true)
                    updateMediaNotification(context)
                }
                setOnCompletionListener {
                    next(context, autoPlay = true)
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("MediaManager", "MediaPlayer error: what=$what extra=$extra")
                    _isPlaying.value = false
                    NotificationRepository.setMediaPlaying(false)
                    updateMediaNotification(context)
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("MediaManager", "Error playing track: ${track.title}", e)
            _isPlaying.value = false
            NotificationRepository.setMediaPlaying(false)
        }
    }

    fun next(context: Context, autoPlay: Boolean = true) {
        val tracks = _playlist.value
        if (tracks.isEmpty()) return
        val nextIndex = (_currentIndex.value + 1) % tracks.size
        _currentIndex.value = nextIndex
        updateCurrentTrackMetadata()
        if (autoPlay || _isPlaying.value) {
            playTrackAtIndex(context, nextIndex)
        } else {
            updateMediaNotification(context)
        }
    }

    fun prev(context: Context, autoPlay: Boolean = true) {
        val tracks = _playlist.value
        if (tracks.isEmpty()) return
        val prevIndex = if (_currentIndex.value - 1 < 0) tracks.size - 1 else _currentIndex.value - 1
        _currentIndex.value = prevIndex
        updateCurrentTrackMetadata()
        if (autoPlay || _isPlaying.value) {
            playTrackAtIndex(context, prevIndex)
        } else {
            updateMediaNotification(context)
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

    // Overload for backward compatibility
    fun play(context: Context, trackUrl: String = "", trackTitle: String = "") {
        if (trackUrl.isNotBlank()) {
            val customTrack = AudioTrack(uri = Uri.parse(trackUrl), title = trackTitle.ifBlank { "שיר" })
            _playlist.value = listOf(customTrack)
            _currentIndex.value = 0
            playTrackAtIndex(context, 0)
        } else {
            togglePlayPause(context)
        }
    }

    fun updateMediaNotification(context: Context) {
        try {
            val tracks = _playlist.value
            val idx = _currentIndex.value
            val title = _currentTrackTitle.value
            val artist = _currentArtist.value
            val countInfo = if (tracks.size > 1) " (${idx + 1}/${tracks.size})" else ""

            val config = NotificationConfig(
                id = 205,
                title = title,
                content = artist,
                subText = if (_isPlaying.value) "מנגן כעת$countInfo" else "מושהה$countInfo",
                styleType = NotificationStyleType.MEDIA,
                mediaTrackTitle = title,
                mediaArtist = artist,
                ongoing = _isPlaying.value
            )
            NotificationEngine.postNotification(context, config)
        } catch (e: Throwable) {
            Log.e("MediaManager", "Failed to update media notification", e)
        }
    }
}
