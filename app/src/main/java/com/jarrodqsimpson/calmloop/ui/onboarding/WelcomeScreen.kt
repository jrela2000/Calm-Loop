package com.jarrodqsimpson.calmloop.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarrodqsimpson.calmloop.ui.about.LegalText
import com.jarrodqsimpson.calmloop.ui.components.GlassCard
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.theme.Lavender
import com.jarrodqsimpson.calmloop.ui.theme.Sage
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlue

/** First-launch screen: medical disclaimer, privacy summary and opt-in AI consent. */
@Composable
fun WelcomeScreen(
    aiAvailable: Boolean,
    onAccept: (aiEnabled: Boolean) -> Unit,
    onOpenPrivacy: () -> Unit
) {
    var aiEnabled by rememberSaveable { mutableStateOf(false) }

    ImmersiveBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .testTag("welcome_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome to CalmLoop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = "Breathing, grounding and journaling to help you ride out anxious moments. A few things first:",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = SlateTextSecondary
            )

            InfoCard(
                icon = Icons.Default.HealthAndSafety,
                iconTint = Color(0xFFDC2626),
                title = "Not a substitute for care",
                body = LegalText.MEDICAL_DISCLAIMER
            )

            InfoCard(
                icon = Icons.Default.Lock,
                iconTint = Sage,
                title = "Private by design",
                body = "No account, no ads, no tracking. Your journal and history are stored only on this phone."
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = aiEnabled,
                                enabled = aiAvailable,
                                role = Role.Switch,
                                onValueChange = { aiEnabled = it }
                            )
                            .testTag("welcome_ai_toggle")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Lavender)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI reflections (optional)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SlateTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(checked = aiEnabled, onCheckedChange = null, enabled = aiAvailable)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (aiAvailable) {
                            LegalText.AI_SUMMARY + " You can change this any time in Settings."
                        } else {
                            "AI reflections aren't available in this build. Everything else works fully offline."
                        },
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = SlateTextSecondary
                    )
                }
            }

            TextButton(onClick = onOpenPrivacy, modifier = Modifier.testTag("welcome_privacy_link")) {
                Text("Read the full Privacy Policy & Terms", color = SoftBlue, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onAccept(aiEnabled && aiAvailable) },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("welcome_accept_button")
            ) {
                Text("I understand — continue", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateTextPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, iconTint: Color, title: String, body: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint)
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SlateTextPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(body, fontSize = 13.sp, lineHeight = 19.sp, color = SlateTextSecondary)
        }
    }
}
