package com.example.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalmLoopDatabase
import com.example.data.model.Book
import com.example.data.model.BreathingSession
import com.example.data.model.GroundingSession
import com.example.data.model.JournalEntry
import com.example.data.repository.CalmLoopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class CalmLoopViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalmLoopRepository(CalmLoopDatabase.getDatabase(application))

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

    private val _userName = MutableStateFlow("Calm Friend")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _semanticSearchResult = MutableStateFlow<List<Long>?>(null)
    val semanticSearchResult: StateFlow<List<Long>?> = _semanticSearchResult.asStateFlow()

    private val _patternInsight = MutableStateFlow<String?>(null)
    val patternInsight: StateFlow<String?> = _patternInsight.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    val currentStreak: StateFlow<Int> = combine(
        allEntries,
        allBreathingSessions,
        allGroundingSessions
    ) { entries, breathing, grounding ->
        calculateStreak(entries, breathing, grounding)
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
        _userName.value = name
    }

    suspend fun saveJournalEntry(transcription: String): JournalEntry {
        return repository.saveJournalEntry(transcription)
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
        if (query.isBlank()) {
            _semanticSearchResult.value = null
            _patternInsight.value = null
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            try {
                val res = repository.performSemanticSearch(query)
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
            _semanticSearchResult.value = null
            _patternInsight.value = null
            onComplete()
        }
    }

    private fun calculateStreak(
        entries: List<JournalEntry>,
        breathing: List<BreathingSession>,
        grounding: List<GroundingSession>
    ): Int {
        val timestamps = mutableSetOf<String>()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

        entries.forEach { timestamps.add(sdf.format(it.createdAt)) }
        breathing.forEach { timestamps.add(sdf.format(it.createdAt)) }
        grounding.forEach { timestamps.add(sdf.format(it.createdAt)) }

        if (timestamps.isEmpty()) return 0

        var streak = 0
        val cal = Calendar.getInstance()
        var dateStr = sdf.format(cal.time)

        if (timestamps.contains(dateStr)) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
            dateStr = sdf.format(cal.time)
        } else {
            // Check if active yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
            dateStr = sdf.format(cal.time)
            if (!timestamps.contains(dateStr)) return 0
        }

        while (timestamps.contains(dateStr)) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
            dateStr = sdf.format(cal.time)
        }

        return streak
    }
}
