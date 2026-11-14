package com.jarrodqsimpson.calmloop.data.repository

import com.jarrodqsimpson.calmloop.data.local.CalmLoopDatabase
import com.jarrodqsimpson.calmloop.data.model.AffiliateClick
import com.jarrodqsimpson.calmloop.data.model.Book
import com.jarrodqsimpson.calmloop.data.model.BreathingSession
import com.jarrodqsimpson.calmloop.data.model.GroundingSession
import com.jarrodqsimpson.calmloop.data.model.JournalEntry
import com.jarrodqsimpson.calmloop.data.remote.AiService
import com.jarrodqsimpson.calmloop.data.remote.SemanticSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CalmLoopRepository(
    private val db: CalmLoopDatabase,
    private val aiService: AiService = AiService()
) {
    private val journalDao = db.journalDao()
    private val breathingDao = db.breathingDao()
    private val groundingDao = db.groundingDao()
    private val bookDao = db.bookDao()
    private val affiliateClickDao = db.affiliateClickDao()

    val allEntries: Flow<List<JournalEntry>> = journalDao.getAllEntries()
    val allBreathingSessions: Flow<List<BreathingSession>> = breathingDao.getAllSessions()
    val allGroundingSessions: Flow<List<GroundingSession>> = groundingDao.getAllSessions()
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    val journalCount: Flow<Int> = journalDao.getEntryCount()
    val breathingCount: Flow<Int> = breathingDao.getSessionCount()
    val groundingCount: Flow<Int> = groundingDao.getSessionCount()

    suspend fun getEntryById(id: Long): JournalEntry? = journalDao.getEntryById(id)

    val isAiConfigured: Boolean get() = aiService.isConfigured

    suspend fun saveJournalEntry(transcription: String, aiEnabled: Boolean): JournalEntry {
        val structured = aiService.reframeEntry(transcription, aiEnabled)
        val entry = JournalEntry(
            transcription = transcription,
            activatingEvent = structured.activatingEvent,
            beliefs = structured.beliefs,
            consequences = structured.consequences,
            aiSynopsis = structured.aiSynopsis,
            aiGenerated = structured.fromAi
        )
        val id = journalDao.insertEntry(entry)
        return entry.copy(id = id)
    }

    suspend fun saveBreathingSession(rounds: Int, outcome: String): Long {
        return breathingDao.insertSession(
            BreathingSession(
                roundsCompleted = rounds,
                outcome = outcome
            )
        )
    }

    suspend fun saveGroundingSession(
        a5: List<String>,
        a4: List<String>,
        a3: List<String>,
        a2: List<String>,
        a1: List<String>,
        inputMethod: String,
        outcome: String
    ): Long {
        return groundingDao.insertSession(
            GroundingSession(
                answers5 = a5.joinToString("; "),
                answers4 = a4.joinToString("; "),
                answers3 = a3.joinToString("; "),
                answers2 = a2.joinToString("; "),
                answers1 = a1.joinToString("; "),
                inputMethod = inputMethod,
                outcome = outcome
            )
        )
    }

    suspend fun trackBookClick(bookId: Long, bookTitle: String, outlet: String) {
        affiliateClickDao.insertClick(
            AffiliateClick(
                bookId = bookId,
                bookTitle = bookTitle,
                outlet = outlet
            )
        )
    }

    suspend fun performSemanticSearch(query: String, aiEnabled: Boolean): SemanticSearchResult {
        val entries = journalDao.getAllEntries().first()
        return aiService.semanticSearch(query, entries, aiEnabled)
    }

    suspend fun deleteAllUserData() {
        journalDao.deleteAllEntries()
        breathingDao.deleteAllSessions()
        groundingDao.deleteAllSessions()
        affiliateClickDao.deleteAllClicks()
    }

    suspend fun seedBooksIfEmpty() {
        if (bookDao.getBookCount() == 0) {
            val initialBooks = listOf(
                Book(
                    title = "Feeling Good: The New Mood Therapy",
                    author = "David D. Burns, M.D.",
                    description = "The clinically proven CBT handbook to conquer anxiety, negative self-talk, and mood dips.",
                    amazonUrl = "https://www.amazon.com/s?k=Feeling+Good%3A+The+New+Mood+Therapy+David+D.+Burns&i=stripbooks",
                    secondaryUrl = "https://bookshop.org/search?keywords=Feeling+Good%3A+The+New+Mood+Therapy+David+D.+Burns",
                    secondaryOutlet = "Bookshop.org",
                    category = "CBT Classic"
                ),
                Book(
                    title = "The Anxiety and Phobia Workbook",
                    author = "Edmund J. Bourne, Ph.D.",
                    description = "Comprehensive CBT exercises for panic attacks, agoraphobia, and generalized anxiety.",
                    amazonUrl = "https://www.amazon.com/s?k=The+Anxiety+and+Phobia+Workbook+Edmund+J.+Bourne&i=stripbooks",
                    secondaryUrl = "https://bookshop.org/search?keywords=The+Anxiety+and+Phobia+Workbook+Edmund+J.+Bourne",
                    secondaryOutlet = "Bookshop.org",
                    category = "Workbook"
                ),
                Book(
                    title = "DARE: The New Way to End Anxiety",
                    author = "Barry McDonagh",
                    description = "A rapid, step-by-step response method to break panic loops and regain calm in seconds.",
                    amazonUrl = "https://www.amazon.com/s?k=DARE%3A+The+New+Way+to+End+Anxiety+Barry+McDonagh&i=stripbooks",
                    secondaryUrl = "https://bookshop.org/search?keywords=DARE%3A+The+New+Way+to+End+Anxiety+Barry+McDonagh",
                    secondaryOutlet = "Bookshop.org",
                    category = "Panic Relief"
                ),
                Book(
                    title = "When Panic Attacks",
                    author = "David D. Burns, M.D.",
                    description = "40 powerful drug-free CBT techniques to eliminate anxiety, worry, and social fears.",
                    amazonUrl = "https://www.amazon.com/s?k=When+Panic+Attacks+David+D.+Burns&i=stripbooks",
                    secondaryUrl = "https://bookshop.org/search?keywords=When+Panic+Attacks+David+D.+Burns",
                    secondaryOutlet = "Bookshop.org",
                    category = "CBT Techniques"
                ),
                Book(
                    title = "Overcoming Unwanted Intrusive Thoughts",
                    author = "Sally M. Winston & Martin N. Seif",
                    description = "Compassionate CBT guide to disarming scary, repetitive thoughts without fighting them.",
                    amazonUrl = "https://www.amazon.com/s?k=Overcoming+Unwanted+Intrusive+Thoughts+Sally+M.+Winston&i=stripbooks",
                    secondaryUrl = "https://bookshop.org/search?keywords=Overcoming+Unwanted+Intrusive+Thoughts+Sally+M.+Winston",
                    secondaryOutlet = "Bookshop.org",
                    category = "Thought Reframing"
                ),
                Book(
                    title = "Retrain Your Brain: CBT in 7 Weeks",
                    author = "Seth J. Gillihan, Ph.D.",
                    description = "A practical 7-week step-by-step CBT program to manage anxiety and depression.",
                    amazonUrl = "https://www.amazon.com/s?k=Retrain+Your+Brain%3A+CBT+in+7+Weeks+Seth+J.+Gillihan&i=stripbooks",
                    secondaryUrl = "https://bookshop.org/search?keywords=Retrain+Your+Brain%3A+CBT+in+7+Weeks+Seth+J.+Gillihan",
                    secondaryOutlet = "Bookshop.org",
                    category = "Guided CBT"
                ),
                Book(
                    title = "The Mindful Way Through Anxiety",
                    author = "Susan M. Orsillo & Lizabeth Roemer",
                    description = "Integrates mindfulness practices with CBT to break free from chronic worry.",
                    amazonUrl = "https://www.amazon.com/s?k=The+Mindful+Way+Through+Anxiety+Susan+M.+Orsillo&i=stripbooks",
                    secondaryUrl = "https://bookshop.org/search?keywords=The+Mindful+Way+Through+Anxiety+Susan+M.+Orsillo",
                    secondaryOutlet = "Bookshop.org",
                    category = "Mindfulness CBT"
                ),
                Book(
                    title = "Rewire Your Anxious Brain",
                    author = "Catherine M. Pittman & Elizabeth M. Karle",
                    description = "Neuroscience-backed CBT tools targeting amygdala and cortex anxiety responses.",
                    amazonUrl = "https://www.amazon.com/s?k=Rewire+Your+Anxious+Brain+Catherine+M.+Pittman&i=stripbooks",
                    secondaryUrl = "https://bookshop.org/search?keywords=Rewire+Your+Anxious+Brain+Catherine+M.+Pittman",
                    secondaryOutlet = "Bookshop.org",
                    category = "Neuroscience & CBT"
                )
            )
            bookDao.insertBooks(initialBooks)
        }
    }
}
