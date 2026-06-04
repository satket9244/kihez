# KIHEZ App Documentation

## Overview

KIHEZ is an Android application designed to send periodic notifications to help users maintain mindful presence. The app allows users to set up notifications at fixed or random intervals to remind them to check in with themselves throughout the day.

## Project Structure

```
KIHEZ/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/kihez/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── scheduler/
│   │   │   │   │   ├── BootReceiver.kt
│   │   │   │   │   ├── NotificationReceiver.kt
│   │   │   │   │   └── NotificationScheduler.kt
│   │   │   │   └── ui/
│   │   │   │       ├── KihezScreen.kt
│   │   │   │       └── theme/
│   │   │   │           ├── Color.kt
│   │   │   │           ├── Theme.kt
│   │   │   │           └── Type.kt
│   │   │   └── res/
│   │   │       └── values/
│   │   │           ├── strings.xml
│   │   │           └── themes.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradle/
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Android Application with Broadcast Receivers for scheduling
- **Minimum SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34 (Android 14)

## Key Features

1. **Notification Scheduling**: Schedule periodic notifications at fixed or random intervals
2. **Mindful Presence Reminders**: Sends "Kihez tartozik ez?" notifications to remind users to check in with themselves
3. **Boot Completion Handling**: Resumes notification scheduling after device restart
4. **Permission Management**: Handles Android permissions for notifications and exact alarms
5. **Material Design 3**: Modern UI with Material Design components

## Architecture

### Main Components

1. **MainActivity.kt** - Entry point of the application
2. **NotificationScheduler.kt** - Handles scheduling logic
3. **NotificationReceiver.kt** - Receives and displays notifications
4. **BootReceiver.kt** - Handles device boot completion to restart scheduling
5. **KihezScreen.kt** - Main UI component
6. **Theme files** - Color, typography, and theme definitions

### Core Functionality

The app allows users to:
- Choose between fixed interval or random interval notifications
- Set custom time intervals for fixed notifications
- Enable/disable notification scheduling
- View app status and permissions

## Code Structure

### 1. App Entry Point - MainActivity.kt

The `MainActivity` is the main entry point of the application. It handles:
- UI rendering using Jetpack Compose
- Permission management for notifications and exact alarms
- User interaction with scheduling controls

Key features:
- Compose-based UI implementation
- Permission handling for Android 13+ (API level 33+) notification permissions
- Exact alarm permission handling for Android 12+ (API level 31+)
- State management for the scheduling controls

### 2. Scheduling System

#### NotificationScheduler.kt
Handles the core scheduling logic:
- Scheduling of notifications at fixed or random intervals
- Storage of user preferences using SharedPreferences
- Calculation of notification delays
- Integration with Android's AlarmManager

Key constants:
- `FIXED`: Fixed interval mode (user-defined interval)
- `RANDOM`: Random interval mode (15 minutes to 4 hours)
- Default interval: 1 hour (3600000 milliseconds)

#### NotificationReceiver.kt
Handles the actual notification display:
- Creates notification channel for Android Oreo+
- Shows notifications with the text "Kihez tartozik ez?"
- Reschedules the next notification if the scheduler is still running

#### BootReceiver.kt
Listens for `BOOT_COMPLETED` broadcasts to restart scheduling after device reboot.

### 3. UI Components - KihezScreen.kt

The main UI is built with Jetpack Compose and includes:
- Hero section with app branding
- Interval mode selection (Fixed vs Random)
- Time input controls for fixed intervals
- Start/Stop button for enabling/disabling notifications
- Status indicators for app state and permissions

### 4. Theming

The app uses a custom color scheme defined in the theme package:
- Primary color: Dark teal (`#124343`)
- Glass-like UI components for a modern look
- Custom typography and styling

## Permissions Required

The app requires the following permissions:
1. `POST_NOTIFICATIONS` - To show notifications (Android 13+)
2. `SCHEDULE_EXACT_ALARM` - To schedule precise notifications
3. `RECEIVE_BOOT_COMPLETED` - To restart scheduling after boot

## Development Setup

### Prerequisites
- Android Studio Iguana or later
- Kotlin 1.9.24 or later
- Android Gradle Plugin 8.13.2
- Android 8.0 (API 26) or later

### Dependencies
- `androidx.core:core-ktx`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.activity:activity-compose`
- `androidx.compose.ui:ui`
- `androidx.compose.ui:ui-tooling-preview`
- `androidx.compose.material3:material3`
- `androidx.compose.material:material-icons-extended`

## Building and Running

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Build and run the project

## Testing

The app includes configurations for:
- Local unit tests
- Instrumented tests for Compose UI
- AndroidJUnitRunner for instrumentation testing

## Customization Points

1. **Notification Text**: Modify `NOTIFICATION_TEXT` in `NotificationScheduler.kt`
2. **Time Intervals**: Adjust `RANDOM_MIN_MILLIS` and `RANDOM_MAX_MILLIS` in `NotificationScheduler.kt`
3. **UI Colors**: Modify `Color.kt` to change the color scheme
4. **Typography**: Adjust `Type.kt` to change text styles

## Troubleshooting

### Common Issues

1. **Notifications not appearing**: Check that all required permissions are granted
2. **App not starting on boot**: Ensure BootReceiver is properly registered
3. **Incorrect intervals**: Verify system time and timezone settings

### Debugging Tips

1. Check logcat for any permission-related errors
2. Verify AlarmManager is working correctly in Android settings
3. Ensure the app is not battery optimized in device settings

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write tests if applicable
5. Submit a pull request

## License

This project is proprietary and intended for internal use only. All rights reserved.