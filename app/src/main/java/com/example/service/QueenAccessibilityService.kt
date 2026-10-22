package com.example.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class QueenAccessibilityService : AccessibilityService() {

    companion object {
        private var instance: QueenAccessibilityService? = null

        fun getInstance(): QueenAccessibilityService? = instance
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("QueenAccess", "Accessibility Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        Log.d("QueenAccess", "Accessibility Service Interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }

    fun performGlobalActionSafe(action: Int): Boolean {
        return try {
            performGlobalAction(action)
        } catch (e: Exception) {
            Log.e("QueenAccess", "Failed to perform global action $action", e)
            false
        }
    }
}
