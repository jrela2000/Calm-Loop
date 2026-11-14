import { CURRENT_DISCLAIMER_VERSION, defaultSettings, MIN_PROCESSING_MS, selectHasAccepted, useAppStore } from '@/store/app-store';

const reset = () =>
  useAppStore.setState({ settings: { ...defaultSettings }, entries: [], breathing: [], grounding: [], journalJob: { status: 'idle' } });

beforeEach(() => {
  reset();
  jest.useFakeTimers();
});
afterEach(() => jest.useRealTimers());

async function finishProcessing() {
  await jest.advanceTimersByTimeAsync(MIN_PROCESSING_MS + 10);
}

describe('app store', () => {
  it('starts with AI off and the disclaimer unaccepted', () => {
    const s = useAppStore.getState();
    expect(s.settings.aiEnabled).toBe(false);
    expect(selectHasAccepted(s)).toBe(false);
  });

  it('records disclaimer acceptance and the AI choice', () => {
    useAppStore.getState().acceptDisclaimer(true);
    const s = useAppStore.getState();
    expect(s.settings.acceptedDisclaimerVersion).toBe(CURRENT_DISCLAIMER_VERSION);
    expect(s.settings.aiEnabled).toBe(true);
  });

  it('saves a journal entry offline with honest labelling, once', async () => {
    useAppStore.getState().submitJournal('  My chest is tight  ');
    useAppStore.getState().submitJournal('duplicate tap');
    expect(useAppStore.getState().journalJob.status).toBe('processing');
    await finishProcessing();
    const { entries, journalJob } = useAppStore.getState();
    expect(entries).toHaveLength(1);
    expect(entries[0]).toMatchObject({ text: 'My chest is tight', aiGenerated: false });
    expect(journalJob).toEqual({ status: 'done', entryId: entries[0].id });
  });

  it('ignores blank journal text', () => {
    useAppStore.getState().submitJournal('   ');
    expect(useAppStore.getState().journalJob.status).toBe('idle');
  });

  it('logs breathing with an outcome', () => {
    const id = useAppStore.getState().logBreathing(4);
    useAppStore.getState().setBreathingOutcome(id, 'better');
    expect(useAppStore.getState().breathing[0]).toMatchObject({ rounds: 4, outcome: 'better' });
  });

  it('imports without overwriting or duplicating', () => {
    useAppStore.getState().logGrounding([['mug']]);
    const existing = useAppStore.getState().grounding[0];
    const result = useAppStore.getState().importData({
      grounding: [existing, { id: 'new', createdAt: 5, answers: [] }],
      breathing: [{ id: 'b', createdAt: 1, rounds: 2 }],
    });
    expect(result).toEqual({ entries: 0, sessions: 2 });
    expect(useAppStore.getState().grounding).toHaveLength(2);
  });

  it('deletes everything, including settings', () => {
    useAppStore.getState().acceptDisclaimer(false);
    useAppStore.getState().updateSettings({ userName: 'Sam' });
    useAppStore.getState().logBreathing(3);
    useAppStore.getState().deleteAllData();
    const s = useAppStore.getState();
    expect(s.breathing).toHaveLength(0);
    expect(s.settings).toEqual(defaultSettings);
  });
});
