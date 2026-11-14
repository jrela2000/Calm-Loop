package com.jarrodqsimpson.calmloop.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jarrodqsimpson.calmloop.data.model.AffiliateClick
import com.jarrodqsimpson.calmloop.data.model.Book
import com.jarrodqsimpson.calmloop.data.model.BreathingSession
import com.jarrodqsimpson.calmloop.data.model.GroundingSession
import com.jarrodqsimpson.calmloop.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Long): JournalEntry?

    @Query("SELECT * FROM journal_entries WHERE createdAt >= :startTime AND createdAt <= :endTime ORDER BY createdAt DESC")
    fun getEntriesByDateRange(startTime: Long, endTime: Long): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntry(id: Long)

    @Query("DELETE FROM journal_entries")
    suspend fun deleteAllEntries()

    @Query("SELECT COUNT(*) FROM journal_entries")
    fun getEntryCount(): Flow<Int>
}

@Dao
interface BreathingDao {
    @Query("SELECT * FROM breathing_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<BreathingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: BreathingSession): Long

    @Query("DELETE FROM breathing_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT COUNT(*) FROM breathing_sessions")
    fun getSessionCount(): Flow<Int>
}

@Dao
interface GroundingDao {
    @Query("SELECT * FROM grounding_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<GroundingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GroundingSession): Long

    @Query("DELETE FROM grounding_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT COUNT(*) FROM grounding_sessions")
    fun getSessionCount(): Flow<Int>
}

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY id ASC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)
}

@Dao
interface AffiliateClickDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClick(click: AffiliateClick)

    @Query("DELETE FROM affiliate_clicks")
    suspend fun deleteAllClicks()
}
