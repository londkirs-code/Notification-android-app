package com.example.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import com.example.data.ButtonActionType
import com.example.data.NotificationConfig
import com.example.service.QueenAccessibilityService

object AccessibilityUtils {

    fun isAccessibilityEnabled(context: Context): Boolean {
        if (QueenAccessibilityService.getInstance() != null) return true
        return try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: ""
            if (enabledServices.contains(context.packageName)) return true

            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            val list = am?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            list?.any { it.resolveInfo.serviceInfo.packageName == context.packageName } == true
        } catch (_: Exception) {
            false
        }
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "לא ניתן לפתוח הגדרות נגישות", Toast.LENGTH_SHORT).show()
        }
    }

    fun promptEnableAccessibility(context: Context, actionName: String? = null) {
        val msg = if (actionName != null) {
            "הפעולה '$actionName' דורשת הרשאת נגישות. מעביר להגדרות..."
        } else {
            "ההגדרה שנבחרה דורשת שירות נגישות פעיל. מעביר להגדרות..."
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        openAccessibilitySettings(context)
    }

    fun configRequiresAccessibility(config: NotificationConfig): Boolean {
        if (config.button1Enabled && config.button1Action.requiresAccessibility) return true
        if (config.button2Enabled && config.button2Action.requiresAccessibility) return true
        if (config.button3Enabled && config.button3Action.requiresAccessibility) return true
        return false
    }
}
