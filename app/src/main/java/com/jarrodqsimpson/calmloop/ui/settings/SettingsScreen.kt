package com.jarrodqsimpson.calmloop.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarrodqsimpson.calmloop.BuildConfig
import com.jarrodqsimpson.calmloop.ui.components.GlassCard
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.theme.Lavender
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextMuted
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlue
import com.jarrodqsimpson.calmloop.ui.viewModel.CalmLoopViewModel

@Composable
fun SettingsScreen(
    viewModel: CalmLoopViewModel,
    onOpenPrivacy: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentName by viewModel.userName.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val aiEnabled = settings?.aiEnabled == true
    val aiAvailable = viewModel.isAiConfigured
    var nameInput by remember(currentName) { mutableStateOf(currentName) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    ImmersiveBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
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
                        .testTag("settings_back_button")
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

                Text(
                    text = "App Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Name Preference
            GlassCard(modifier = Modifier.fillMaxWidth().testTag("settings_user_name_card")) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SoftBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Your Preferred Name", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SlateTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("name_input_field"),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                                focusedBorderColor = SoftBlue,
                                unfocusedBorderColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.setUserName(nameInput.ifBlank { "Calm Friend" })
                                Toast.makeText(context, "Name updated", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftBlue),
                            modifier = Modifier.testTag("save_name_button")
                        ) {
                            Text("Save", color = SlateTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI reflections (opt-in)
            GlassCard(modifier = Modifier.fillMaxWidth().testTag("ai_status_card")) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = aiEnabled,
                                enabled = aiAvailable,
                                role = Role.Switch,
                                onValueChange = { viewModel.setAiEnabled(it) }
                            )
                            .testTag("settings_ai_toggle")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Lavender)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "AI reflections",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SlateTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(checked = aiEnabled, onCheckedChange = null, enabled = aiAvailable)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when {
                            !aiAvailable -> "Not available in this build. Journal entries get on-device reflection prompts instead."
                            aiEnabled -> "On. Journal text is sent to CalmLoop's relay and Google Gemini to create a summary. Nothing is stored there."
                            else -> "Off. Nothing you write leaves this device."
                        },
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = SlateTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy & terms
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("privacy_terms_card"),
                onClick = onOpenPrivacy
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = SoftBlue)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Privacy Policy & Terms", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SlateTextPrimary)
                        Text(
                            "CalmLoop is a self-help tool, not medical care.",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = SlateTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Delete Data Danger Zone
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("danger_zone_card"),
                backgroundColor = Color(0xFFFEF2F2).copy(alpha = 0.8f),
                borderColor = Color(0xFFFECACA)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete All Application Data", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF991B1B))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Permanently erases all journal entries, breathing and grounding history, and settings from this device.",
                        fontSize = 12.sp,
                        color = Color(0xFFB91C1C)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.testTag("delete_data_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Delete All My Data", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("CalmLoop ${BuildConfig.VERSION_NAME}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateTextSecondary)
                Text("Mobile-first CBT companion app for panic & anxiety", fontSize = 11.sp, color = SlateTextMuted)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete All Local Data?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure? All your voice journals, reframed ABC notes, and session history will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllData {
                            showDeleteDialog = false
                            Toast.makeText(context, "All data deleted", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    }
                ) {
                    Text("Permanently Delete", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
