package com.example.kihez.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlin.random.Random

class NotificationScheduler(private val context: Context) {

    enum class Mode { FIXED, RANDOM }

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isRunning(): Boolean = prefs.getBoolean(KEY_RUNNING, false)

    fun getMode(): Mode {
        val ordinal = prefs.getInt(KEY_MODE, Mode.RANDOM.ordinal)
        return Mode.values().getOrElse(ordinal) { Mode.RANDOM }
    }

    fun getFixedIntervalMillis(): Long =
        prefs.getLong(KEY_FIXED_INTERVAL_MILLIS, DEFAULT_FIXED_INTERVAL_MILLIS)

    fun start(mode: Mode, fixedIntervalMillis: Long) {
        prefs.edit()
            .putBoolean(KEY_RUNNING, true)
            .putInt(KEY_MODE, mode.ordinal)
            .putLong(KEY_FIXED_INTERVAL_MILLIS, fixedIntervalMillis)
            .apply()
        scheduleNext()
    }

    fun stop() {
        prefs.edit().putBoolean(KEY_RUNNING, false).apply()
        cancelAlarm()
    }

    fun scheduleNext() {
        if (!isRunning()) return
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return
        }

        val delay = computeDelayMillis(getMode(), getFixedIntervalMillis())
        val triggerAt = System.currentTimeMillis() + delay
        val pendingIntent = buildPendingIntent()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
            prefs.edit().putLong(KEY_NEXT_TRIGGER_AT_MILLIS, triggerAt).apply()
        } catch (_: SecurityException) {
            // Permission denied for exact alarms.
        }
    }

    private fun cancelAlarm() {
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = buildPendingIntent()
        alarmManager.cancel(pi)
        pi.cancel()
    }

    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(appContext, NotificationReceiver::class.java).apply {
            action = ACTION_ALARM
        }
        return PendingIntent.getBroadcast(
            appContext,
            REQUEST_CODE_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun computeDelayMillis(mode: Mode, fixedIntervalMillis: Long): Long {
        return when (mode) {
            Mode.FIXED -> fixedIntervalMillis.coerceAtLeast(60_000L)
            Mode.RANDOM -> Random.nextLong(RANDOM_MIN_MILLIS, RANDOM_MAX_MILLIS + 1L)
        }
    }

    companion object {
        private const val PREFS_NAME = "kihez_scheduler"
        private const val KEY_RUNNING = "running"
        private const val KEY_MODE = "mode"
        private const val KEY_FIXED_INTERVAL_MILLIS = "fixed_interval_millis"
        private const val KEY_NEXT_TRIGGER_AT_MILLIS = "next_trigger_at_millis"

        const val ACTION_ALARM = "com.example.kihez.ACTION_ALARM"
        const val NOTIFICATION_TEXT = "Kihez tartozik ez?"
        const val NOTIFICATION_CHANNEL_ID = "kihez_high_priority"
        const val NOTIFICATION_ID = 1001

        private const val REQUEST_CODE_ALARM = 1
        private const val DEFAULT_FIXED_INTERVAL_MILLIS = 60L * 60L * 1000L
        private const val RANDOM_MIN_MILLIS = 15L * 60L * 1000L
        private const val RANDOM_MAX_MILLIS = 4L * 60L * 60L * 1000L
    }
}
