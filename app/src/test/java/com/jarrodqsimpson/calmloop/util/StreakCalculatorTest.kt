package com.jarrodqsimpson.calmloop.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class StreakCalculatorTest {
    private val zone: ZoneId = ZoneOffset.UTC
    private val today = LocalDate.of(2026, 11, 14)

    private fun at(date: LocalDate, hour: Int = 12) =
        date.atTime(hour, 0).atZone(zone).toInstant().toEpochMilli()

    private fun streak(vararg days: LocalDate) =
        StreakCalculator.currentStreak(days.map { at(it) }, today, zone)

    @Test
    fun `no activity means no streak`() = assertEquals(0, streak())

    @Test
    fun `activity today counts as one`() = assertEquals(1, streak(today))

    @Test
    fun `consecutive days ending today`() =
        assertEquals(3, streak(today, today.minusDays(1), today.minusDays(2)))

    @Test
    fun `streak ending yesterday is still alive`() =
        assertEquals(2, streak(today.minusDays(1), today.minusDays(2)))

    @Test
    fun `gap breaks the streak`() =
        assertEquals(1, streak(today, today.minusDays(2), today.minusDays(3)))

    @Test
    fun `last activity two days ago means streak is broken`() =
        assertEquals(0, streak(today.minusDays(2)))

    @Test
    fun `multiple activities on one day count once`() {
        val stamps = listOf(at(today, 8), at(today, 20), at(today.minusDays(1), 9))
        assertEquals(2, StreakCalculator.currentStreak(stamps, today, zone))
    }
}
