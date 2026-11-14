/** YYYY-MM-DD in the device's local time zone. */
export function localDayKey(timestamp: number): string {
  const d = new Date(timestamp);
  const m = `${d.getMonth() + 1}`.padStart(2, '0');
  const day = `${d.getDate()}`.padStart(2, '0');
  return `${d.getFullYear()}-${m}-${day}`;
}

function previousDay(date: Date): Date {
  const d = new Date(date);
  d.setDate(d.getDate() - 1);
  return d;
}

/**
 * Consecutive days with at least one activity, ending today, or yesterday so a streak
 * isn't shown as broken before the user has had a chance to check in today.
 */
export function currentStreak(timestamps: number[], now: Date = new Date()): number {
  const days = new Set(timestamps.map(localDayKey));
  let cursor = new Date(now);
  if (!days.has(localDayKey(cursor.getTime()))) {
    cursor = previousDay(cursor);
    if (!days.has(localDayKey(cursor.getTime()))) return 0;
  }
  let streak = 0;
  while (days.has(localDayKey(cursor.getTime()))) {
    streak++;
    cursor = previousDay(cursor);
  }
  return streak;
}

export function longestStreak(timestamps: number[]): number {
  const days = [...new Set(timestamps.map(localDayKey))].sort();
  let best = 0;
  let run = 0;
  let prev: string | null = null;
  for (const day of days) {
    if (prev) {
      const [y, m, d] = prev.split('-').map(Number);
      const expected = localDayKey(new Date(y, m - 1, d + 1).getTime());
      run = day === expected ? run + 1 : 1;
    } else {
      run = 1;
    }
    best = Math.max(best, run);
    prev = day;
  }
  return best;
}
