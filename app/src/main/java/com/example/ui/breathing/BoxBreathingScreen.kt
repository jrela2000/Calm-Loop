package com.example.ui.breathing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ImmersiveBackground
import com.example.ui.theme.Lavender
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.Sage
import com.example.ui.theme.SageDark
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.SoftBlueDark
import com.example.ui.viewModel.CalmLoopViewModel
import com.example.util.SoundSynth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

enum class BreathingStep { INTRO, BREATHE, CHECKIN }

@Composable
fun BoxBreathingScreen(
    viewModel: CalmLoopViewModel,
    onNavigateToGrounding: () -> Unit,
    onNavigateToCrisis: () -> Unit,
    onBack: () -> Unit
) {
    var step by remember { mutableStateOf(BreathingStep.INTRO) }
    var soundEnabled by remember { mutableStateOf(true) }
    var targetRounds by remember { mutableIntStateOf(3) }
    var currentRound by remember { mutableIntStateOf(1) }
    var currentPhaseName by remember { mutableStateOf("Inhale") }
    var currentPhaseIndex by remember { mutableIntStateOf(0) }
    var secondsInPhase by remember { mutableIntStateOf(4) }

    val circleScale = remember { Animatable(0.6f) }
    val phaseProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Breathing loop execution
    LaunchedEffect(step) {
        if (step == BreathingStep.BREATHE) {
            currentRound = 1
            while (currentRound <= targetRounds && step == BreathingStep.BREATHE) {
                // Phase 0: Inhale (4s)
                currentPhaseIndex = 0
                currentPhaseName = "Inhale"
                if (soundEnabled) SoundSynth.playChime(396.0)
                coroutineScope.launch {
                    phaseProgress.snapTo(0f)
                    phaseProgress.animateTo(1f, animationSpec = tween(4000, easing = LinearEasing))
                }
                coroutineScope.launch {
                    circleScale.animateTo(1.15f, animationSpec = tween(4000, easing = FastOutSlowInEasing))
                }
                for (s in 4 downTo 1) {
                    secondsInPhase = s
                    delay(1000)
                }

                // Phase 1: Hold Full (4s)
                currentPhaseIndex = 1
                currentPhaseName = "Hold"
                if (soundEnabled) SoundSynth.playChime(528.0)
                coroutineScope.launch {
                    phaseProgress.snapTo(0f)
                    phaseProgress.animateTo(1f, animationSpec = tween(4000, easing = LinearEasing))
                }
                coroutineScope.launch {
                    circleScale.animateTo(1.18f, animationSpec = tween(2000, easing = FastOutSlowInEasing))
                    circleScale.animateTo(1.15f, animationSpec = tween(2000, easing = FastOutSlowInEasing))
                }
                for (s in 4 downTo 1) {
                    secondsInPhase = s
                    delay(1000)
                }

                // Phase 2: Exhale (4s)
                currentPhaseIndex = 2
                currentPhaseName = "Exhale"
                if (soundEnabled) SoundSynth.playChime(639.0)
                coroutineScope.launch {
                    phaseProgress.snapTo(0f)
                    phaseProgress.animateTo(1f, animationSpec = tween(4000, easing = LinearEasing))
                }
                coroutineScope.launch {
                    circleScale.animateTo(0.6f, animationSpec = tween(4000, easing = FastOutSlowInEasing))
                }
                for (s in 4 downTo 1) {
                    secondsInPhase = s
                    delay(1000)
                }

                // Phase 3: Hold Empty (4s)
                currentPhaseIndex = 3
                currentPhaseName = "Hold"
                if (soundEnabled) SoundSynth.playChime(741.0)
                coroutineScope.launch {
                    phaseProgress.snapTo(0f)
                    phaseProgress.animateTo(1f, animationSpec = tween(4000, easing = LinearEasing))
                }
                coroutineScope.launch {
                    circleScale.animateTo(0.58f, animationSpec = tween(2000, easing = FastOutSlowInEasing))
                    circleScale.animateTo(0.60f, animationSpec = tween(2000, easing = FastOutSlowInEasing))
                }
                for (s in 4 downTo 1) {
                    secondsInPhase = s
                    delay(1000)
                }

                if (currentRound < targetRounds) {
                    currentRound++
                } else {
                    break
                }
            }
            if (step == BreathingStep.BREATHE) {
                step = BreathingStep.CHECKIN
            }
        }
    }

    ImmersiveBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("breathing_back_button")
                        .size(42.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SlateTextPrimary
                    )
                }

                Text(
                    text = "Box Breathing Coach",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )

                IconButton(
                    onClick = { soundEnabled = !soundEnabled },
                    modifier = Modifier
                        .testTag("sound_toggle_button")
                        .size(42.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Sound Toggle",
                        tint = if (soundEnabled) SoftBlueDark else SlateTextSecondary
                    )
                }
            }

            when (step) {
                BreathingStep.INTRO -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(130.dp)
                                .shadow(12.dp, CircleShape, spotColor = SoftBlue.copy(alpha = 0.4f))
                                .border(2.dp, Color.White, CircleShape),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                SoftBlue.copy(alpha = 0.3f),
                                                Sage.copy(alpha = 0.2f),
                                                Color.White
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🫁", fontSize = 52.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Reset Your Nervous System",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "4s Inhale · 4s Hold · 4s Exhale · 4s Hold\nLower heart rate instantly with steady 4-part rhythm.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = SlateTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Round Picker
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Rounds:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SlateTextPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            listOf(1, 3, 5).forEach { r ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (targetRounds == r) SoftBlueDark else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .clickable { targetRounds = r }
                                        .testTag("round_selector_$r"),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (targetRounds == r) SoftBlueDark else Color.White
                                    )
                                ) {
                                    Text(
                                        text = "$r",
                                        fontWeight = FontWeight.Bold,
                                        color = if (targetRounds == r) Color.White else SlateTextPrimary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        Button(
                            onClick = { step = BreathingStep.BREATHE },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftBlueDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = SoftBlueDark.copy(alpha = 0.4f))
                                .testTag("start_breathing_button")
                        ) {
                            Text(
                                "Begin Box Breathing",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                BreathingStep.BREATHE -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Round $currentRound of $targetRounds",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        BoxBreathingVisualizer(
                            circleScale = circleScale.value,
                            phaseIndex = currentPhaseIndex,
                            phaseProgress = phaseProgress.value,
                            currentPhaseName = currentPhaseName,
                            secondsInPhase = secondsInPhase
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        OutlinedButton(
                            onClick = { step = BreathingStep.CHECKIN },
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            modifier = Modifier.testTag("finish_early_button")
                        ) {
                            Text("Finish Early", color = SlateTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                BreathingStep.CHECKIN -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🌸", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Great job exhaling.",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "How is your heart rate and mind feeling right now?",
                            fontSize = 14.sp,
                            color = SlateTextSecondary
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        CheckInOutcomeButton(
                            text = "💚 I'm Feeling OK Now",
                            tag = "outcome_ok",
                            onClick = {
                                viewModel.saveBreathingSession(currentRound, "ok")
                                onBack()
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CheckInOutcomeButton(
                            text = "🫁 Keep Breathing (More Rounds)",
                            tag = "outcome_continue",
                            onClick = {
                                viewModel.saveBreathingSession(currentRound, "continue")
                                step = BreathingStep.BREATHE
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CheckInOutcomeButton(
                            text = "🖐️ Switch to 5-4-3-2-1 Grounding Game",
                            tag = "outcome_switch_grounding",
                            onClick = {
                                viewModel.saveBreathingSession(currentRound, "switch")
                                onNavigateToGrounding()
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CheckInOutcomeButton(
                            text = "📞 Need to Talk / Crisis Helpline",
                            tag = "outcome_call_crisis",
                            onClick = {
                                viewModel.saveBreathingSession(currentRound, "call")
                                onNavigateToCrisis()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxBreathingVisualizer(
    circleScale: Float,
    phaseIndex: Int,
    phaseProgress: Float,
    currentPhaseName: String,
    secondsInPhase: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    val accentColor = when (phaseIndex) {
        0 -> SoftBlueDark
        1 -> LavenderDark
        2 -> SageDark
        else -> SoftBlueDark
    }

    val phaseGuidance = when (phaseIndex) {
        0 -> "Inhale through nose"
        1 -> "Hold breath calmly"
        2 -> "Exhale slowly"
        else -> "Rest in stillness"
    }

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)
            val padding = 28.dp.toPx()
            val boxW = width - 2 * padding
            val boxH = height - 2 * padding
            val cornerRadius = 24.dp.toPx()

            // 1. Central Ambient Layers & Rotating Petals
            drawCircle(
                color = accentColor.copy(alpha = 0.10f),
                radius = (80.dp.toPx()) * circleScale,
                center = center
            )
            drawCircle(
                color = accentColor.copy(alpha = 0.20f),
                radius = (64.dp.toPx()) * circleScale,
                center = center
            )

            val petalRadius = 36.dp.toPx() * circleScale
            val petalDistance = 26.dp.toPx() * circleScale
            for (i in 0 until 6) {
                val angleRad = Math.toRadians((i * 60 + rotationAngle).toDouble())
                val petalCenter = Offset(
                    (center.x + cos(angleRad) * petalDistance).toFloat(),
                    (center.y + sin(angleRad) * petalDistance).toFloat()
                )
                drawCircle(
                    color = accentColor.copy(alpha = 0.15f),
                    radius = petalRadius,
                    center = petalCenter
                )
            }

            val coreRadius = 48.dp.toPx() * circleScale
            drawCircle(
                color = Color.White.copy(alpha = 0.92f),
                radius = coreRadius,
                center = center
            )
            drawCircle(
                color = accentColor.copy(alpha = 0.35f),
                radius = coreRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 2. The Box Square Outline
            drawRoundRect(
                color = Color(0xFFCBD5E1),
                topLeft = Offset(padding, padding),
                size = Size(boxW, boxH),
                cornerRadius = CornerRadius(cornerRadius),
                style = Stroke(width = 4.dp.toPx())
            )

            // 3. Active Box Edge & Tracer Point
            var tracerPos = Offset(padding, padding)
            val activeStrokeWidth = 6.dp.toPx()

            when (phaseIndex) {
                0 -> { // Top Edge: Left -> Right
                    val currentX = padding + boxW * phaseProgress
                    tracerPos = Offset(currentX, padding)
                    drawLine(
                        color = SoftBlueDark,
                        start = Offset(padding + cornerRadius / 2, padding),
                        end = tracerPos,
                        strokeWidth = activeStrokeWidth,
                        cap = StrokeCap.Round
                    )
                }
                1 -> { // Right Edge: Top -> Bottom
                    drawLine(
                        color = SoftBlueDark.copy(alpha = 0.5f),
                        start = Offset(padding, padding),
                        end = Offset(padding + boxW, padding),
                        strokeWidth = activeStrokeWidth
                    )
                    val currentY = padding + boxH * phaseProgress
                    tracerPos = Offset(padding + boxW, currentY)
                    drawLine(
                        color = LavenderDark,
                        start = Offset(padding + boxW, padding + cornerRadius / 2),
                        end = tracerPos,
                        strokeWidth = activeStrokeWidth,
                        cap = StrokeCap.Round
                    )
                }
                2 -> { // Bottom Edge: Right -> Left
                    drawLine(
                        color = SoftBlueDark.copy(alpha = 0.4f),
                        start = Offset(padding, padding),
                        end = Offset(padding + boxW, padding),
                        strokeWidth = activeStrokeWidth
                    )
                    drawLine(
                        color = LavenderDark.copy(alpha = 0.4f),
                        start = Offset(padding + boxW, padding),
                        end = Offset(padding + boxW, padding + boxH),
                        strokeWidth = activeStrokeWidth
                    )
                    val currentX = padding + boxW * (1f - phaseProgress)
                    tracerPos = Offset(currentX, padding + boxH)
                    drawLine(
                        color = SageDark,
                        start = Offset(padding + boxW - cornerRadius / 2, padding + boxH),
                        end = tracerPos,
                        strokeWidth = activeStrokeWidth,
                        cap = StrokeCap.Round
                    )
                }
                3 -> { // Left Edge: Bottom -> Top
                    drawLine(
                        color = SoftBlueDark.copy(alpha = 0.3f),
                        start = Offset(padding, padding),
                        end = Offset(padding + boxW, padding),
                        strokeWidth = activeStrokeWidth
                    )
                    drawLine(
                        color = LavenderDark.copy(alpha = 0.3f),
                        start = Offset(padding + boxW, padding),
                        end = Offset(padding + boxW, padding + boxH),
                        strokeWidth = activeStrokeWidth
                    )
                    drawLine(
                        color = SageDark.copy(alpha = 0.3f),
                        start = Offset(padding + boxW, padding + boxH),
                        end = Offset(padding, padding + boxH),
                        strokeWidth = activeStrokeWidth
                    )
                    val currentY = padding + boxH * (1f - phaseProgress)
                    tracerPos = Offset(padding, currentY)
                    drawLine(
                        color = SoftBlueDark,
                        start = Offset(padding, padding + boxH - cornerRadius / 2),
                        end = tracerPos,
                        strokeWidth = activeStrokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 4. Glowing Tracer Orb
            drawCircle(
                color = accentColor.copy(alpha = 0.35f),
                radius = 12.dp.toPx(),
                center = tracerPos
            )
            drawCircle(
                color = accentColor,
                radius = 6.dp.toPx(),
                center = tracerPos
            )
            drawCircle(
                color = Color.White,
                radius = 2.5f.dp.toPx(),
                center = tracerPos
            )
        }

        // Center Content Overlay
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentPhaseName.uppercase(),
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$secondsInPhase",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = phaseGuidance,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SlateTextSecondary
            )
        }
    }
}

@Composable
private fun CheckInOutcomeButton(
    text: String,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag(tag)
            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.75f)
    ) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = SlateTextPrimary
            )
        }
    }
}

