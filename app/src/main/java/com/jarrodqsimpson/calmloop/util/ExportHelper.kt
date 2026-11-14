package com.jarrodqsimpson.calmloop.util

import android.content.Context
import android.content.Intent
import com.jarrodqsimpson.calmloop.data.model.JournalEntry
import java.text.SimpleDateFormat
import java.util.Locale

object ExportHelper {
    // Built line by line (not trimIndent) so multi-line journal text can't break the layout.
    private fun dateTime(entry: JournalEntry) =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(entry.createdAt)

    private fun synopsisLabel(entry: JournalEntry) =
        if (entry.aiGenerated) "AI-generated synopsis" else "Reflection"

    fun formatAsPlainText(entry: JournalEntry): String = buildString {
        appendLine("CalmLoop CBT Journal Entry")
        appendLine("==========================")
        appendLine("Date: ${dateTime(entry)}")
        appendLine()
        appendLine("[${synopsisLabel(entry).uppercase()}]")
        appendLine(entry.aiSynopsis)
        appendLine()
        appendLine("[A - ACTIVATING EVENT]")
        appendLine(entry.activatingEvent)
        appendLine()
        appendLine("[B - BELIEFS & THOUGHTS]")
        appendLine(entry.beliefs)
        appendLine()
        appendLine("[C - CONSEQUENCES & FEELINGS]")
        appendLine(entry.consequences)
        appendLine()
        appendLine("[ORIGINAL TRANSCRIPTION]")
        append(entry.transcription)
    }

    fun formatAsMarkdown(entry: JournalEntry): String = buildString {
        appendLine("# CalmLoop CBT Journal Entry")
        appendLine("**Date:** ${dateTime(entry)}")
        appendLine()
        appendLine("## ${synopsisLabel(entry)}")
        entry.aiSynopsis.lines().forEach { appendLine("> $it") }
        appendLine()
        appendLine("### A - Activating Event")
        appendLine(entry.activatingEvent)
        appendLine()
        appendLine("### B - Beliefs & Automatic Thoughts")
        appendLine(entry.beliefs)
        appendLine()
        appendLine("### C - Consequences & Emotional Feelings")
        appendLine(entry.consequences)
        appendLine()
        appendLine("---")
        appendLine("#### Original Voice/Text Transcript")
        append(entry.transcription)
    }

    fun formatAsCsv(entry: JournalEntry): String {
        fun escapeCsv(text: String) = "\"${text.replace("\"", "\"\"")}\""
        return "Date,Synopsis,AiGenerated,ActivatingEvent,Beliefs,Consequences,Transcription\n" +
            listOf(
                dateTime(entry), entry.aiSynopsis, entry.aiGenerated.toString(), entry.activatingEvent,
                entry.beliefs, entry.consequences, entry.transcription
            ).joinToString(",") { escapeCsv(it) }
    }

    fun shareEntry(context: Context, entry: JournalEntry) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "CalmLoop CBT Journal - ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(entry.createdAt)}"
            )
            putExtra(Intent.EXTRA_TEXT, formatAsPlainText(entry))
        }
        context.startActivitySafely(Intent.createChooser(intent, "Share Journal Entry"), "No app available to share with.")
    }
}
