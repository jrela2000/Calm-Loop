package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.AtmosphereBgCenter
import com.example.ui.theme.AtmosphereBgEnd
import com.example.ui.theme.AtmosphereBgStart
import com.example.ui.theme.IceBlueGlow
import com.example.ui.theme.Lavender
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.Sage
import com.example.ui.theme.SageDark
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.SoftBlueDark
import com.example.ui.theme.WarmWhite

/**
 * Ambient Immersive Background wrapper with radial & linear gradient matching the reference design
 */
@Composable
fun ImmersiveBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background != WarmWhite
    
    val bgBrush = if (!isDark) {
        Brush.verticalGradient(
            colors = listOf(
                AtmosphereBgStart,
                AtmosphereBgCenter,
                AtmosphereBgEnd
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFF0F172A),
                Color(0xFF1E1B4B).copy(alpha = 0.6f),
                Color(0xFF0F172A)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        content()
    }
}

/**
 * Backdrop Glass Card component with rounded corners, translucent surface & light borders
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.80f),
    borderColor: Color = Color.White.copy(alpha = 0.95f),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardShape = RoundedCornerShape(32.dp)
    Surface(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = cardShape,
                spotColor = Color(0x10000000),
                ambientColor = Color(0x08000000)
            )
            .border(1.dp, borderColor, cardShape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = cardShape,
        color = backgroundColor
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

/**
 * Immersive Top Header Bar with settings toggle matching screenshot layout
 */
@Composable
fun TopHeaderBar(
    title: String,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .testTag("settings_button")
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.85f))
                .border(1.dp, Color.White, CircleShape)
                .shadow(2.dp, CircleShape, spotColor = Color(0x0D000000))
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = SlateTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Emergency Crisis Footer Callout Card
 */
@Composable
fun CrisisFooterBar(
    onCrisisClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable(onClick = onCrisisClick)
            .testTag("crisis_footer_bar"),
        color = Color(0xFFF1F5F9).copy(alpha = 0.8f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EMERGENCY SUPPORT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = SlateTextMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Crisis Support",
                    tint = Color(0xFFE11D48),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "In crisis? Call or text ",
                    fontSize = 12.sp,
                    color = SlateTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "988",
                    fontSize = 12.sp,
                    color = SlateTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " anytime.",
                    fontSize = 12.sp,
                    color = SlateTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Floating Glass Bottom Navigation Bar with pill container
 */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color(0x20A8C8E8),
                    ambientColor = Color(0x15000000)
                )
                .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            color = Color.White.copy(alpha = 0.85f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItemPill(
                    label = "Home",
                    icon = if (currentRoute == "home") Icons.Default.Home else Icons.Outlined.Home,
                    selected = currentRoute == "home",
                    activeColor = Color(0xFF3B82F6),
                    tag = "nav_home",
                    onClick = { onNavigate("home") }
                )

                NavItemPill(
                    label = "My Journal",
                    icon = if (currentRoute.startsWith("journal")) Icons.Default.Book else Icons.Outlined.Book,
                    selected = currentRoute == "journal_history" || currentRoute == "journal",
                    activeColor = Color(0xFF3B82F6),
                    tag = "nav_journal",
                    onClick = { onNavigate("journal_history") }
                )

                NavItemPill(
                    label = "Progress",
                    icon = if (currentRoute == "progress") Icons.Default.ShowChart else Icons.Outlined.ShowChart,
                    selected = currentRoute == "progress",
                    activeColor = Color(0xFF3B82F6),
                    tag = "nav_progress",
                    onClick = { onNavigate("progress") }
                )
            }
        }
    }
}

@Composable
private fun NavItemPill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    activeColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag(tag)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) activeColor else SlateTextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) activeColor else SlateTextMuted
        )
    }
}
