package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NotificationConfig
import com.example.data.NotificationPriority
import com.example.engine.NotificationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("מלך ההתראות", appName)
  }

  @Test
  fun `test termux command generation`() {
    val config = NotificationConfig(
      id = 777,
      title = "Test Title",
      content = "Test Message",
      priority = NotificationPriority.HIGH,
      button1Enabled = true,
      button1Label = "Action1"
    )
    val cmd = NotificationEngine.generateTermuxCommand(config)
    assertTrue(cmd.contains("--id 777"))
    assertTrue(cmd.contains("--title \"Test Title\""))
    assertTrue(cmd.contains("--prio high"))
    assertTrue(cmd.contains("--button1 \"Action1\""))
  }

  @Test
  fun `test recurring notification configuration`() {
    val config = NotificationConfig(
      id = 888,
      title = "Recurring Notification",
      isRepeating = true,
      repeatIntervalValue = 15,
      repeatUnit = com.example.data.RepeatUnit.MINUTES
    )
    assertTrue(config.isRepeating)
    assertEquals(15, config.repeatIntervalValue)
    assertEquals(com.example.data.RepeatUnit.MINUTES, config.repeatUnit)
    assertEquals(15 * 60L, config.repeatIntervalValue * config.repeatUnit.secondsMultiplier)
  }

  @Test
  fun `test schedule and cancel notification alarm`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val config = NotificationConfig(
      id = 999,
      title = "Scheduled Test",
      isRepeating = true,
      repeatIntervalValue = 30,
      repeatUnit = com.example.data.RepeatUnit.SECONDS
    )
    val triggerTime = System.currentTimeMillis() + 30000L
    NotificationEngine.scheduleNotification(context, config, triggerTime)
    NotificationEngine.cancelScheduledAlarm(context, 999)
    NotificationEngine.cancelNotification(context, 999)
  }

  @Test
  fun `test accessibility requirement detection`() {
    val nonAccConfig = NotificationConfig(
      button1Enabled = true,
      button1Action = com.example.data.ButtonActionType.TOAST,
      button2Enabled = true,
      button2Action = com.example.data.ButtonActionType.OPEN_SETTINGS,
      button3Enabled = false
    )
    org.junit.Assert.assertFalse(com.example.util.AccessibilityUtils.configRequiresAccessibility(nonAccConfig))

    val accConfig = NotificationConfig(
      button1Enabled = true,
      button1Action = com.example.data.ButtonActionType.ACCESSIBILITY_HOME
    )
    assertTrue(com.example.util.AccessibilityUtils.configRequiresAccessibility(accConfig))
    assertTrue(com.example.data.ButtonActionType.ACCESSIBILITY_BACK.requiresAccessibility)
  }

  @Test
  fun `test media manager playlist navigation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.engine.MediaManager.resetToDefaults(context)
    val initialTitle = com.example.engine.MediaManager.currentTrackTitle.value
    assertTrue(initialTitle.isNotEmpty())
    assertEquals(3, com.example.engine.MediaManager.playlist.value.size)

    com.example.engine.MediaManager.next(context, autoPlay = false)
    val secondTitle = com.example.engine.MediaManager.currentTrackTitle.value
    org.junit.Assert.assertNotEquals(initialTitle, secondTitle)

    com.example.engine.MediaManager.prev(context, autoPlay = false)
    assertEquals(initialTitle, com.example.engine.MediaManager.currentTrackTitle.value)
  }
}
