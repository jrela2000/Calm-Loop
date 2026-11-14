import { breathingReducer, initialBreathing, PHASE_SECONDS } from '@/lib/breathing';

const ticks = (s: typeof initialBreathing, n: number) => {
  for (let i = 0; i < n; i++) s = breathingReducer(s, { type: 'tick' });
  return s;
};

describe('box breathing timer', () => {
  it('does not tick until started', () => expect(ticks(initialBreathing, 5)).toEqual(initialBreathing));

  it('moves through the four phases, then the next round', () => {
    let s = breathingReducer(initialBreathing, { type: 'start' });
    s = ticks(s, PHASE_SECONDS);
    expect(s).toMatchObject({ phase: 1, round: 1, left: PHASE_SECONDS });
    s = ticks(s, PHASE_SECONDS * 3);
    expect(s).toMatchObject({ phase: 0, round: 2 });
  });

  it('finishes after the chosen number of rounds', () => {
    let s = breathingReducer(initialBreathing, { type: 'rounds', n: 3 });
    s = breathingReducer(s, { type: 'start' });
    s = ticks(s, 3 * 4 * PHASE_SECONDS);
    expect(s.status).toBe('done');
    expect(s.round).toBe(3);
  });

  it('pauses, and resumes at the start of the current phase', () => {
    let s = ticks(breathingReducer(initialBreathing, { type: 'start' }), 2);
    s = breathingReducer(s, { type: 'pause' });
    expect(ticks(s, 3)).toEqual(s);
    s = breathingReducer(s, { type: 'resume' });
    expect(s).toMatchObject({ status: 'running', left: PHASE_SECONDS, phase: 0 });
  });
});
