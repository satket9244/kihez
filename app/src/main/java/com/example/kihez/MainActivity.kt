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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.kihez.scheduler.NotificationScheduler
import com.example.kihez.scheduler.NotificationScheduler.Mode
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
    ) {
        // no-op; app keeps scheduling regardless
    }

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

    val fixedValid = fixedIntervalMillisOrNull() != null

    KihezTheme {
        Scaffold { padding ->
            Column(
                modifier = Modifier.padding(padding).padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text("KIHEZ", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))

                Text("Interval mode")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = mode == Mode.FIXED,
                        onClick = {
                            mode = Mode.FIXED
                            if (running) startScheduling()
                        }
                    )
                    Text("Fixed")
                }

                if (mode == Mode.FIXED) {
                    Spacer(Modifier.height(8.dp))
                    Text("Hours")
                    TextField(
                        value = hoursText,
                        onValueChange = { hoursText = it.filter(Char::isDigit) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Minutes (0-59)")
                    TextField(
                        value = minutesText,
                        onValueChange = { value ->
                            val digits = value.filter(Char::isDigit)
                            if (digits.isEmpty()) {
                                minutesText = ""
                            } else {
                                val parsed = digits.toIntOrNull() ?: 0
                                minutesText = parsed.coerceIn(0, 59).toString()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        isError = !fixedValid
                    )
                    if (!fixedValid) {
                        Spacer(Modifier.height(4.dp))
                        Text("Enter at least 1 minute total.", color = MaterialTheme.colorScheme.error)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = mode == Mode.RANDOM,
                        onClick = {
                            mode = Mode.RANDOM
                            if (running) startScheduling()
                        }
                    )
                    Text("Random (15 min - 4 hours)")
                }

                Spacer(Modifier.height(20.dp))
                Button(onClick = { if (running) stopScheduling() else startScheduling() }) {
                    Text(if (running) "Stop" else "Start")
                }

                Spacer(Modifier.height(12.dp))
                Text("Running: $running")
                Text("POST_NOTIFICATIONS: ${hasNotificationPermission()}")
                Text("Exact alarms allowed: ${canExactAlarms()}")
            }
        }
    }
}
