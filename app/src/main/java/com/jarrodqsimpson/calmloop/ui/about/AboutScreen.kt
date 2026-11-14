package com.jarrodqsimpson.calmloop.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarrodqsimpson.calmloop.BuildConfig
import com.jarrodqsimpson.calmloop.ui.components.GlassCard
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextMuted
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary

/** Privacy policy, terms of use and disclaimer, readable offline. */
@Composable
fun AboutScreen(onBack: () -> Unit) {
    ImmersiveBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .testTag("about_screen")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("about_back_button")
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SlateTextPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Privacy & Terms",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary,
                    modifier = Modifier.semantics { heading() }
                )
            }

            Text(
                text = "Last updated ${LegalText.LAST_UPDATED}",
                fontSize = 12.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LegalSection(title = "Important", sections = listOf("Not a substitute for care" to LegalText.MEDICAL_DISCLAIMER))
            LegalSection(title = "Privacy Policy", sections = LegalText.privacySections)
            LegalSection(title = "Terms of Use", sections = LegalText.termsSections)

            Text(
                text = "CalmLoop ${BuildConfig.VERSION_NAME}",
                fontSize = 12.sp,
                color = SlateTextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun LegalSection(title: String, sections: List<Pair<String, String>>) {
    GlassCard(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = SlateTextPrimary,
                modifier = Modifier.semantics { heading() }
            )
            sections.forEach { (heading, body) ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = heading, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateTextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = body, fontSize = 13.sp, lineHeight = 19.sp, color = SlateTextSecondary)
            }
        }
    }
}
