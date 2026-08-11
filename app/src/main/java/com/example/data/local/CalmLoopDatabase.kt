package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AffiliateClick
import com.example.data.model.Book
import com.example.data.model.BreathingSession
import com.example.data.model.GroundingSession
import com.example.data.model.JournalEntry

@Database(
    entities = [
        JournalEntry::class,
        BreathingSession::class,
        GroundingSession::class,
        Book::class,
        AffiliateClick::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CalmLoopDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun breathingDao(): BreathingDao
    abstract fun groundingDao(): GroundingDao
    abstract fun bookDao(): BookDao
    abstract fun affiliateClickDao(): AffiliateClickDao

    companion object {
        @Volatile
        private var INSTANCE: CalmLoopDatabase? = null

        fun getDatabase(context: Context): CalmLoopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalmLoopDatabase::class.java,
                    "calmloop_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
