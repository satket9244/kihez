package com.example.kihez

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.kihez.scheduler.NotificationScheduler
import com.example.kihez.scheduler.NotificationScheduler.Mode
import com.example.kihez.ui.KihezScreen
import com.example.kihez.ui.KihezUiState
import com.example.kihez.ui.QuestionsListScreen
import com.example.kihez.ui.theme.KihezTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen(this) }
    }
}

@Composable
private fun MainScreen(context: Context) {
    val scheduler = remember { NotificationScheduler(context.applicationContext) }

    var running by remember { mutableStateOf(scheduler.isRunning()) }
    var mode by remember { mutableStateOf(scheduler.getMode()) }
    var questionMode by remember { mutableStateOf(scheduler.getQuestionMode()) }
    var customQuestionText by remember { mutableStateOf(scheduler.getCustomNotificationText()) }
    var showQuestionsList by remember { mutableStateOf(false) }

    val initialMillis = scheduler.getFixedIntervalMillis()
    var hoursText by remember { mutableStateOf((initialMillis / 3_600_000L).toString()) }
    var minutesText by remember { mutableStateOf(((initialMillis % 3_600_000L) / 60_000L).toString()) }
    var pendingExact by remember { mutableStateOf(false) }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun canExactAlarms(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    fun fixedIntervalMillisOrNull(): Long? {
        val h = hoursText.toLongOrNull() ?: 0L
        val m = minutesText.toLongOrNull() ?: 0L
        if (m !in 0L..59L) return null
        val totalMinutes = h * 60L + m
        if (totalMinutes < 1L) return null
        return totalMinutes * 60_000L
    }

    val requestNotificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    val requestExactAlarmAccess = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (pendingExact) {
            pendingExact = false
            if (canExactAlarms()) {
                val fixed = fixedIntervalMillisOrNull() ?: (60L * 60L * 1000L)
                scheduler.start(mode, fixed)
                running = scheduler.isRunning()
            }
        }
    }

    fun startScheduling() {
        val fixed = if (mode == Mode.FIXED) {
            fixedIntervalMillisOrNull() ?: run {
                running = false
                return
            }
        } else {
            fixedIntervalMillisOrNull() ?: (60L * 60L * 1000L)
        }

        if (!canExactAlarms()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingExact = true
                requestExactAlarmAccess.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
            return
        }

        scheduler.start(mode, fixed)
        running = scheduler.isRunning()

        if (!hasNotificationPermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun stopScheduling() {
        scheduler.stop()
        running = false
    }

    KihezTheme {
        AnimatedContent(
            targetState = showQuestionsList,
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
                } else {
                    slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
                }
            },
            label = "screenTransition"
        ) { showingQuestions ->
            if (showingQuestions) {
                QuestionsListScreen(
                    onBack = { showQuestionsList = false },
                    questions = NotificationScheduler.MINDFULNESS_QUESTIONS,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                KihezScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = KihezUiState(
                        running = running,
                        mode = mode,
                        hoursText = hoursText,
                        minutesText = minutesText,
                        fixedValid = fixedIntervalMillisOrNull() != null,
                        hasNotificationPermission = hasNotificationPermission(),
                        canExactAlarms = canExactAlarms(),
                        questionMode = questionMode,
                        customQuestionText = customQuestionText
                    ),
                    onHoursChange = { hoursText = it.filter(Char::isDigit) },
                    onMinutesChange = { value ->
                        val digits = value.filter(Char::isDigit)
                        minutesText = if (digits.isEmpty()) {
                            ""
                        } else {
                            (digits.toIntOrNull() ?: 0).coerceIn(0, 59).toString()
                        }
                    },
                    onModeChange = { newMode ->
                        mode = newMode
                        if (running) startScheduling()
                    },
                    onToggleRunning = {
                        if (running) stopScheduling() else startScheduling()
                    },
                    onQuestionModeChange = { newMode ->
                        questionMode = newMode
                        scheduler.setQuestionMode(newMode)
                    },
                    onCustomQuestionTextChange = { text ->
                        customQuestionText = text
                        scheduler.setCustomNotificationText(text)
                    },
                    onNavigateToQuestions = { showQuestionsList = true }
                )
            }
        }
    }
}
