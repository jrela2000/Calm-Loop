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

const dayNumber = (key: string) => {
  const [y, m, d] = key.split('-').map(Number);
  return Math.round(Date.UTC(y, m - 1, d) / 86400000);
};

/**
 * Gentle streak: the number of days you showed up in the current run. One rest day never breaks a
 * run; two missed days in a row end it. Rest days aren't counted, and nothing is shown as "lost"
 * before today is over.
 */
export function currentStreak(timestamps: number[], now: Date = new Date()): number {
  const days = new Set(timestamps.map(localDayKey));
  let cursor = new Date(now);
  let count = 0;
  let missed = 0;
  for (let i = 0; i < 3660; i++) {
    if (days.has(localDayKey(cursor.getTime()))) {
      count++;
      missed = 0;
    } else if (++missed >= 2) {
      break;
    }
    cursor = previousDay(cursor);
  }
  return count;
}

/** The best run ever, by the same rule: active days, with single rest days allowed in between. */
export function longestStreak(timestamps: number[]): number {
  const days = [...new Set(timestamps.map(localDayKey))].map(dayNumber).sort((a, b) => a - b);
  let best = 0;
  let run = 0;
  let prev: number | null = null;
  for (const day of days) {
    run = prev !== null && day - prev <= 2 ? run + 1 : 1;
    best = Math.max(best, run);
    prev = day;
  }
  return best;
}
