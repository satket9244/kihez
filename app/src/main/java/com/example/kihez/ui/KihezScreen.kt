package com.example.kihez.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.kihez.scheduler.NotificationScheduler
import com.example.kihez.scheduler.NotificationScheduler.Mode
import com.example.kihez.ui.theme.GlassWhite
import com.example.kihez.ui.theme.ManropeFontFamily
import com.example.kihez.ui.theme.NewsreaderFontFamily
import com.example.kihez.ui.theme.OutlineVariant
import com.example.kihez.ui.theme.Primary
import com.example.kihez.ui.theme.Secondary
import com.example.kihez.ui.theme.Surface as KihezSurface
import com.example.kihez.ui.theme.SurfaceContainer
import com.example.kihez.ui.theme.Tertiary
import kotlinx.coroutines.launch
import kotlin.math.abs

data class KihezUiState(
    val running: Boolean,
    val mode: Mode,
    val hoursText: String,
    val minutesText: String,
    val fixedValid: Boolean,
    val hasNotificationPermission: Boolean,
    val canExactAlarms: Boolean,
    val questionMode: NotificationScheduler.QuestionMode,
    val customQuestionText: String
)

@Composable
fun KihezScreen(
    state: KihezUiState,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onModeChange: (Mode) -> Unit,
    onToggleRunning: () -> Unit,
    onQuestionModeChange: (NotificationScheduler.QuestionMode) -> Unit,
    onCustomQuestionTextChange: (String) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HeroSection()

            QuestionSelectionCard(
                questionMode = state.questionMode,
                customQuestionText = state.customQuestionText,
                onQuestionModeChange = onQuestionModeChange,
                onCustomQuestionTextChange = onCustomQuestionTextChange
            )

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
                    modifier = Modifier.weight(1f)
                )
                TimeInputCard(
                    label = "Perc",
                    value = state.minutesText,
                    onValueChange = onMinutesChange,
                    enabled = state.mode == Mode.FIXED,
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
                elevation = 4.dp,
                spotColor = Primary.copy(alpha = 0.03f),
                ambientColor = Primary.copy(alpha = 0.03f)
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Kihez tartozik ez?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Primary
                ),
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
            style = MaterialTheme.typography.displayMedium,
            color = Primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuestionSelectionCard(
    questionMode: NotificationScheduler.QuestionMode,
    customQuestionText: String,
    onQuestionModeChange: (NotificationScheduler.QuestionMode) -> Unit,
    onCustomQuestionTextChange: (String) -> Unit
) {
    val isKihez = questionMode == NotificationScheduler.QuestionMode.KIHEZ
    val isZokkent = questionMode == NotificationScheduler.QuestionMode.ZOKKENT
    val isSajat = questionMode == NotificationScheduler.QuestionMode.SAJAT

    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kérdés választása",
                style = MaterialTheme.typography.headlineMedium,
                color = Primary
            )
            Icon(
                imageVector = Icons.Outlined.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row 1: Kihez tartozik ez?
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kihez tartozik ez?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                MindfulSwitch(
                    checked = isKihez,
                    onCheckedChange = { if (it) onQuestionModeChange(NotificationScheduler.QuestionMode.KIHEZ) }
                )
            }

            // Row 2: Zökkents ki a valóságomból!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zökkents ki a valóságomból!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                MindfulSwitch(
                    checked = isZokkent,
                    onCheckedChange = { if (it) onQuestionModeChange(NotificationScheduler.QuestionMode.ZOKKENT) }
                )
            }

            // Row 3: Saját kérdés
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saját kérdés",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    MindfulSwitch(
                        checked = isSajat,
                        onCheckedChange = { if (it) onQuestionModeChange(NotificationScheduler.QuestionMode.SAJAT) }
                    )
                }

                AnimatedVisibility(
                    visible = isSajat,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextField(
                            value = customQuestionText,
                            onValueChange = onCustomQuestionTextChange,
                            placeholder = {
                                Text(
                                    text = "Ide írd a kérdésed...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
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
                style = MaterialTheme.typography.headlineMedium,
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
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
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
            text = text,
            style = MaterialTheme.typography.labelMedium,
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
                spotColor = Primary.copy(alpha = 0.08f),
                ambientColor = Primary.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = GlassWhite
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
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
    modifier: Modifier = Modifier
) {
    val alphaState by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.4f,
        animationSpec = tween(300),
        label = "timeInputAlpha"
    )

    GlassCard(
        modifier = modifier
            .graphicsLayer(alpha = alphaState)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (enabled) {
            val intValue = value.toIntOrNull() ?: 0
            WheelPicker(
                selectedValue = intValue,
                onValueChange = { onValueChange(it.toString()) },
                maxLimit = if (label == "Óra") 23 else 59,
                label = label,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (value.isBlank()) "0" else value,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = NewsreaderFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Primary.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    maxLimit: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    val items = remember(maxLimit) { (0..maxLimit).toList() }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedValue)
    val coroutineScope = rememberCoroutineScope()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState.firstVisibleItemIndex) {
        val stabilizedIndex = listState.firstVisibleItemIndex
        if (stabilizedIndex in items.indices && stabilizedIndex != selectedValue) {
            onValueChange(items[stabilizedIndex])
        }
    }

    LaunchedEffect(selectedValue) {
        if (selectedValue in items.indices && selectedValue != listState.firstVisibleItemIndex) {
            listState.animateScrollToItem(selectedValue)
        }
    }

    val itemHeight = 40.dp

    Box(
        modifier = modifier
            .height(itemHeight * 3)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(Primary.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                .border(1.dp, Primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight),
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items.size) { index ->
                val itemValue = items[index]
                val itemText = if (itemValue < 10) "0$itemValue" else itemValue.toString()

                val isSelected = listState.firstVisibleItemIndex == index

                val scale by remember {
                    derivedStateOf {
                        val layoutInfo = listState.layoutInfo
                        val visibleItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                        if (visibleItem != null) {
                            val itemCenter = visibleItem.offset + visibleItem.size / 2f
                            val containerCenter = layoutInfo.viewportEndOffset / 2f
                            val distance = abs(itemCenter - containerCenter)
                            val fraction = (1f - (distance / visibleItem.size.toFloat())).coerceIn(0f, 1f)
                            1.0f + 0.25f * fraction
                        } else {
                            if (isSelected) 1.25f else 1.0f
                        }
                    }
                }

                val alpha by remember {
                    derivedStateOf {
                        val layoutInfo = listState.layoutInfo
                        val visibleItem = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                        if (visibleItem != null) {
                            val itemCenter = visibleItem.offset + visibleItem.size / 2f
                            val containerCenter = layoutInfo.viewportEndOffset / 2f
                            val distance = abs(itemCenter - containerCenter)
                            val fraction = (1f - (distance / visibleItem.size.toFloat())).coerceIn(0f, 1f)
                            0.4f + 0.6f * fraction
                        } else {
                            if (isSelected) 1.0f else 0.4f
                        }
                    }
                }

                val fontStyle = if (isSelected) {
                    MaterialTheme.typography.displaySmall.copy(
                        fontFamily = NewsreaderFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                } else {
                    MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemText,
                        style = fontStyle,
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                alpha = alpha
                            ),
                        textAlign = TextAlign.Center
                    )
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
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(
                        isActive = running,
                        activeContainerColor = Primary.copy(alpha = 0.05f),
                        activeBorderColor = Primary.copy(alpha = 0.2f),
                        activeContentColor = Primary,
                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        inactiveContentColor = MaterialTheme.colorScheme.outline,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (running) Tertiary else MaterialTheme.colorScheme.error)
                            )
                        },
                        label = if (running) "Aktív" else "Inaktív"
                    )

                    StatusChip(
                        isActive = hasNotificationPermission,
                        activeContainerColor = Primary.copy(alpha = 0.05f),
                        activeBorderColor = Primary.copy(alpha = 0.2f),
                        activeContentColor = Primary,
                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        inactiveContentColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = if (hasNotificationPermission) Primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = "Értesítések bekapcsolva"
                    )
                }

                StatusChip(
                    isActive = canExactAlarms,
                    activeContainerColor = Tertiary.copy(alpha = 0.05f),
                    activeBorderColor = Tertiary.copy(alpha = 0.2f),
                    activeContentColor = Tertiary,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    inactiveContentColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = null,
                            tint = if (canExactAlarms) Tertiary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = "Precíziós riasztás"
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
    isActive: Boolean,
    activeContainerColor: Color,
    activeBorderColor: Color,
    activeContentColor: Color,
    inactiveContainerColor: Color,
    inactiveBorderColor: Color,
    inactiveContentColor: Color,
    icon: @Composable () -> Unit,
    label: String
) {
    val containerColor = if (isActive) activeContainerColor else inactiveContainerColor
    val borderColor = if (isActive) activeBorderColor else inactiveBorderColor
    val contentColor = if (isActive) activeContentColor else inactiveContentColor

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(containerColor)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
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
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MindfulSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val switchWidth = 44.dp
    val switchHeight = 24.dp
    val thumbSize = 20.dp

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 2.dp,
        animationSpec = tween(300),
        label = "thumbOffset"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) Primary else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(300),
        label = "trackColor"
    )

    Box(
        modifier = modifier
            .size(switchWidth, switchHeight)
            .clip(CircleShape)
            .background(trackColor)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .shadow(2.dp, CircleShape)
                .background(Color.White, CircleShape)
        )
    }
}
