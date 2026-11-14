package com.jarrodqsimpson.calmloop.ui.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarrodqsimpson.calmloop.ui.components.CrisisFooterBar
import com.jarrodqsimpson.calmloop.ui.components.GlassCard
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.components.TopHeaderBar
import com.jarrodqsimpson.calmloop.ui.theme.Lavender
import com.jarrodqsimpson.calmloop.ui.theme.LavenderDark
import com.jarrodqsimpson.calmloop.ui.theme.Sage
import com.jarrodqsimpson.calmloop.ui.theme.SageDark
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextMuted
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlue
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlueDark
import com.jarrodqsimpson.calmloop.ui.viewModel.CalmLoopViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: CalmLoopViewModel,
    onNavigate: (String) -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val streak by viewModel.currentStreak.collectAsState()
    val journalCount by viewModel.journalCount.collectAsState()
    val breathingCount by viewModel.breathingCount.collectAsState()
    val groundingCount by viewModel.groundingCount.collectAsState()

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    // Gentle pulsing animation for breathing orb
    val infiniteTransition = rememberInfiniteTransition(label = "orb_float")
    val orbGlowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    ImmersiveBackground {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopHeaderBar(
                title = "CalmLoop",
                onSettingsClick = { onNavigate("settings") }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Hero Logo & Central Greeting matching screenshot
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Lavender Logo Badge
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFC8C3EE),
                                        Color(0xFFB5AEEC)
                                    )
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                            .shadow(4.dp, CircleShape, spotColor = Color(0x15000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌿", fontSize = 36.sp)
                    }

                    Text(
                        text = "CalmLoop",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "$greeting. You've got this.\nLet's get through this together.",
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = SlateTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.88f)
                    )
                }

                // 3 Immersive Tool Cards matching screenshot
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ImmersiveToolButton(
                        title = "Voice Journal",
                        subtitle = "Say everything, raw and unfiltered",
                        icon = Icons.Default.Mic,
                        iconBgColor = SoftBlue,
                        tag = "tool_voice_journal",
                        onClick = { onNavigate("journal") }
                    )

                    ImmersiveToolButton(
                        title = "Box Breathing",
                        subtitle = "Follow the circle back to calm",
                        icon = Icons.Default.Air,
                        iconBgColor = Lavender,
                        tag = "tool_box_breathing",
                        onClick = { onNavigate("breathing") }
                    )

                    ImmersiveToolButton(
                        title = "Grounding Game",
                        subtitle = "5-4-3-2-1, right here right now",
                        icon = Icons.Default.Widgets,
                        iconBgColor = Sage,
                        tag = "tool_grounding",
                        onClick = { onNavigate("grounding") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Streak Banner Glass Card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .testTag("streak_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "$streak Day Calm Streak",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SlateTextPrimary
                                )
                                Text(
                                    text = "${journalCount + breathingCount + groundingCount} total sessions completed",
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier.clickable { onNavigate("progress") }
                        ) {
                            Text(
                                text = "Progress",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            CrisisFooterBar(
                onCrisisClick = { onNavigate("contact") }
            )
        }
    }
}

@Composable
private fun ImmersiveToolButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(32.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
            .shadow(
                elevation = 2.dp,
                shape = cardShape,
                spotColor = Color(0x0F000000)
            )
            .border(1.dp, Color.White, cardShape)
            .clickable(onClick = onClick),
        shape = cardShape,
        color = Color.White.copy(alpha = 0.75f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = SlateTextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.5.sp,
                    color = SlateTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
