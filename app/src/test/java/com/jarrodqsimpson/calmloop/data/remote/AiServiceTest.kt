package com.jarrodqsimpson.calmloop.data.remote

import com.jarrodqsimpson.calmloop.data.model.JournalEntry
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AiServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: AiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        service = AiService(endpoint = "http://localhost:${server.port}")
    }

    @After
    fun tearDown() = server.shutdown()

    private val entries = listOf(
        JournalEntry(id = 1, transcription = "Panic before a work presentation"),
        JournalEntry(id = 2, transcription = "Calm walk in the park"),
        JournalEntry(id = 3, transcription = "Work emails at night made me panic")
    )

    @Test
    fun `reframe uses proxy response when AI is enabled`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{"activating_event":"A","beliefs":"B","consequences":"C","ai_synopsis":"S"}"""
            )
        )
        val result = service.reframeEntry("I felt panicky", aiEnabled = true)

        assertTrue(result.fromAi)
        assertEquals("S", result.aiSynopsis)
        val request = server.takeRequest()
        assertEquals("/v1/reframe", request.path)
        assertEquals("I felt panicky", JSONObject(request.body.readUtf8()).getString("transcription"))
    }

    @Test
    fun `reframe sends nothing when AI is disabled`() = runTest {
        val result = service.reframeEntry("private thoughts", aiEnabled = false)

        assertFalse(result.fromAi)
        assertEquals(0, server.requestCount)
    }

    @Test
    fun `reframe falls back to on-device prompts on server error`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))
        val result = service.reframeEntry("text", aiEnabled = true)

        assertFalse(result.fromAi)
        assertEquals(AiService.offlineReframe(), result)
    }

    @Test
    fun `reframe falls back on malformed response`() = runTest {
        server.enqueue(MockResponse().setBody("not json"))
        assertFalse(service.reframeEntry("text", aiEnabled = true).fromAi)
    }

    @Test
    fun `unconfigured endpoint never calls out`() = runTest {
        val unconfigured = AiService(endpoint = "")
        assertFalse(unconfigured.isConfigured)
        assertFalse(unconfigured.reframeEntry("text", aiEnabled = true).fromAi)
    }

    @Test
    fun `search parses proxy ids`() = runTest {
        server.enqueue(MockResponse().setBody("""{"matching_ids":[1,3],"pattern_insight":"Work triggers panic."}"""))
        val result = service.semanticSearch("work panic", entries, aiEnabled = true)

        assertTrue(result.fromAi)
        assertEquals(listOf(1L, 3L), result.matchingIds)
        assertEquals("/v1/search", server.takeRequest().path)
    }

    @Test
    fun `keyword search matches all words in any order`() {
        val result = AiService.keywordSearch("panic work", entries)
        assertEquals(listOf(1L, 3L), result.matchingIds)
        assertFalse(result.fromAi)
    }

    @Test
    fun `keyword search with no hits says so`() {
        val result = AiService.keywordSearch("beach", entries)
        assertTrue(result.matchingIds.isEmpty())
        assertTrue(result.patternInsight.startsWith("No entries"))
    }
}
