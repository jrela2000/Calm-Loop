package com.jarrodqsimpson.calmloop.ui.journal

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarrodqsimpson.calmloop.ui.components.GlassCard
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.theme.Lavender
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextMuted
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlue
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlueDark
import kotlinx.coroutines.delay

@Composable
fun VoiceJournalScreen(
    onNavigateToProcessing: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isVoiceMode by remember { mutableStateOf(true) }
    var isRecording by remember { mutableStateOf(false) }
    var transcriptionText by remember { mutableStateOf("") }
    var recordingTimerSeconds by remember { mutableIntStateOf(0) }
    var speechSupported by remember { mutableStateOf(SpeechRecognizer.isRecognitionAvailable(context)) }

    // Recording timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingTimerSeconds = 0
            while (isRecording) {
                delay(1000)
                recordingTimerSeconds++
            }
        }
    }

    // Pulse animation for recording ring
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse_scale"
    )

    val speechRecognizer = remember {
        if (speechSupported) SpeechRecognizer.createSpeechRecognizer(context) else null
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    fun startListening() {
        if (speechRecognizer == null) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault().toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isRecording = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) { isRecording = false }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val currentText = transcriptionText
                    val newSpeech = matches[0]
                    transcriptionText = if (currentText.isBlank()) newSpeech else "$currentText $newSpeech"
                }
                isRecording = false
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    // Show live partial result
                    val partial = matches[0]
                    if (!transcriptionText.endsWith(partial)) {
                        transcriptionText = partial
                    }
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        isRecording = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening()
        }
    }

    fun onRecordClick() {
        if (isRecording) {
            stopListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun onSaveEntry() {
        if (transcriptionText.isNotBlank()) {
            onNavigateToProcessing(transcriptionText)
        }
    }

    ImmersiveBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("back_button")
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

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Voice Journal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Exhale your thoughts safely",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Speak / Type Mode Toggle
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
                color = Color.White.copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ModeTabButton(
                        text = "🎙️ Speak",
                        selected = isVoiceMode,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_speak_tab"),
                        onClick = { isVoiceMode = true }
                    )
                    ModeTabButton(
                        text = "✍️ Type",
                        selected = !isVoiceMode,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_type_tab"),
                        onClick = {
                            if (isRecording) stopListening()
                            isVoiceMode = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isVoiceMode) {
                // VOICE MODE UI
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Pulse Ring Mic
                    Box(contentAlignment = Alignment.Center) {
                        if (isRecording) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(Lavender.copy(alpha = 0.35f))
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(115.dp)
                                .shadow(12.dp, CircleShape, spotColor = SoftBlue.copy(alpha = 0.5f))
                                .border(2.dp, Color.White, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        if (isRecording) listOf(Color(0xFFEF4444), Lavender)
                                        else listOf(SoftBlue, Lavender)
                                    )
                                )
                                .clickable { onRecordClick() }
                                .testTag("mic_record_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val minutes = recordingTimerSeconds / 60
                    val seconds = recordingTimerSeconds % 60
                    Text(
                        text = if (isRecording) String.format("%02d:%02d", minutes, seconds) else "Tap to speak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = if (isRecording) Color(0xFFDC2626) else SlateTextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Live Transcript Glass Card
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("live_transcript_card")
                    ) {
                        Column {
                            Text(
                                text = "LIVE TRANSCRIPTION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = SlateTextMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (transcriptionText.isBlank())
                                    "Speak whatever comes to mind... panic, worries, physical sensations. The AI will structure it safely."
                                else transcriptionText,
                                fontSize = 15.sp,
                                color = if (transcriptionText.isBlank()) SlateTextMuted
                                else SlateTextPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            } else {
                // TYPE MODE UI
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "TYPE YOUR THOUGHTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = SlateTextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = transcriptionText,
                        onValueChange = { transcriptionText = it },
                        placeholder = {
                            Text("What triggered this anxiety? What thoughts or body sensations are you feeling?", color = SlateTextMuted)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .testTag("type_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.9f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                            focusedBorderColor = SoftBlue,
                            unfocusedBorderColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${transcriptionText.length} characters",
                        fontSize = 12.sp,
                        color = SlateTextMuted,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            // Save Entry Action Button
            Button(
                onClick = { onSaveEntry() },
                enabled = transcriptionText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = SoftBlueDark.copy(alpha = 0.4f))
                    .testTag("save_journal_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftBlueDark,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE2E8F0),
                    disabledContentColor = SlateTextMuted
                )
            ) {
                Text(
                    text = "Organize Thoughts with AI ✨",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ModeTabButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (selected) SoftBlue.copy(alpha = 0.35f) else Color.Transparent
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp,
                color = if (selected) SlateTextPrimary else SlateTextSecondary
            )
        }
    }
}
