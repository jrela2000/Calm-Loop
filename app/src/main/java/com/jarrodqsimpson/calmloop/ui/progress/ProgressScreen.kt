package com.jarrodqsimpson.calmloop.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarrodqsimpson.calmloop.ui.components.GlassCard
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.theme.Lavender
import com.jarrodqsimpson.calmloop.ui.theme.Sage
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextMuted
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlue
import com.jarrodqsimpson.calmloop.ui.viewModel.CalmLoopViewModel
import java.util.Calendar

@Composable
fun ProgressScreen(
    viewModel: CalmLoopViewModel,
    onBack: () -> Unit
) {
    val streak by viewModel.currentStreak.collectAsState()
    val journalCount by viewModel.journalCount.collectAsState()
    val breathingCount by viewModel.breathingCount.collectAsState()
    val groundingCount by viewModel.groundingCount.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()
    val allBreathing by viewModel.allBreathingSessions.collectAsState()
    val allGrounding by viewModel.allGroundingSessions.collectAsState()

    val totalSessions = journalCount + breathingCount + groundingCount

    val motivationalTier = when {
        totalSessions == 0 -> "Welcome to CalmLoop! Every journey toward peace starts with a single exhale."
        totalSessions < 5 -> "Great start! You are building mental flexibility and calm habits."
        totalSessions < 15 -> "Consistent practice! Your brain is learning that panic is temporary."
        else -> "Calm Master! You've developed an enduring toolkit for emotional peace."
    }

    // Calculate last 7 days activity
    val last7DaysActive = rememberLast7DaysActivity(allEntries, allBreathing, allGrounding)

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
                        .testTag("progress_back_button")
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
                    text = "Gentle Progress & Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Streak Flame Banner
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("progress_streak_banner"),
                backgroundColor = Sage.copy(alpha = 0.15f),
                borderColor = Sage.copy(alpha = 0.4f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 42.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "$streak Day Calm Streak",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = SlateTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = motivationalTier,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = SlateTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PAST 7 DAYS ACTIVITY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 7-Day Activity Bar Chart
            GlassCard(modifier = Modifier.fillMaxWidth().testTag("activity_chart_card")) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    last7DaysActive.forEach { (dayLabel, count) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height((count * 18).coerceAtMost(80).coerceAtLeast(10).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (count > 0) Sage else Color.White.copy(alpha = 0.5f))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = dayLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SlateTextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CUMULATIVE METRICS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "Journal Entries",
                    count = journalCount,
                    icon = Icons.Default.Book,
                    color = Lavender,
                    modifier = Modifier.weight(1f).testTag("metric_journal")
                )
                MetricTile(
                    title = "Box Breathing",
                    count = breathingCount,
                    icon = Icons.Default.SelfImprovement,
                    color = SoftBlue,
                    modifier = Modifier.weight(1f).testTag("metric_breathing")
                )
                MetricTile(
                    title = "Grounding Games",
                    count = groundingCount,
                    icon = Icons.Default.TouchApp,
                    color = Sage,
                    modifier = Modifier.weight(1f).testTag("metric_grounding")
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Privacy Reassurance Card
            GlassCard(
                modifier = Modifier.fillMaxWidth().testTag("privacy_card")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Sage, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "100% Private & Local",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "Your voice transcripts, ABC model reflections, and grounding logs remain strictly on your Android device.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = SlateTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MetricTile(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "$count",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SlateTextSecondary
            )
        }
    }
}

@Composable
private fun rememberLast7DaysActivity(
    entries: List<com.jarrodqsimpson.calmloop.data.model.JournalEntry>,
    breathing: List<com.jarrodqsimpson.calmloop.data.model.BreathingSession>,
    grounding: List<com.jarrodqsimpson.calmloop.data.model.GroundingSession>
): List<Pair<String, Int>> {
    val list = mutableListOf<Pair<String, Int>>()
    val cal = Calendar.getInstance()
    val sdfKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val sdfDay = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())

    val counts = mutableMapOf<String, Int>()
    entries.forEach { counts[sdfKey.format(it.createdAt)] = (counts[sdfKey.format(it.createdAt)] ?: 0) + 1 }
    breathing.forEach { counts[sdfKey.format(it.createdAt)] = (counts[sdfKey.format(it.createdAt)] ?: 0) + 1 }
    grounding.forEach { counts[sdfKey.format(it.createdAt)] = (counts[sdfKey.format(it.createdAt)] ?: 0) + 1 }

    for (i in 6 downTo 0) {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, -i)
        val k = sdfKey.format(c.time)
        val d = sdfDay.format(c.time)
        list.add(Pair(d, counts[k] ?: 0))
    }
    return list
}
