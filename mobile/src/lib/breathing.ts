export const PHASE_SECONDS = 4;
const PHASE_COUNT = 4; // inhale, hold, exhale, rest

/** Box-breathing timer state, advanced by a once-a-second `tick`. */
export type BreathingState = {
  status: 'ready' | 'running' | 'paused' | 'done';
  rounds: number;
  round: number; // 1-based
  phase: number; // index into PHASES
  left: number; // seconds left in phase
};

export type BreathingAction = { type: 'start' } | { type: 'pause' } | { type: 'resume' } | { type: 'tick' } | { type: 'rounds'; n: number } | { type: 'finish' };

export const initialBreathing: BreathingState = { status: 'ready', rounds: 4, round: 1, phase: 0, left: PHASE_SECONDS };

export function breathingReducer(s: BreathingState, a: BreathingAction): BreathingState {
  switch (a.type) {
    case 'rounds':
      return { ...s, rounds: a.n };
    case 'start':
      return { ...initialBreathing, rounds: s.rounds, status: 'running' };
    case 'pause':
      return s.status === 'running' ? { ...s, status: 'paused' } : s;
    case 'resume':
      // Resume at the start of the current phase so the visual and the count stay in sync.
      return s.status === 'paused' ? { ...s, status: 'running', left: PHASE_SECONDS } : s;
    case 'finish':
      return { ...s, status: 'done' };
    case 'tick': {
      if (s.status !== 'running') return s;
      if (s.left > 1) return { ...s, left: s.left - 1 };
      if (s.phase < PHASE_COUNT - 1) return { ...s, phase: s.phase + 1, left: PHASE_SECONDS };
      if (s.round < s.rounds) return { ...s, round: s.round + 1, phase: 0, left: PHASE_SECONDS };
      return { ...s, status: 'done' };
    }
  }
}
