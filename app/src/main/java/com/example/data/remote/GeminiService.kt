package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.JournalEntry
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
    val aiSynopsis: String
)

data class SemanticSearchResult(
    val matchingIds: List<Long>,
    val patternInsight: String
)

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY

    suspend fun processJournalEntry(transcription: String): CbtStructureResponse = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Friendly fallback if key is missing
            return@withContext CbtStructureResponse(
                activatingEvent = "Expressed feelings in moment: \"${transcription.take(80)}...\"",
                beliefs = "Automatic thoughts and self-evaluations during anxiety moment.",
                consequences = "Felt emotional tension and physical anxiety responses.",
                aiSynopsis = "You took a brave step by voicing your feelings. Remember that thoughts are mental events, not facts. Take a deep exhale — you are safe right now."
            )
        }

        val prompt = """
            You are a compassionate CBT (Cognitive Behavioral Therapy) assistant.
            Analyze the following anxiety or panic journal transcription and extract the CBT ABC Model:
            1. Activating Event (A): What happened or triggered this moment.
            2. Beliefs (B): Thoughts, interpretations, and self-talk that arose.
            3. Consequences (C): Emotional and physical feelings experienced.
            4. AI Synopsis: A warm, encouraging 2-3 sentence CBT reframing summary.

            Return ONLY a valid JSON object with exact keys:
            {
              "activating_event": "...",
              "beliefs": "...",
              "consequences": "...",
              "ai_synopsis": "..."
            }

            Journal transcription:
            "$transcription"
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    throw Exception("API Error ${response.code}: $bodyStr")
                }

                val jsonResponse = JSONObject(bodyStr)
                val text = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val parsed = JSONObject(text.trim())
                CbtStructureResponse(
                    activatingEvent = parsed.optString("activating_event", "Noted event"),
                    beliefs = parsed.optString("beliefs", "Noted thoughts"),
                    consequences = parsed.optString("consequences", "Noted feelings"),
                    aiSynopsis = parsed.optString("ai_synopsis", "You are doing great taking time to reflect.")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback response on error
            CbtStructureResponse(
                activatingEvent = "Moments described: \"${transcription.take(100)}\"",
                beliefs = "Automatic thoughts during anxiety spike.",
                consequences = "Physical or emotional discomfort.",
                aiSynopsis = "Thank you for sharing your thoughts. Acknowledge your feelings without judgment. Breathing deeply helps your nervous system settle."
            )
        }
    }

    suspend fun semanticSearch(query: String, entries: List<JournalEntry>): SemanticSearchResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || entries.isEmpty()) {
            val filtered = entries.filter {
                it.transcription.contains(query, ignoreCase = true) ||
                it.activatingEvent.contains(query, ignoreCase = true) ||
                it.beliefs.contains(query, ignoreCase = true) ||
                it.aiSynopsis.contains(query, ignoreCase = true)
            }.map { it.id }

            return@withContext SemanticSearchResult(
                matchingIds = filtered,
                patternInsight = if (filtered.isNotEmpty())
                    "Found ${filtered.size} entries matching '$query'. Notice how recurring thoughts soften when examined over time."
                else "No matching entries found for '$query'."
            )
        }

        val entriesArray = JSONArray()
        entries.forEach { entry ->
            entriesArray.put(JSONObject().apply {
                put("id", entry.id)
                put("transcription", entry.transcription)
                put("activating_event", entry.activatingEvent)
                put("beliefs", entry.beliefs)
                put("consequences", entry.consequences)
                put("synopsis", entry.aiSynopsis)
            })
        }

        val prompt = """
            The user is searching their CBT Journal with query: "$query".
            Analyze the provided list of journal entries. Identify entry IDs that match the emotional theme, triggers, or concepts in the search query.
            Also generate a warm, compassionate 2-4 sentence pattern insight explaining the recurring themes or triggers found across matching entries.

            Return ONLY a JSON object with schema:
            {
              "matching_ids": [1, 2],
              "pattern_insight": "Warm explanation..."
            }

            Entries:
            $entriesArray
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                val jsonResponse = JSONObject(bodyStr)
                val text = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val parsed = JSONObject(text.trim())
                val idsJson = parsed.getJSONArray("matching_ids")
                val matchingIds = mutableListOf<Long>()
                for (i in 0 until idsJson.length()) {
                    matchingIds.add(idsJson.getLong(i))
                }

                SemanticSearchResult(
                    matchingIds = matchingIds,
                    patternInsight = parsed.getString("pattern_insight")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val filtered = entries.filter {
                it.transcription.contains(query, ignoreCase = true) ||
                it.beliefs.contains(query, ignoreCase = true)
            }.map { it.id }

            SemanticSearchResult(
                matchingIds = filtered,
                patternInsight = "Showing entries relevant to '$query'."
            )
        }
    }
}
