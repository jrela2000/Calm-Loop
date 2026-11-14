import AsyncStorage from '@react-native-async-storage/async-storage';
import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

import { reframeEntry } from '@/lib/ai';
import { newId } from '@/lib/id';

import type {
  BreathingOutcome,
  BreathingSession,
  GroundingSession,
  JournalEntry,
  JournalJob,
  Settings,
} from './types';

/** Bump when the disclaimer or privacy terms change materially so users see them again. */
export const CURRENT_DISCLAIMER_VERSION = 1;
export const DEFAULT_USER_NAME = 'friend';
/** Keep the "untangling" moment on screen long enough to register, even when offline. */
export const MIN_PROCESSING_MS = 1400;

export const defaultSettings: Settings = {
  userName: DEFAULT_USER_NAME,
  acceptedDisclaimerVersion: 0,
  aiEnabled: false,
  soundEnabled: true,
  hapticsEnabled: true,
  theme: 'system',
  trustedContact: null,
};

type Data = {
  settings: Settings;
  entries: JournalEntry[];
  breathing: BreathingSession[];
  grounding: GroundingSession[];
};

type State = Data & {
  hydrated: boolean;
  journalJob: JournalJob;
  updateSettings: (patch: Partial<Settings>) => void;
  acceptDisclaimer: (aiEnabled: boolean) => void;
  /** Saves a journal entry (with AI reflection if enabled). Safe to call once per submit. */
  submitJournal: (text: string) => void;
  resetJournalJob: () => void;
  deleteEntry: (id: string) => void;
  logBreathing: (rounds: number) => string;
  setBreathingOutcome: (id: string, outcome: BreathingOutcome) => void;
  logGrounding: (answers: string[][]) => void;
  importData: (data: Partial<Data>) => { entries: number; sessions: number };
  deleteAllData: () => void;
};

const emptyData = (): Data => ({ settings: { ...defaultSettings }, entries: [], breathing: [], grounding: [] });

function mergeById<T extends { id: string; createdAt: number }>(current: T[], incoming: T[]): T[] {
  const byId = new Map(current.map((x) => [x.id, x]));
  for (const item of incoming) if (!byId.has(item.id)) byId.set(item.id, item);
  return [...byId.values()].sort((a, b) => b.createdAt - a.createdAt);
}

export const useAppStore = create<State>()(
  persist(
    (set, get) => ({
      ...emptyData(),
      hydrated: false,
      journalJob: { status: 'idle' },

      updateSettings: (patch) => set((s) => ({ settings: { ...s.settings, ...patch } })),

      acceptDisclaimer: (aiEnabled) =>
        set((s) => ({
          settings: { ...s.settings, aiEnabled, acceptedDisclaimerVersion: CURRENT_DISCLAIMER_VERSION },
        })),

      submitJournal: (text) => {
        const trimmed = text.trim();
        if (!trimmed || get().journalJob.status === 'processing') return;
        set({ journalJob: { status: 'processing', startedAt: Date.now() } });
        void (async () => {
          const [reframe] = await Promise.all([
            reframeEntry(trimmed, get().settings.aiEnabled),
            new Promise((resolve) => setTimeout(resolve, MIN_PROCESSING_MS)),
          ]);
          const entry: JournalEntry = {
            id: newId(),
            createdAt: Date.now(),
            text: trimmed,
            activatingEvent: reframe.activatingEvent,
            beliefs: reframe.beliefs,
            consequences: reframe.consequences,
            synopsis: reframe.synopsis,
            aiGenerated: reframe.fromAi,
          };
          set((s) => ({ entries: [entry, ...s.entries], journalJob: { status: 'done', entryId: entry.id } }));
        })();
      },

      resetJournalJob: () => set({ journalJob: { status: 'idle' } }),

      deleteEntry: (id) => set((s) => ({ entries: s.entries.filter((e) => e.id !== id) })),

      logBreathing: (rounds) => {
        const session: BreathingSession = { id: newId(), createdAt: Date.now(), rounds };
        set((s) => ({ breathing: [session, ...s.breathing] }));
        return session.id;
      },

      setBreathingOutcome: (id, outcome) =>
        set((s) => ({ breathing: s.breathing.map((b) => (b.id === id ? { ...b, outcome } : b)) })),

      logGrounding: (answers) =>
        set((s) => ({ grounding: [{ id: newId(), createdAt: Date.now(), answers }, ...s.grounding] })),

      importData: (data) => {
        const before = get();
        const entries = mergeById(before.entries, data.entries ?? []);
        const breathing = mergeById(before.breathing, data.breathing ?? []);
        const grounding = mergeById(before.grounding, data.grounding ?? []);
        set({ entries, breathing, grounding });
        return {
          entries: entries.length - before.entries.length,
          sessions: breathing.length + grounding.length - before.breathing.length - before.grounding.length,
        };
      },

      deleteAllData: () => set({ ...emptyData(), journalJob: { status: 'idle' } }),
    }),
    {
      name: 'calmloop-v1',
      version: 1,
      storage: createJSONStorage(() => AsyncStorage),
      partialize: (s): Data => ({ settings: s.settings, entries: s.entries, breathing: s.breathing, grounding: s.grounding }),
      merge: (persisted, current) => {
        const p = (persisted ?? {}) as Partial<Data>;
        return { ...current, ...p, settings: { ...defaultSettings, ...p.settings } };
      },
      onRehydrateStorage: () => () => useAppStore.setState({ hydrated: true }),
    },
  ),
);

export const selectHasAccepted = (s: State) => s.settings.acceptedDisclaimerVersion >= CURRENT_DISCLAIMER_VERSION;

export function activityTimestamps(s: Pick<Data, 'entries' | 'breathing' | 'grounding'>): number[] {
  return [...s.entries, ...s.breathing, ...s.grounding].map((x) => x.createdAt);
}
