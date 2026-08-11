package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.model.JournalEntry

object ExportHelper {
    fun formatAsPlainText(entry: JournalEntry): String {
        return """
            CalmLoop CBT Journal Entry
            ==========================
            Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(entry.createdAt)}

            [AI SYNOPSIS]
            ${entry.aiSynopsis}

            [A - ACTIVATING EVENT]
            ${entry.activatingEvent}

            [B - BELIEFS & THOUGHTS]
            ${entry.beliefs}

            [C - CONSEQUENCES & FEELINGS]
            ${entry.consequences}

            [ORIGINAL TRANSCRIPTION]
            ${entry.transcription}
        """.trimIndent()
    }

    fun formatAsMarkdown(entry: JournalEntry): String {
        return """
            # CalmLoop CBT Journal Entry
            **Date:** ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(entry.createdAt)}

            ## AI Synopsis
            > ${entry.aiSynopsis}

            ### A - Activating Event
            ${entry.activatingEvent}

            ### B - Beliefs & Automatic Thoughts
            ${entry.beliefs}

            ### C - Consequences & Emotional Feelings
            ${entry.consequences}

            ---
            #### Original Voice/Text Transcript
            _${entry.transcription}_
        """.trimIndent()
    }

    fun formatAsCsv(entry: JournalEntry): String {
        fun escapeCsv(text: String) = "\"${text.replace("\"", "\"\"")}\""
        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(entry.createdAt)
        return "Date,Synopsis,ActivatingEvent,Beliefs,Consequences,Transcription\n" +
                "${escapeCsv(dateStr)},${escapeCsv(entry.aiSynopsis)},${escapeCsv(entry.activatingEvent)},${escapeCsv(entry.beliefs)},${escapeCsv(entry.consequences)},${escapeCsv(entry.transcription)}"
    }

    fun shareEntry(context: Context, entry: JournalEntry) {
        val text = formatAsPlainText(entry)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "CalmLoop CBT Journal - ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(entry.createdAt)}")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Journal Entry"))
    }
}
