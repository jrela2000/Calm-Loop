package com.jarrodqsimpson.calmloop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jarrodqsimpson.calmloop.data.model.AffiliateClick
import com.jarrodqsimpson.calmloop.data.model.Book
import com.jarrodqsimpson.calmloop.data.model.BreathingSession
import com.jarrodqsimpson.calmloop.data.model.GroundingSession
import com.jarrodqsimpson.calmloop.data.model.JournalEntry

@Database(
    entities = [
        JournalEntry::class,
        BreathingSession::class,
        GroundingSession::class,
        Book::class,
        AffiliateClick::class
    ],
    version = 1,
    exportSchema = true
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
                ).build() // Add Migrations for every schema change; never drop user data.
                INSTANCE = instance
                instance
            }
        }
    }
}
