package com.example.engine

import android.content.Context
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.R
import com.example.data.NotificationConfig
import com.example.data.NotificationStyleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object DownloadEngine {
    private val activeDownloads = mutableMapOf<Int, Job>()

    fun startRealDownload(context: Context, config: NotificationConfig, fileUrl: String = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", fileName: String = "Download_Sample.pdf") {
        activeDownloads[config.id]?.cancel()

        val job = CoroutineScope(Dispatchers.IO).launch {
            val manager = NotificationManagerCompat.from(context)
            val channelId = NotificationEngine.CHANNEL_MEDIA

            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpsURLConnection
                connection.connect()
                val fileLength = connection.contentLength

                val inputStream = connection.inputStream
                val targetFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                val outputStream = FileOutputStream(targetFile)

                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                var lastProgress = -1

                val baseDelay = 600L
                val adjustedDelay = (baseDelay / config.progressSpeedFactor).toLong()

                while (inputStream.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    outputStream.write(data, 0, count)

                    val progress = if (fileLength > 0) ((total * 100) / fileLength).toInt() else 50

                    if (progress != lastProgress) {
                        lastProgress = progress
                        val progressBuilder = NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_stat_notify)
                            .setContentTitle("מוריד: $fileName ($progress%)")
                            .setContentText("${total / 1024} KB הורדו")
                            .setProgress(100, progress, false)
                            .setOngoing(true)
                            .setOnlyAlertOnce(true)

                        withContext(Dispatchers.Main) {
                            try {
                                manager.notify(config.id, progressBuilder.build())
                            } catch (_: SecurityException) {}
                        }
                        delay(adjustedDelay)
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                // Success Notification
                val completeBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setContentTitle("✅ ההורדה הושלמה בהצלחה!")
                    .setContentText("הקובץ נשמר בנתיב: ${targetFile.absolutePath}")
                    .setAutoCancel(true)
                    .setOngoing(false)

                withContext(Dispatchers.Main) {
                    try {
                        manager.notify(config.id, completeBuilder.build())
                    } catch (_: SecurityException) {}
                }

            } catch (e: Exception) {
                val errorBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setContentTitle("❌ שגיאה בהורדת הקובץ")
                    .setContentText(e.localizedMessage ?: "שגיאת רשת")
                    .setAutoCancel(true)
                    .setOngoing(false)

                withContext(Dispatchers.Main) {
                    try {
                        manager.notify(config.id, errorBuilder.build())
                    } catch (_: SecurityException) {}
                }
            }
        }

        activeDownloads[config.id] = job
    }

    fun cancelDownload(id: Int) {
        activeDownloads[id]?.cancel()
        activeDownloads.remove(id)
    }
}
