package com.jarrodqsimpson.calmloop.ui.contact

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarrodqsimpson.calmloop.ui.components.GlassCard
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextMuted
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlue
import com.jarrodqsimpson.calmloop.util.startActivitySafely

@Composable
fun ContactCrisisScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    fun dialNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        context.startActivitySafely(intent, "No phone app found. Please call $number from another phone.")
    }

    fun sendSms(number: String, text: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:$number")).apply {
            putExtra("sms_body", text)
        }
        context.startActivitySafely(intent, "No messaging app found. Please text $text to $number from another phone.")
    }

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
                        .testTag("contact_back_button")
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
                    text = "Crisis & Support Lines",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFFFEF2F2).copy(alpha = 0.85f),
                borderColor = Color(0xFFFECACA)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "You don't have to do this alone.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF991B1B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Free, confidential 24/7 support is one tap away.",
                            fontSize = 13.sp,
                            color = Color(0xFFB91C1C)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "In immediate danger? Call 911 or your local emergency number now.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PERSONAL SUPPORT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Call a Friend / Family
            ContactOptionCard(
                title = "Call a Friend or Family Member",
                subtitle = "Open your contacts to reach out to someone you trust.",
                icon = Icons.Default.People,
                badge = "Trusted Circle",
                tag = "contact_call_friend",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people"))
                    context.startActivitySafely(intent, "Couldn't open your contacts app.")
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "24/7 PROFESSIONAL CRISIS LINES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 988 Lifeline
            ContactOptionCard(
                title = "988 Suicide & Crisis Lifeline",
                subtitle = "Call or text 988 anytime. Free, confidential, 24/7 support in English & Spanish.",
                icon = Icons.Default.Call,
                badge = "Call or Text 988",
                tag = "contact_988",
                onClick = { dialNumber("988") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Crisis Text Line
            ContactOptionCard(
                title = "Crisis Text Line",
                subtitle = "Text HOME to 741741 to connect with a crisis counselor over SMS.",
                icon = Icons.AutoMirrored.Filled.Message,
                badge = "Text 741741",
                tag = "contact_text_line",
                onClick = { sendSms("741741", "HOME") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // SAMHSA Helpline
            ContactOptionCard(
                title = "SAMHSA National Helpline",
                subtitle = "1-800-662-4357. Treatment referral and information service.",
                icon = Icons.Default.Call,
                badge = "1-800-662-4357",
                tag = "contact_samhsa",
                onClick = { dialNumber("18006624357") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // NAMI Helpline
            ContactOptionCard(
                title = "NAMI Helpline",
                subtitle = "1-800-950-NAMI. Mental health support, resources, and guidance.",
                icon = Icons.Default.Call,
                badge = "1-800-950-6264",
                tag = "contact_nami",
                onClick = { dialNumber("18009506264") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "OUTSIDE THE UNITED STATES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ContactOptionCard(
                title = "Find a helpline in your country",
                subtitle = "The numbers above are US-only. Find A Helpline lists free, confidential support lines worldwide.",
                icon = Icons.Default.Public,
                badge = "findahelpline.com",
                tag = "contact_international",
                onClick = {
                    context.startActivitySafely(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://findahelpline.com")),
                        "No browser found. Visit findahelpline.com from another device."
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBack,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = SoftBlue.copy(alpha = 0.4f))
                    .testTag("contact_return_button")
            ) {
                Text("Return to CalmLoop", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateTextPrimary)
            }
        }
    }
}

@Composable
private fun ContactOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.75f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEF2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SlateTextPrimary)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, lineHeight = 17.sp, color = SlateTextSecondary)
            }
        }
    }
}
