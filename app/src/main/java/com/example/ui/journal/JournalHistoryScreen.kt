package com.example.ui.journal

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntry
import com.example.ui.components.GlassCard
import com.example.ui.components.ImmersiveBackground
import com.example.ui.theme.Lavender
import com.example.ui.theme.LavenderDark
import com.example.ui.theme.Sage
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.SoftBlueDark
import com.example.ui.viewModel.CalmLoopViewModel

@Composable
fun JournalHistoryScreen(
    viewModel: CalmLoopViewModel,
    onNavigateToNew: () -> Unit,
    onNavigateToEntry: (Long) -> Unit,
    onBack: () -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val matchingIds by viewModel.semanticSearchResult.collectAsState()
    val patternInsight by viewModel.patternInsight.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    val displayEntries = if (matchingIds != null) {
        val matched = entries.filter { matchingIds!!.contains(it.id) }
        val rest = entries.filter { !matchingIds!!.contains(it.id) }
        matched + rest
    } else {
        entries
    }

    ImmersiveBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .testTag("history_back_button")
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
                        text = "My Journal History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                }

                Button(
                    onClick = onNavigateToNew,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftBlue),
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = SoftBlue.copy(alpha = 0.4f))
                        .testTag("new_journal_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = SlateTextPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateTextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Semantic Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.performSearch(it) },
                placeholder = { Text("Search feelings or themes (e.g. 'work panic')...", fontSize = 13.sp, color = SlateTextMuted) },
                leadingIcon = {
                    if (isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = LavenderDark)
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = LavenderDark)
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.performSearch("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = SlateTextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("semantic_search_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                    focusedBorderColor = SoftBlueDark,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedTextColor = SlateTextPrimary,
                    unfocusedTextColor = SlateTextPrimary
                ),
                singleLine = true
            )

            // Pattern Insight Card
            AnimatedVisibility(visible = !patternInsight.isNullOrBlank()) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .testTag("pattern_insight_card"),
                    backgroundColor = Lavender.copy(alpha = 0.5f),
                    borderColor = LavenderDark.copy(alpha = 0.4f)
                ) {
                    Column {
                        Text("💡 RECURRING PATTERN INSIGHT", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = LavenderDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = patternInsight ?: "",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = SlateTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Journal List
            if (displayEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📓", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No entries matching '$searchQuery'" else "Your journal is empty.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SlateTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Record or type your first journal entry to start CBT reframing.",
                            fontSize = 13.sp,
                            color = SlateTextSecondary
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onNavigateToNew,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftBlue)
                        ) {
                            Text("Record First Entry", color = SlateTextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayEntries) { entry ->
                        val isMatched = matchingIds?.contains(entry.id) == true
                        JournalHistoryItemCard(
                            entry = entry,
                            isMatched = isMatched,
                            onClick = { onNavigateToEntry(entry.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JournalHistoryItemCard(
    entry: JournalEntry,
    isMatched: Boolean,
    onClick: () -> Unit
) {
    val dateFormatted = java.text.SimpleDateFormat("MMM dd, yyyy · HH:mm", java.util.Locale.getDefault())
        .format(entry.createdAt)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("journal_history_item_${entry.id}")
            .then(
                if (isMatched) Modifier.border(2.dp, Lavender, RoundedCornerShape(28.dp)) else Modifier
            ),
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormatted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateTextSecondary
                )

                if (isMatched) {
                    Surface(shape = CircleShape, color = Lavender.copy(alpha = 0.3f)) {
                        Text("Semantic Match", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateTextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.aiSynopsis.ifBlank { entry.transcription },
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 3,
                fontWeight = FontWeight.Medium,
                color = SlateTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AbcTag("A: Trigger", SoftBlue)
                AbcTag("B: Thoughts", Lavender)
                AbcTag("C: Feelings", Sage)
            }
        }
    }
}

@Composable
private fun AbcTag(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.25f)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = SlateTextPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
