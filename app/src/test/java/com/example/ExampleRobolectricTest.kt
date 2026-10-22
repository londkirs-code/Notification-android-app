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
}
