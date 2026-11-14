package com.jarrodqsimpson.calmloop.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object StreakCalculator {
    /**
     * Consecutive days with at least one activity, ending today (or yesterday, so a streak
     * isn't shown as broken before the user has had a chance to check in today).
     */
    fun currentStreak(
        timestamps: Collection<Long>,
        today: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault()
    ): Int {
        val activeDays = timestamps.mapTo(HashSet()) { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }
        var day = when {
            today in activeDays -> today
            today.minusDays(1) in activeDays -> today.minusDays(1)
            else -> return 0
        }
        var streak = 0
        while (day in activeDays) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }
}
