package com.jarrodqsimpson.calmloop.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Starts [intent], showing [fallbackMessage] instead of crashing when no app can handle it
 * (e.g. dialing on a tablet without a phone app).
 */
fun Context.startActivitySafely(intent: Intent, fallbackMessage: String): Boolean {
    return try {
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, fallbackMessage, Toast.LENGTH_LONG).show()
        false
    } catch (e: SecurityException) {
        Toast.makeText(this, fallbackMessage, Toast.LENGTH_LONG).show()
        false
    }
}
