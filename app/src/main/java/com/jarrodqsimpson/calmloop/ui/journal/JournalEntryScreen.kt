package com.jarrodqsimpson.calmloop.ui.journal

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.jarrodqsimpson.calmloop.data.model.Book
import com.jarrodqsimpson.calmloop.data.model.JournalEntry
import com.jarrodqsimpson.calmloop.ui.components.GlassCard
import com.jarrodqsimpson.calmloop.ui.components.ImmersiveBackground
import com.jarrodqsimpson.calmloop.ui.theme.Lavender
import com.jarrodqsimpson.calmloop.ui.theme.Sage
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextMuted
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextPrimary
import com.jarrodqsimpson.calmloop.ui.theme.SlateTextSecondary
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlue
import com.jarrodqsimpson.calmloop.ui.theme.SoftBlueDark
import com.jarrodqsimpson.calmloop.ui.viewModel.CalmLoopViewModel
import com.jarrodqsimpson.calmloop.util.startActivitySafely
import com.jarrodqsimpson.calmloop.util.ExportHelper

@Composable
fun JournalEntryScreen(
    entryId: Long,
    viewModel: CalmLoopViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var entry by remember { mutableStateOf<JournalEntry?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showOriginal by remember { mutableStateOf(false) }
    val books by viewModel.allBooks.collectAsState()

    LaunchedEffect(entryId) {
        entry = viewModel.getEntryById(entryId)
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SoftBlue)
        }
        return
    }

    val currentEntry = entry
    if (currentEntry == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Entry not found.", color = SlateTextPrimary)
        }
        return
    }

    val dateFormatted = java.text.SimpleDateFormat("EEEE, MMM dd, yyyy · HH:mm", java.util.Locale.getDefault())
        .format(currentEntry.createdAt)

    ImmersiveBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header Bar
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
                        .testTag("entry_back_button")
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
                    text = "CBT Reframed Entry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )

                IconButton(
                    onClick = { ExportHelper.shareEntry(context, currentEntry) },
                    modifier = Modifier
                        .testTag("share_entry_button")
                        .size(42.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = SoftBlueDark
                    )
                }
            }

            Text(
                text = dateFormatted,
                fontSize = 12.sp,
                color = SlateTextSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Primary AI Synopsis Card (Lavender)
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("ai_synopsis_card"),
                backgroundColor = Lavender.copy(alpha = 0.15f),
                borderColor = Lavender.copy(alpha = 0.4f)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentEntry.aiGenerated) "AI-GENERATED CBT SYNOPSIS" else "REFLECTION",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = SlateTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentEntry.aiSynopsis,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (currentEntry.aiGenerated) {
                            "Generated by AI (Google Gemini). It can be wrong and isn't professional advice."
                        } else {
                            "AI reflections were off or unavailable, so CalmLoop added reflection prompts instead. Your entry stayed on this device."
                        },
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = SlateTextSecondary,
                        modifier = Modifier.testTag("ai_disclosure_label")
                    )
                }
            }

            Text(
                text = "THE CBT ABC MODEL BREAKDOWN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // A - Activating Event (Soft Blue)
            AbcModelCard(
                letter = "A",
                title = "Activating Event (Trigger)",
                content = currentEntry.activatingEvent.ifBlank { "Not specified" },
                badgeColor = SoftBlue,
                tag = "abc_card_a"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // B - Beliefs (Lavender)
            AbcModelCard(
                letter = "B",
                title = "Beliefs & Automatic Thoughts",
                content = currentEntry.beliefs.ifBlank { "Not specified" },
                badgeColor = Lavender,
                tag = "abc_card_b"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // C - Consequences (Sage)
            AbcModelCard(
                letter = "C",
                title = "Consequences & Physical/Emotional Feelings",
                content = currentEntry.consequences.ifBlank { "Not specified" },
                badgeColor = Sage,
                tag = "abc_card_c"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Collapsible Original Transcription
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                    .clickable { showOriginal = !showOriginal }
                    .testTag("toggle_original_transcription"),
                color = Color.White.copy(alpha = 0.7f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Original Voice/Text Transcription",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextPrimary
                        )
                        Icon(
                            imageVector = if (showOriginal) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = SlateTextPrimary
                        )
                    }

                    AnimatedVisibility(
                        visible = showOriginal,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentEntry.transcription,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recommended Reading Section
            Text(
                text = "RECOMMENDED CBT READING",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Independent picks. Links open the store's search in your browser; CalmLoop earns nothing from them.",
                fontSize = 11.sp,
                color = SlateTextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            for (book in books.take(4)) {
                BookItemCard(
                    book = book,
                    onOutletClick = { outlet, url ->
                        viewModel.trackBookClick(book.id, book.title, outlet)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivitySafely(intent, "No browser found to open this link.")
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Export Actions
            Text(
                text = "EXPORT JOURNAL ENTRY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = SlateTextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val text = ExportHelper.formatAsPlainText(currentEntry)
                        copyToClipboard(context, "Journal Entry", text)
                        Toast.makeText(context, "Copied Plain Text to Clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).testTag("export_text_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Text", fontSize = 12.sp, color = SlateTextPrimary, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val md = ExportHelper.formatAsMarkdown(currentEntry)
                        copyToClipboard(context, "Journal Entry MD", md)
                        Toast.makeText(context, "Copied Markdown to Clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).testTag("export_md_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Markdown", fontSize = 12.sp, color = SlateTextPrimary, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val csv = ExportHelper.formatAsCsv(currentEntry)
                        copyToClipboard(context, "Journal Entry CSV", csv)
                        Toast.makeText(context, "Copied CSV to Clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).testTag("export_csv_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("CSV", fontSize = 12.sp, color = SlateTextPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AbcModelCard(
    letter: String,
    title: String,
    content: String,
    badgeColor: Color,
    tag: String
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        backgroundColor = Color.White.copy(alpha = 0.75f)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SlateTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SlateTextSecondary
            )
        }
    }
}

@Composable
private fun BookItemCard(
    book: Book,
    onOutletClick: (String, String) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "by ${book.author}",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = SoftBlue.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = book.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateTextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = book.description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = SlateTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onOutletClick("Amazon", book.amazonUrl) },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlue,
                        contentColor = SlateTextPrimary
                    )
                ) {
                    Text("Amazon", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = SlateTextPrimary, modifier = Modifier.size(12.dp))
                }

                OutlinedButton(
                    onClick = { onOutletClick(book.secondaryOutlet, book.secondaryUrl) },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(book.secondaryOutlet, fontSize = 11.sp, color = SlateTextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = SlateTextPrimary, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

private fun copyToClipboard(context: android.content.Context, label: String, text: String) {
    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}
