package com.example.kihez.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.kihez.scheduler.NotificationScheduler.Mode
import com.example.kihez.ui.theme.GlassWhite
import com.example.kihez.ui.theme.OutlineVariant
import com.example.kihez.ui.theme.Primary
import com.example.kihez.ui.theme.Surface as KihezSurface
import com.example.kihez.ui.theme.SurfaceContainer
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

data class KihezUiState(
    val running: Boolean,
    val mode: Mode,
    val hoursText: String,
    val minutesText: String,
    val fixedValid: Boolean,
    val hasNotificationPermission: Boolean,
    val canExactAlarms: Boolean
)

@Composable
fun KihezScreen(
    state: KihezUiState,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onModeChange: (Mode) -> Unit,
    onToggleRunning: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KihezSurface)
    ) {
        KihezTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            HeroSection()

            IntervalModeCard(
                mode = state.mode,
                onModeChange = onModeChange
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimeInputCard(
                    label = "Óra",
                    value = state.hoursText,
                    onValueChange = onHoursChange,
                    enabled = state.mode == Mode.FIXED,
                    isError = state.mode == Mode.FIXED && !state.fixedValid,
                    modifier = Modifier.weight(1f)
                )
                TimeInputCard(
                    label = "Perc",
                    value = state.minutesText,
                    onValueChange = onMinutesChange,
                    enabled = state.mode == Mode.FIXED,
                    isError = state.mode == Mode.FIXED && !state.fixedValid,
                    modifier = Modifier.weight(1f)
                )
            }

            if (state.mode == Mode.FIXED && !state.fixedValid) {
                Text(
                    text = "Legalább 1 perc szükséges.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            PrimaryActionButton(
                running = state.running,
                onClick = onToggleRunning
            )

            StatusSection(
                running = state.running,
                hasNotificationPermission = state.hasNotificationPermission,
                canExactAlarms = state.canExactAlarms
            )
        }
    }
}

@Composable
private fun KihezTopBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                spotColor = Primary.copy(alpha = 0.05f),
                ambientColor = Primary.copy(alpha = 0.05f)
            ),
        color = KihezSurface.copy(alpha = 0.8f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Spa,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Kihez tartozik ez?",
                style = MaterialTheme.typography.displaySmall,
                color = Primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HeroSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "EMLÉKEZTETŐ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "Tudatos jelenlét",
            style = MaterialTheme.typography.headlineMedium,
            color = Primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun IntervalModeCard(
    mode: Mode,
    onModeChange: (Mode) -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Időköz",
                style = MaterialTheme.typography.titleMedium,
                color = Primary
            )
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ModeToggle(
            selectedMode = mode,
            onModeChange = onModeChange
        )

        AnimatedVisibility(
            visible = mode == Mode.RANDOM,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "(15 perc - 4 óra)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ModeToggle(
    selectedMode: Mode,
    onModeChange: (Mode) -> Unit
) {
    val isFixed = selectedMode == Mode.FIXED

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(SurfaceContainer)
            .padding(4.dp)
    ) {
        val segmentWidth = maxWidth / 2
        val sliderOffset by animateDpAsState(
            targetValue = if (isFixed) 0.dp else segmentWidth,
            animationSpec = tween(500),
            label = "modeSlider"
        )

        Box(
            modifier = Modifier
                .width(segmentWidth - 4.dp)
                .height(48.dp)
                .offset(x = sliderOffset)
                .clip(CircleShape)
                .background(Primary)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            ModeToggleLabel(
                text = "Fix",
                selected = isFixed,
                onClick = { onModeChange(Mode.FIXED) },
                modifier = Modifier.weight(1f)
            )
            ModeToggleLabel(
                text = "Véletlenszerű",
                selected = !isFixed,
                onClick = { onModeChange(Mode.RANDOM) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModeToggleLabel(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Primary.copy(alpha = 0.05f),
                ambientColor = Primary.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = GlassWhite
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            content()
        }
    }
}

@Composable
private fun TimeInputCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        enabled -> Color.Transparent
        else -> Color.Transparent
    }

    GlassCard(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (enabled) {
            // Use scrollable number picker for enabled state
            val intValue = value.toIntOrNull() ?: 0
            ScrollableNumberPicker(
                value = intValue,
                onValueChange = { onValueChange(it.toString()) },
                maxValue = if (label == "Óra") 23 else 59,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Show read-only value
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = if (enabled) Primary else Primary.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun ScrollableNumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    maxValue: Int,
    modifier: Modifier = Modifier
) {
    val minSize = if (maxValue == 23) 0 else 0
    val items = (minSize..maxValue).toList()
    val selectedIndex = items.indexOf(value).coerceIn(0, items.size - 1)
    
    val state = rememberScrollState(initial = selectedIndex)
    val scope = rememberCoroutineScope()
    
    val itemHeight = 48.dp
    val halfItemHeight = itemHeight / 2
    val visibleItems = 3
    val centerIndex = 1
    val totalScrollRange = (itemHeight * (items.size - visibleItems)).roundToPx().toFloat()
    
    Box(
        modifier = modifier
            .height(itemHeight * 3)
            .fillMaxWidth()
    ) {
        // Center indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .offset(y = itemHeight)
                .zIndex(1f)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = Primary.copy(alpha = 0.5f)
                    )
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .scrollable(
                    state = rememberScrollableState { delta ->
                        scope.launch {
                            state.scrollBy(-delta)
                        }
                        delta
                    },
                    orientation = Orientation.Vertical
                )
        ) {
            val currentValue = (state.value / itemHeight.roundToPx()).coerceIn(0, items.size - 1)
            onValueChange(items[currentValue])
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = halfItemHeight, bottom = halfItemHeight)
            ) {
                repeat(items.size) { index ->
                    val itemValue = items[index]
                    val itemText = if (itemValue < 10) "0$itemValue" else itemValue.toString()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = itemText,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryActionButton(
    running: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                spotColor = Primary.copy(alpha = 0.2f)
            ),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = if (running) "Értesítések kikapcsolása" else "Értesítések bekapcsolása",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun StatusSection(
    running: Boolean,
    hasNotificationPermission: Boolean,
    canExactAlarms: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FadeDivider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (running) Primary else MaterialTheme.color徽rScheme.error
                                )
                        )
                    },
                    label = if (running) "Aktív" else "Inaktív"
                )
                StatusChip(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            tint = if (hasNotificationPermission) Primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = "Értesítések bekapcsolva",
                    muted = !hasNotificationPermission
                )
                StatusChip(
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = null,
                            tint = if (canExactAlarms) Primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = "Precíziós riasztás",
                    muted = !canExactAlarms
                )
            }
        }
    }
}

@Composable
private fun FadeDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        OutlineVariant,
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun StatusChip(
    icon: @Composable () -> Unit,
    label: String,
    muted: Boolean = false
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 1.dp,
                color = OutlineVariant.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon()
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.85f,
                letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing * 0.5f
            ),
            color = if (muted) MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
