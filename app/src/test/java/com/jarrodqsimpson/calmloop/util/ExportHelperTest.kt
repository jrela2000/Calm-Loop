package com.jarrodqsimpson.calmloop.util

import com.jarrodqsimpson.calmloop.data.model.JournalEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportHelperTest {
    private val entry = JournalEntry(
        id = 1,
        transcription = "Line one\nline \"two\", with comma",
        activatingEvent = "Meeting",
        beliefs = "I'll fail",
        consequences = "Racing heart",
        aiSynopsis = "Be kind to yourself.",
        aiGenerated = true,
        createdAt = 0L
    )

    @Test
    fun `plain text keeps multi-line journal text unindented`() {
        val text = ExportHelper.formatAsPlainText(entry)
        assertTrue(text.startsWith("CalmLoop CBT Journal Entry\n"))
        assertTrue(text.endsWith("Line one\nline \"two\", with comma"))
        assertTrue("[AI-GENERATED SYNOPSIS]" in text)
    }

    @Test
    fun `offline entries are not labelled as AI`() {
        val text = ExportHelper.formatAsMarkdown(entry.copy(aiGenerated = false))
        assertTrue("## Reflection" in text)
        assertTrue("AI" !in text.substringBefore("### A"))
    }

    @Test
    fun `csv escapes quotes and keeps one record`() {
        val csv = ExportHelper.formatAsCsv(entry)
        val header = csv.substringBefore('\n')
        assertEquals("Date,Synopsis,AiGenerated,ActivatingEvent,Beliefs,Consequences,Transcription", header)
        assertTrue(csv.endsWith("\"Line one\nline \"\"two\"\", with comma\""))
        assertTrue("\"true\"" in csv)
    }
}
