package com.jarrodqsimpson.calmloop.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jarrodqsimpson.calmloop.data.local.CalmLoopDatabase
import com.jarrodqsimpson.calmloop.data.model.Book
import com.jarrodqsimpson.calmloop.data.model.BreathingSession
import com.jarrodqsimpson.calmloop.data.model.GroundingSession
import com.jarrodqsimpson.calmloop.data.model.JournalEntry
import com.jarrodqsimpson.calmloop.data.repository.CalmLoopRepository
import com.jarrodqsimpson.calmloop.data.settings.SettingsStore
import com.jarrodqsimpson.calmloop.data.settings.UserSettings
import com.jarrodqsimpson.calmloop.util.StreakCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalmLoopViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalmLoopRepository(CalmLoopDatabase.getDatabase(application))
    private val settingsStore = SettingsStore(application)

    /** Null until preferences have loaded from disk, so the UI can avoid flashing the wrong screen. */
    val settings: StateFlow<UserSettings?> = settingsStore.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val isAiConfigured: Boolean get() = repository.isAiConfigured

    val allEntries: StateFlow<List<JournalEntry>> = repository.allEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allBreathingSessions: StateFlow<List<BreathingSession>> = repository.allBreathingSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allGroundingSessions: StateFlow<List<GroundingSession>> = repository.allGroundingSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allBooks: StateFlow<List<Book>> = repository.allBooks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val journalCount: StateFlow<Int> = repository.journalCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val breathingCount: StateFlow<Int> = repository.breathingCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val groundingCount: StateFlow<Int> = repository.groundingCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val userName: StateFlow<String> = settingsStore.settings.map { it.userName }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings.DEFAULT_USER_NAME
    )

    // Journal processing lives here (not in a composable) so rotating the screen or briefly
    // leaving the app can't cancel the save or save the same entry twice.
    private var pendingTranscription: String? = null
    private var processingJob: Job? = null
    private val _processedEntryId = MutableStateFlow<Long?>(null)
    val processedEntryId: StateFlow<Long?> = _processedEntryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _semanticSearchResult = MutableStateFlow<List<Long>?>(null)
    val semanticSearchResult: StateFlow<List<Long>?> = _semanticSearchResult.asStateFlow()

    private val _patternInsight = MutableStateFlow<String?>(null)
    val patternInsight: StateFlow<String?> = _patternInsight.asStateFlow()

    private var searchJob: Job? = null
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    val currentStreak: StateFlow<Int> = combine(
        allEntries,
        allBreathingSessions,
        allGroundingSessions
    ) { entries, breathing, grounding ->
        StreakCalculator.currentStreak(
            entries.map { it.createdAt } + breathing.map { it.createdAt } + grounding.map { it.createdAt }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        viewModelScope.launch {
            repository.seedBooksIfEmpty()
        }
    }

    fun setUserName(name: String) {
        viewModelScope.launch { settingsStore.setUserName(name) }
    }

    fun acceptDisclaimer(aiEnabled: Boolean) {
        viewModelScope.launch { settingsStore.acceptDisclaimer(aiEnabled) }
    }

    fun setAiEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsStore.setAiEnabled(enabled) }
    }

    /** Queue text from the journal screen for processing; returns false if it is blank. */
    fun submitJournal(transcription: String): Boolean {
        if (transcription.isBlank()) return false
        pendingTranscription = transcription.trim()
        _processedEntryId.value = null
        return true
    }

    /** Starts processing the queued journal text once; repeated calls are ignored. */
    fun processPendingJournal() {
        val text = pendingTranscription ?: return
        if (processingJob?.isActive == true) return
        processingJob = viewModelScope.launch {
            val aiEnabled = settingsStore.settings.first().aiEnabled
            val entry = repository.saveJournalEntry(text, aiEnabled)
            pendingTranscription = null
            _processedEntryId.value = entry.id
        }
    }

    fun consumeProcessedEntry() {
        _processedEntryId.value = null
    }

    suspend fun getEntryById(id: Long): JournalEntry? {
        return repository.getEntryById(id)
    }

    fun saveBreathingSession(rounds: Int, outcome: String) {
        viewModelScope.launch {
            repository.saveBreathingSession(rounds, outcome)
        }
    }

    fun saveGroundingSession(
        a5: List<String>,
        a4: List<String>,
        a3: List<String>,
        a2: List<String>,
        a1: List<String>,
        inputMethod: String,
        outcome: String
    ) {
        viewModelScope.launch {
            repository.saveGroundingSession(a5, a4, a3, a2, a1, inputMethod, outcome)
        }
    }

    fun trackBookClick(bookId: Long, bookTitle: String, outlet: String) {
        viewModelScope.launch {
            repository.trackBookClick(bookId, bookTitle, outlet)
        }
    }

    fun performSearch(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _semanticSearchResult.value = null
            _patternInsight.value = null
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            try {
                val res = repository.performSemanticSearch(query, settingsStore.settings.first().aiEnabled)
                _semanticSearchResult.value = res.matchingIds
                _patternInsight.value = res.patternInsight
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun deleteAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAllUserData()
            settingsStore.clear()
            _semanticSearchResult.value = null
            _patternInsight.value = null
            onComplete()
        }
    }
}
