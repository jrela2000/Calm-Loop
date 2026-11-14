package com.jarrodqsimpson.calmloop.ui.grounding

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.theme.Lavender
import com.jarrodqsimpson.calmloop.ui.theme.Sage
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextMuted
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlue
import com.jarrodqsimpson.calmloop.ui.viewModel.CalmLoopViewModel
import com.jarrodqsimpson.calmloop.util.SoundSynth
import kotlinx.coroutines.launch

data class SenseStep(
    val stepIndex: Int,
    val count: Int,
    val title: String,
    val emoji: String,
    val prompt: String,
    val color: Color
)

@Composable
fun GroundingGameScreen(
    viewModel: CalmLoopViewModel,
    onNavigateToBreathing: () -> Unit,
    onNavigateToCrisis: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val senseSteps = remember {
        listOf(
            SenseStep(1, 5, "5 Things You Can SEE", "👁️", "Look around you right now. Name 5 distinct objects you see.", SoftBlue),
            SenseStep(2, 4, "4 Things You Can TOUCH", "✋", "Notice 4 physical textures (clothing, table, chair, ground).", Sage),
            SenseStep(3, 3, "3 Things You Can HEAR", "👂", "Listen closely. Identify 3 subtle background sounds.", Lavender),
            SenseStep(4, 2, "2 Things You Can SMELL", "👃", "Breathe in deeply. Notice 2 scents around you.", SoftBlue),
            SenseStep(5, 1, "1 Thing You Can TASTE", "👅", "Focus on your mouth. Notice 1 taste or sensation.", Sage)
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    var isDone by remember { mutableStateOf(false) }

    // Store answers for all 5 steps
    val answers5 = remember { mutableStateListOf("", "", "", "", "") }
    val answers4 = remember { mutableStateListOf("", "", "", "") }
    val answers3 = remember { mutableStateListOf("", "", "") }
    val answers2 = remember { mutableStateListOf("", "") }
    val answers1 = remember { mutableStateListOf("") }

    var isVoiceActive by remember { mutableStateOf(false) }
    var voiceInputMethodUsed by remember { mutableStateOf(false) }

    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) SpeechRecognizer.createSpeechRecognizer(context) else null
    }

    DisposableEffect(Unit) {
        onDispose { speechRecognizer?.destroy() }
    }

    fun getActiveAnswersList(): MutableList<String> {
        return when (currentStepIndex) {
            0 -> answers5
            1 -> answers4
            2 -> answers3
            3 -> answers2
            else -> answers1
        }
    }

    fun startListeningForStep() {
        if (speechRecognizer == null) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault().toLanguageTag())
        }
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isVoiceActive = true; voiceInputMethodUsed = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) { isVoiceActive = false }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spoken = matches[0]
                    val list = getActiveAnswersList()
                    val idx = list.indexOfFirst { it.isBlank() }
                    if (idx != -1) {
                        list[idx] = spoken
                    } else if (list.isNotEmpty()) {
                        list[0] = spoken
                    }
                }
                isVoiceActive = false
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        speechRecognizer.startListening(intent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startListeningForStep()
    }

    fun advanceStep() {
        coroutineScope.launch { SoundSynth.playChime(528.0, 600) }
        if (currentStepIndex < senseSteps.size - 1) {
            currentStepIndex++
        } else {
            isDone = true
        }
    }

    fun saveAndFinish(outcome: String) {
        viewModel.saveGroundingSession(
            a5 = answers5.toList(),
            a4 = answers4.toList(),
            a3 = answers3.toList(),
            a2 = answers2.toList(),
            a1 = answers1.toList(),
            inputMethod = if (voiceInputMethodUsed) "voice" else "type",
            outcome = outcome
        )
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("grounding_back_button")
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
                    text = "5-4-3-2-1 Grounding Game",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )

                IconButton(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    modifier = Modifier
                        .testTag("grounding_speak_button")
                        .size(42.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isVoiceActive) Lavender else Color.White.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Speak Input",
                        tint = if (isVoiceActive) Color.White else SlateTextPrimary
                    )
                }
            }

            if (!isDone) {
                val currentSense = senseSteps[currentStepIndex]
                val currentAnswers = getActiveAnswersList()

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Indicator Bar
                LinearProgressIndicator(
                    progress = { (currentStepIndex + 1) / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .testTag("grounding_progress_bar"),
                    color = currentSense.color,
                    trackColor = Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Step Glass Banner Card
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(currentSense.emoji, fontSize = 40.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = currentSense.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = SlateTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentSense.prompt,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "TYPE OR SPEAK ITEMS BELOW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = SlateTextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Input items for current step
                    currentAnswers.indices.forEach { index ->
                        OutlinedTextField(
                            value = currentAnswers[index],
                            onValueChange = { currentAnswers[index] = it },
                            placeholder = { Text("${index + 1}. Item...", color = SlateTextMuted) },
                            leadingIcon = {
                                Text("${index + 1}", fontWeight = FontWeight.Bold, color = currentSense.color)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("grounding_input_${currentStepIndex}_$index"),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                                focusedBorderColor = currentSense.color,
                                unfocusedBorderColor = Color.White
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { advanceStep() },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = currentSense.color),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = currentSense.color.copy(alpha = 0.4f))
                        .testTag("grounding_next_button")
                ) {
                    Text(
                        text = if (currentStepIndex == 4) "Finish Grounding ✨" else "Next Sense (${currentStepIndex + 2}/5)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                }
            } else {
                // GAME DONE SUMMARY SCREEN
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Sage.copy(alpha = 0.25f))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Sage,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "You are grounded in the present moment.",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "5 sense items captured. Your physical environment is safe.",
                        fontSize = 14.sp,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    GroundingDoneButton("🖐️ Play Grounding Again", "grounding_again") {
                        saveAndFinish("again")
                        currentStepIndex = 0
                        isDone = false
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GroundingDoneButton("🫁 Try Box Breathing Coach", "grounding_switch_breathing") {
                        saveAndFinish("breathing")
                        onNavigateToBreathing()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GroundingDoneButton("📞 Need Support / Call Hotline", "grounding_call_crisis") {
                        saveAndFinish("call")
                        onNavigateToCrisis()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GroundingDoneButton("💚 I'm Feeling Grounded & Done", "grounding_finish_done") {
                        saveAndFinish("done")
                        onBack()
                    }
                }
            }
        }
    }
}

@Composable
private fun GroundingDoneButton(text: String, tag: String, onClick: () -> Unit) {
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
