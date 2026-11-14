package com.jarrodqsimpson.calmloop.data.remote

import com.jarrodqsimpson.calmloop.BuildConfig
import com.jarrodqsimpson.calmloop.data.model.JournalEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CbtStructureResponse(
    val activatingEvent: String,
    val beliefs: String,
    val consequences: String,
    val aiSynopsis: String,
    /** False when the result is the on-device fallback (AI off, not configured, or unreachable). */
    val fromAi: Boolean
)

data class SemanticSearchResult(
    val matchingIds: List<Long>,
    val patternInsight: String,
    val fromAi: Boolean
)

/**
 * Talks to the CalmLoop AI proxy (see /proxy), which holds the AI provider key server-side.
 * No API key ships inside the app. When AI is disabled, unconfigured or unreachable, every
 * call falls back to an on-device result and reports `fromAi = false`.
 */
class AiService(
    private val endpoint: String = BuildConfig.AI_PROXY_URL,
    private val client: OkHttpClient = defaultClient
) {
    val isConfigured: Boolean
        get() = endpoint.startsWith("https://") || endpoint.startsWith("http://localhost") ||
            endpoint.startsWith("http://127.0.0.1")

    suspend fun reframeEntry(transcription: String, aiEnabled: Boolean): CbtStructureResponse =
        withContext(Dispatchers.IO) {
            if (!aiEnabled || !isConfigured) return@withContext offlineReframe()
            try {
                val body = JSONObject().put("transcription", transcription.take(MAX_TRANSCRIPTION_CHARS))
                parseReframe(post("/v1/reframe", body))
            } catch (e: Exception) {
                offlineReframe()
            }
        }

    suspend fun semanticSearch(
        query: String,
        entries: List<JournalEntry>,
        aiEnabled: Boolean
    ): SemanticSearchResult = withContext(Dispatchers.IO) {
        if (!aiEnabled || !isConfigured || entries.isEmpty()) {
            return@withContext keywordSearch(query, entries)
        }
        try {
            val entriesJson = JSONArray()
            entries.take(MAX_SEARCH_ENTRIES).forEach { entry ->
                entriesJson.put(
                    JSONObject()
                        .put("id", entry.id)
                        .put("text", entry.searchableText().take(MAX_ENTRY_CHARS))
                )
            }
            val body = JSONObject().put("query", query.take(200)).put("entries", entriesJson)
            parseSearch(post("/v1/search", body))
        } catch (e: Exception) {
            keywordSearch(query, entries)
        }
    }

    private fun post(path: String, body: JSONObject): String {
        val request = Request.Builder()
            .url(endpoint.trimEnd('/') + path)
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IllegalStateException("AI proxy returned ${response.code}")
            return text
        }
    }

    companion object {
        private const val MAX_TRANSCRIPTION_CHARS = 6_000
        private const val MAX_SEARCH_ENTRIES = 100
        private const val MAX_ENTRY_CHARS = 1_000

        private val defaultClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        internal fun parseReframe(json: String): CbtStructureResponse {
            val parsed = JSONObject(json)
            val synopsis = parsed.getString("ai_synopsis")
            require(synopsis.isNotBlank()) { "Empty synopsis" }
            return CbtStructureResponse(
                activatingEvent = parsed.optString("activating_event"),
                beliefs = parsed.optString("beliefs"),
                consequences = parsed.optString("consequences"),
                aiSynopsis = synopsis,
                fromAi = true
            )
        }

        internal fun parseSearch(json: String): SemanticSearchResult {
            val parsed = JSONObject(json)
            val ids = parsed.getJSONArray("matching_ids")
            return SemanticSearchResult(
                matchingIds = (0 until ids.length()).map { ids.getLong(it) },
                patternInsight = parsed.optString("pattern_insight"),
                fromAi = true
            )
        }

        /** Honest on-device result: reflection prompts rather than a pretend AI analysis. */
        internal fun offlineReframe() = CbtStructureResponse(
            activatingEvent = "What was happening just before this feeling started? Where were you, and what were you doing?",
            beliefs = "What thoughts or predictions went through your mind? Try writing the scariest one down, then ask: what is the evidence for and against it?",
            consequences = "What did you feel in your body and emotions? Naming a sensation (tight chest, racing heart) can make it feel more manageable.",
            aiSynopsis = "You took a brave step by putting this into words. Thoughts are mental events, not facts. Take a slow breath out — you are safe right now.",
            fromAi = false
        )

        internal fun keywordSearch(query: String, entries: List<JournalEntry>): SemanticSearchResult {
            val terms = query.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
            val matches = entries.filter { entry ->
                val text = entry.searchableText().lowercase()
                terms.isNotEmpty() && terms.all { it in text }
            }.map { it.id }
            return SemanticSearchResult(
                matchingIds = matches,
                patternInsight = if (matches.isNotEmpty()) {
                    "Found ${matches.size} ${if (matches.size == 1) "entry" else "entries"} mentioning '$query'."
                } else {
                    "No entries mention '$query'."
                },
                fromAi = false
            )
        }

        private fun JournalEntry.searchableText() =
            listOf(transcription, activatingEvent, beliefs, consequences, aiSynopsis).joinToString(" ")
    }
}
