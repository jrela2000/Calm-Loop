import { currentStreak, localDayKey, longestStreak } from '@/lib/streak';

const day = (y: number, m: number, d: number, h = 12) => new Date(y, m - 1, d, h).getTime();
const today = new Date(2026, 10, 14, 15);

describe('streaks', () => {
  it('is zero with no activity', () => expect(currentStreak([], today)).toBe(0));
  it('counts consecutive days ending today', () =>
    expect(currentStreak([day(2026, 11, 14), day(2026, 11, 13), day(2026, 11, 12)], today)).toBe(3));
  it('keeps a streak that ended yesterday alive', () =>
    expect(currentStreak([day(2026, 11, 13), day(2026, 11, 12)], today)).toBe(2));
  it('breaks on a gap', () => expect(currentStreak([day(2026, 11, 14), day(2026, 11, 12)], today)).toBe(1));
  it('is broken when the last activity was two days ago', () => expect(currentStreak([day(2026, 11, 12)], today)).toBe(0));
  it('counts several activities on one day once', () =>
    expect(currentStreak([day(2026, 11, 14, 8), day(2026, 11, 14, 22)], today)).toBe(1));
  it('crosses month boundaries', () =>
    expect(currentStreak([day(2026, 11, 1), day(2026, 10, 31)], new Date(2026, 10, 1, 9))).toBe(2));
  it('finds the longest run', () =>
    expect(longestStreak([day(2026, 1, 1), day(2026, 1, 2), day(2026, 1, 3), day(2026, 1, 10), day(2026, 1, 11)])).toBe(3));
  it('formats local day keys', () => expect(localDayKey(day(2026, 3, 5))).toBe('2026-03-05'));
});
